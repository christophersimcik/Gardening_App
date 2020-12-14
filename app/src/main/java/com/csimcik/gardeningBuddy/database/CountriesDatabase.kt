package com.csimcik.gardeningBuddy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.csimcik.gardeningBuddy.converter.GeoConverters
import com.csimcik.gardeningBuddy.dao.CountriesDao
import com.csimcik.gardeningBuddy.models.entities.Country


@Database(
    entities = arrayOf(Country::class), version = 1

)
@TypeConverters(GeoConverters::class)
abstract class CountriesDatabase : RoomDatabase() {
    abstract fun countriesDao(): CountriesDao

    companion object {
        const val DATABASE_NAME = "Geo Database"
        private var instance: CountriesDatabase? = null
        fun getInstance(context: Context): CountriesDatabase? {
            if (instance == null) {
                synchronized(CountriesDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        CountriesDatabase::class.java,
                        DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return instance
        }
    }
}