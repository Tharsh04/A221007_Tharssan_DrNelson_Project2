package com.example.a221007_tharssan_drnelson_project2.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// =============================================================================
// --- PILLAR 1: ROOM LOCAL PERSISTENCE ENTITIES ---
// =============================================================================

@Entity(tableName = "users")
data class User(
    @PrimaryKey val email: String,
    val name: String,
    val matric: String,
    val username: String,
    val password: String
)

@Entity(tableName = "favorite_food_banks")
data class FavoriteFoodBank(
    @PrimaryKey val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val addedTimestamp: Long
)

@Entity(tableName = "donations")
data class FoodDonation(
    @PrimaryKey val id: String = "",
    val donorName: String = "",
    val donorEmail: String = "",
    val charityName: String = "",
    val packageType: String = "",
    val itemsJson: String = "", // Holds item arrays safely as a flat text string
    val date: String = "",
    val totalItemCount: Int = 0
)

// --- Type Converter Engine Utilities for Room Complex Data Handling ---
class MapConverters {
    @TypeConverter
    fun fromMap(map: Map<String, Int>?): String {
        return Gson().toJson(map ?: emptyMap<String, Int>())
    }

    @TypeConverter
    fun toMap(value: String?): Map<String, Int> {
        val mapType = object : TypeToken<Map<String, Int>>() {}.type
        return Gson().fromJson(value ?: "{}", mapType)
    }
}

// =============================================================================
// --- PILLAR 2: REST WEB API RESPONSE CONTRACTS (Overpass & Nominatim) ---
// =============================================================================

// Overpass API Spatial Geolocation Structure
data class OverpassResponse(val elements: List<OsmElement>)
data class OsmElement(
    val id: Long,
    val lat: Double,
    val lon: Double,
    val tags: Map<String, String>?
)

// Nominatim OpenStreetMap Reverse Geocoding Structure
data class NominatimResponse(
    val display_name: String = "",
    val address: AddressDetails? = null
)

data class AddressDetails(
    val city: String = "",
    val state: String = "",
    val quarter: String = ""
)

// =============================================================================
// --- PILLAR 3: CLOUD INTERACTION DATA PLUGS (Firebase Firestore) ---
// =============================================================================

data class FirebaseFoodStock(
    val foodBankId: String = "",
    val foodBankName: String = "",
    val availablePackagesCount: Int = 0,
    val itemsAvailableList: List<String> = emptyList(), // Array backing your real item stocks manifest
    val operationalStatus: String = ""
)

// =============================================================================
// --- STATIC CAMPAIGN DATA LABELS (SDG 2: Zero Hunger Baseline) ---
// =============================================================================

data class CharityCampaign(
    val id: String,
    val name: String,
    val description: String,
    val fullMission: String,
    val factoid: String,
    val wishlist: List<String>,
    val isUrgent: Boolean,
    val organizationName: String,
    val mealsShared: Int,
    val imageUrl: String
)

val charityCampaigns = listOf(
    CharityCampaign(
        "1", "Community Food Bank", "Providing essential food supplies to families in need.",
        "We aim to distribute over 500 food packages weekly to B40 families.",
        "Did you know? Over 20% of urban households in Malaysia struggle to afford three balanced meals a day.",
        listOf("Rice", "Cooking Oil", "Canned Sardines"), true,
        "Community Food Bank", 12500, "https://ergsy.com/sites/default/files/styles/video_thumb/public/video_thumbs/info-image-How_can_I_support_my_local_food_bank_-thumb-1776783927.jpg.webp"
    ),
    CharityCampaign(
        "2", "Children's Nutrition", "Ensuring children have access to nutritious meals.",
        "Our goal is to provide growth-milk and protein-rich meals to 200 children daily.",
        "SDG Fact: Proper nutrition in the first 1,000 days of a child's life is critical for brain development.",
        listOf("Infant Formula", "Oats", "High-Protein Biscuits"), false,
        "Children's Welfare Foundation", 8500, "https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?w=400"
    ),
    CharityCampaign(
        "3", "Emergency Relief Fund", "Rapid response to natural disasters.",
        "We maintain a 24/7 supply chain to deliver ready-to-eat meals to displaced families.",
        "Impact: During the monsoon season, the need for dry, shelf-stable food increases by 400%.",
        listOf("Instant Noodles", "Bottled Water", "Dry Crackers"), true,
        "Disaster Response Team", 4200, "https://kinlocate.wordpress.com/wp-content/uploads/2013/04/argentina-flood.jpg"
    )
)