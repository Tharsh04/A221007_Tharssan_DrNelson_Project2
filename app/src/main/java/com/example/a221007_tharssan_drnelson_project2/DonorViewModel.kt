package com.example.a221007_tharssan_drnelson_project2

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.a221007_tharssan_drnelson_project2.data.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class DonorViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val favoriteDao = db.favoriteDao()
    private val apiService = FoodBankApiService.create()
    private val firestore = FirebaseFirestore.getInstance()

    var currentUser by mutableStateOf<User?>(null)
    val donationHistory = mutableStateListOf<FoodDonation>()
    var lastDonation by mutableStateOf<FoodDonation?>(null)

    var globalMealsCount by mutableIntStateOf(25200)
        private set

    // Uses immutable snapshot states to force layout updates on the main thread
    var globalRecentDonations by mutableStateOf<List<FoodDonation>>(emptyList())
        private set

    var remoteFoodBanks = mutableStateListOf<OsmElement>()
        private set
    var isApiLoading by mutableStateOf(false)
        private set

    var currentResolvedAddress by mutableStateOf("Locating your coordinates...")
        private set

    var cloudStockLevels = mutableStateOf<Map<String, FirebaseFoodStock>>(emptyMap())
        private set

    private val _localFavorites = MutableStateFlow<List<FavoriteFoodBank>>(emptyList())
    val localFavorites: StateFlow<List<FavoriteFoodBank>> = _localFavorites.asStateFlow()

    init {
        observeLocalDatabase()
        listenToCloudStock()
        listenToGlobalStatistics()
        listenToGlobalRecentDonations()
    }

    private fun listenToGlobalStatistics() {
        firestore.collection("app_statistics").document("totals")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val cloudCount = snapshot.getLong("totalMealsShared")
                    if (cloudCount != null) { globalMealsCount = cloudCount.toInt() }
                }
            }
    }

    private fun listenToGlobalRecentDonations() {
        firestore.collection("donations")
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let { querySnapshot ->
                    val newList = mutableListOf<FoodDonation>()
                    for (doc in querySnapshot.documents) {
                        val donation = doc.toObject(FoodDonation::class.java)
                        if (donation != null) { newList.add(donation) }
                    }
                    globalRecentDonations = newList // Force structural UI re-composition update
                }
            }
    }

    fun register(user: User, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                // 1. Cross-examine Local Room Cache Constraints
                val localEmailCheck = favoriteDao.getUserByEmail(user.email)
                val localMatricCheck = favoriteDao.getUserByMatric(user.matric)

                if (localEmailCheck != null) {
                    onResult("Gmail address already exists.")
                    return@launch
                }
                if (localMatricCheck != null) {
                    onResult("Matric number already exists.")
                    return@launch
                }

                // 2. Cross-examine Cloud Firestore Keys
                val cloudEmailDoc = firestore.collection("users").document(user.email).get().await()
                if (cloudEmailDoc.exists()) {
                    onResult("Gmail address already exists.")
                    return@launch
                }

                val cloudMatricQuery = firestore.collection("users").whereEqualTo("matric", user.matric).get().await()
                if (!cloudMatricQuery.isEmpty) {
                    onResult("Matric number already exists.")
                    return@launch
                }

                // 3. Persist details if valid
                favoriteDao.insertUserLocal(user)
                firestore.collection("users").document(user.email).set(user)
                onResult(null)

            } catch (e: Exception) {
                e.printStackTrace()
                onResult("Network error. Please try again.")
            }
        }
    }

    fun login(email: String, pass: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val localUser = favoriteDao.getUserByEmail(email)
            if (localUser != null && localUser.password == pass) {
                currentUser = localUser
                loadOfflineDonationHistory(email)
                onResult(true)
            } else {
                try {
                    val cloudDoc = firestore.collection("users").document(email).get().await()
                    if (cloudDoc.exists()) {
                        val cloudUser = cloudDoc.toObject(User::class.java)
                        if (cloudUser != null && cloudUser.password == pass) {
                            favoriteDao.insertUserLocal(cloudUser)
                            currentUser = cloudUser
                            loadOfflineDonationHistory(email)
                            onResult(true)
                            return@launch
                        }
                    }
                } catch (e: Exception) { e.printStackTrace() }
                onResult(false)
            }
        }
    }

    fun logout() {
        currentUser = null
        donationHistory.clear()
    }

    fun updateProfile(newName: String, newEmail: String, newMatric: String) {
        currentUser?.let { user ->
            val updatedUser = user.copy(name = newName, email = newEmail, matric = newMatric)
            currentUser = updatedUser
            viewModelScope.launch {
                favoriteDao.insertUserLocal(updatedUser)
                firestore.collection("users").document(updatedUser.email).set(updatedUser)
            }
        }
    }

    // Campaign/Package Form Submission Pipeline
    fun addDonation(donation: FoodDonation) {
        donationHistory.add(donation)
        lastDonation = donation
        viewModelScope.launch {
            try {
                favoriteDao.insertDonationLocal(donation)
                firestore.collection("donations").document(donation.id).set(donation)
                firestore.collection("app_statistics").document("totals")
                    .update("totalMealsShared", FieldValue.increment(donation.totalItemCount.toLong()))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadOfflineDonationHistory(email: String) {
        viewModelScope.launch {
            favoriteDao.getLocalDonationHistory(email).collect { historyList ->
                donationHistory.clear()
                donationHistory.addAll(historyList)
            }
        }
    }

    private fun observeLocalDatabase() {
        viewModelScope.launch {
            favoriteDao.getAllFavorites().collect { items -> _localFavorites.value = items }
        }
    }

    fun fetchRegionalFoodBanks(lat: Double, lon: Double) {
        viewModelScope.launch {
            isApiLoading = true
            try {
                val geocodeResponse = apiService.reverseGeocode(lat, lon)
                currentResolvedAddress = geocodeResponse.display_name.ifBlank { "Unmapped Location Point" }

                val dynamicQuery = "[out:json];node(around:50000,$lat,$lon)[\"amenity\"=\"food_bank\"];out;"
                val response = apiService.getNearbyFoodBanks(dynamicQuery)

                remoteFoodBanks.clear()
                if (response.elements.isNotEmpty()) {
                    remoteFoodBanks.addAll(response.elements)
                }

                remoteFoodBanks.addAll(listOf(
                    OsmElement(448001L, 3.0567, 101.5854, mapOf("name" to "448 Food Bank Selangor (Subang Hub)")),
                    OsmElement(101002L, 3.0721, 101.5832, mapOf("name" to "Food Aid Foundation Malaysia")),
                    OsmElement(202003L, 2.9512, 101.8451, mapOf("name" to "Yayasan Food Bank Malaysia (HQ Semenyih)"))
                ))
            } catch (e: Exception) {
                e.printStackTrace()
                currentResolvedAddress = "Kajang, Selangor"
                remoteFoodBanks.clear()
                remoteFoodBanks.addAll(listOf(
                    OsmElement(448001L, 3.0567, 101.5854, mapOf("name" to "448 Food Bank Selangor (Subang Hub)")),
                    OsmElement(101002L, 3.0721, 101.5832, mapOf("name" to "Food Aid Foundation Malaysia")),
                    OsmElement(202003L, 2.9512, 101.8451, mapOf("name" to "Yayasan Food Bank Malaysia (HQ Semenyih)"))
                ))
            } finally { isApiLoading = false }
        }
    }

    fun toggleLocalFavorite(element: OsmElement) {
        viewModelScope.launch {
            val label = element.tags?.get("name") ?: "Registered Community Pantry"
            val existing = _localFavorites.value.find { it.id == element.id.toString() }

            if (existing != null) {
                favoriteDao.deleteFavorite(existing)
                firestore.collection("favorites").document(element.id.toString()).delete()
            } else {
                val newFav = FavoriteFoodBank(element.id.toString(), label, element.lat, element.lon, System.currentTimeMillis())
                favoriteDao.insertFavorite(newFav)
                firestore.collection("favorites").document(newFav.id).set(newFav)
            }
        }
    }

    private fun listenToCloudStock() {
        firestore.collection("food_bank_stocks")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let { querySnapshot ->
                    val updatedMap = mutableMapOf<String, FirebaseFoodStock>()
                    for (doc in querySnapshot.documents) {
                        val stockItem = doc.toObject(FirebaseFoodStock::class.java)
                        if (stockItem != null) { updatedMap[stockItem.foodBankId] = stockItem }
                    }
                    cloudStockLevels.value = updatedMap
                }
            }
    }


    fun pushCloudDirectDonation(bankId: String, name: String, updatedCount: Int, items: List<String>, donation: FoodDonation) {
        viewModelScope.launch {
            try {

                val payload = FirebaseFoodStock(bankId, name, updatedCount, items, "Active Node")
                firestore.collection("food_bank_stocks").document(bankId).set(payload)


                favoriteDao.insertDonationLocal(donation)
                donationHistory.add(donation)
                lastDonation = donation


                firestore.collection("donations").document(donation.id).set(donation)


                firestore.collection("app_statistics").document("totals")
                    .update("totalMealsShared", FieldValue.increment(donation.totalItemCount.toLong()))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}