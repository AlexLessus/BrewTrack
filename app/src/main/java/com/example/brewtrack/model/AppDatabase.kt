package com.example.brewtrack.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [CoffeeLog::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun coffeeLogDao(): CoffeeLogDao

    companion object {//this will be visible for other classes
        @Volatile
        private var INSTANCE: AppDatabase? = null

        //if there is an instance, return it, otherwise create one
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "coffee_log_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
