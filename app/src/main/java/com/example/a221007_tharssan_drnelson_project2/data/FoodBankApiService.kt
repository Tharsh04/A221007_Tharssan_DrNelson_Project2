package com.example.a221007_tharssan_drnelson_project2.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface FoodBankApiService {
    @POST("api/interpreter")
    @FormUrlEncoded
    suspend fun getNearbyFoodBanks(@Field("data") query: String): OverpassResponse

    // --- Reverse Geocoding Address Decoder Gateway ---
    @GET("https://nominatim.openstreetmap.org/reverse")
    suspend fun reverseGeocode(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("format") format: String = "json",
        @Header("User-Agent") userAgent: String = "FeedForwardMobileAppSoftwareEngineeringProject"
    ): NominatimResponse

    companion object {
        fun create(): FoodBankApiService {
            return Retrofit.Builder()
                .baseUrl("https://overpass-api.de/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FoodBankApiService::class.java)
        }
    }
}