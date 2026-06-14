package com.example.a221007_tharssan_drnelson_project2.data

import android.content.Context
import androidx.room.*

@Database(entities = [FavoriteFoodBank::class, User::class, FoodDonation::class], version = 3, exportSchema = false)
@TypeConverters(MapConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "feedforward_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}