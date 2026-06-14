package com.example.a221007_tharssan_drnelson_project2

import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a221007_tharssan_drnelson_project2.data.FoodDonation
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("MissingPermission")
@Composable
fun FoodBankLocatorScreen(viewModel: DonorViewModel, onNavigateToPayment: (String, FoodDonation) -> Unit) {
    val context = LocalContext.current
    val clientLauncher = remember { LocationServices.getFusedLocationProviderClient(context) }

    var localLat by remember { mutableDoubleStateOf(2.9935) }
    var localLon by remember { mutableDoubleStateOf(101.7874) }

    val localFavorites by viewModel.localFavorites.collectAsState()
    val cloudStocks = viewModel.cloudStockLevels.value
    val apiListSize = viewModel.remoteFoodBanks.size

    LaunchedEffect(Unit) {
        viewModel.fetchRegionalFoodBanks(localLat, localLon)
        clientLauncher.lastLocation.addOnSuccessListener { loc: Location? ->
            loc?.let {
                localLat = it.latitude
                localLon = it.longitude
                viewModel.fetchRegionalFoodBanks(localLat, localLon)
            }
        }
    }

    Column(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth().background(Color(0xFFEA580C)).padding(24.dp)) {
            Column {
                Text("Food Bank Locator", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("📍 Resolved Address: ${viewModel.currentResolvedAddress}", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
                Text("GPS Telemetry Matrix: ($localLat, $localLon)", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
            }
        }

        if (viewModel.isApiLoading && apiListSize == 0) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFEA580C))
            }
        } else {
            LazyColumn(Modifier.weight(1f).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                item {
                    Text("Verified Regional Supply Hubs (50km Network Circle)", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 13.sp)
                }

                items(viewModel.remoteFoodBanks) { element ->
                    val name = element.tags?.get("name") ?: "Registered Community Pantry"
                    val isSavedLocally = localFavorites.any { it.id == element.id.toString() }

                    val stockItem = cloudStocks[element.id.toString()]
                    val currentCount = stockItem?.availablePackagesCount ?: 75
                    val standardItemsList = stockItem?.itemsAvailableList ?: listOf("Rice 5kg", "Cooking Oil 1kg", "Canned Baked Beans", "Condensed Milk")

                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.weight(1f)) {
                                    Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Coordinates: ${element.lat}, ${element.lon}", fontSize = 11.sp, color = Color.Gray)
                                }
                                IconButton(onClick = { viewModel.toggleLocalFavorite(element) }) {
                                    Icon(
                                        imageVector = if (isSavedLocally) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Room Persistence Storage",
                                        tint = Color(0xFFEA580C)
                                    )
                                }
                            }

                            HorizontalDivider(Modifier.padding(vertical = 8.dp))

                            Text("Current Stock Inventory Manifest:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEA580C))
                            standardItemsList.forEach { item ->
                                Text("• $item", fontSize = 12.sp, color = Color.DarkGray, modifier = Modifier.padding(start = 4.dp))
                            }

                            Spacer(Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CloudDone, null, tint = Color(0xFF0284C7), modifier = Modifier.size(16.dp))
                                Text(" Live Cloud Stack: $currentCount Units Cached", fontSize = 12.sp, color = Color(0xFF0284C7), fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    val transactionId = UUID.randomUUID().toString()
                                    val currentDonor = viewModel.currentUser

                                    val donationPayload = FoodDonation(
                                        id = transactionId,
                                        donorName = currentDonor?.name ?: "Anonymous Helper",
                                        donorEmail = currentDonor?.email ?: "helper@gmail.com",
                                        charityName = name,
                                        packageType = "Direct Hub Care Provision",
                                        itemsJson = com.google.gson.Gson().toJson(standardItemsList),
                                        totalItemCount = 5,
                                        date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                                    )

                                    // FIXED: Passes the full donation entity payload directly to your updated
                                    // pushCloudDirectDonation method which handles local and Firestore state synchronization.
                                    viewModel.pushCloudDirectDonation(
                                        bankId = element.id.toString(),
                                        name = name,
                                        updatedCount = currentCount + 5,
                                        items = standardItemsList,
                                        donation = donationPayload
                                    )

                                    onNavigateToPayment("RM 50.00", donationPayload)
                                },
                                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                                colors = ButtonDefaults.buttonColors(Color(0xFF0284C7))
                            ) {
                                Icon(Icons.Default.VolunteerActivism, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Donate Provision Package (RM 50.00)", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}