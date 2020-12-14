package com.csimcik.gardeningBuddy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.csimcik.gardeningBuddy.dao.FamilyDao
import com.csimcik.gardeningBuddy.models.entities.FamilyDB


@Database(
    entities = arrayOf(FamilyDB::class), version = 4
)
abstract class TrefleDatabase : RoomDatabase() {
    abstract fun familyDao(): FamilyDao

    companion object {
        private const val DATABASE_NAME = "Schedules database"
        private var instance: TrefleDatabase? = null
        fun getInstance(context: Context): TrefleDatabase? {
            if (instance == null) {
                synchronized(TrefleDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TrefleDatabase::class.java,
                        DATABASE_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return instance
        }
    }
}