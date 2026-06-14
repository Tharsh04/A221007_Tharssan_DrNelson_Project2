package com.example.a221007_tharssan_drnelson_project2.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_food_banks ORDER BY addedTimestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteFoodBank>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(bank: FavoriteFoodBank)

    @Delete
    suspend fun deleteFavorite(bank: FavoriteFoodBank)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserLocal(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    // FIXED: Added lookup functionality for your registration validation checks
    @Query("SELECT * FROM users WHERE matric = :matric LIMIT 1")
    suspend fun getUserByMatric(matric: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonationLocal(donation: FoodDonation)

    @Query("SELECT * FROM donations WHERE donorEmail = :email ORDER BY date DESC")
    fun getLocalDonationHistory(email: String): Flow<List<FoodDonation>>
}