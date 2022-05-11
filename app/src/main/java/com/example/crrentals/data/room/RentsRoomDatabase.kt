package com.example.crrentals.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import com.example.crrentals.data.RentedItem
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.crrentals.util.Converters

@Database(entities = [RentedItem::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RentsRoomDatabase: RoomDatabase() {

    abstract fun rentedItemsDao(): RentedItemsDao

    companion object {
        @Volatile
        private var INSTANCE: RentsRoomDatabase? = null
        private const val DATABASE_NAME = "rents_database"

        fun getInstance(context: Context): RentsRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance= Room.databaseBuilder(
                    context.applicationContext,
                    RentsRoomDatabase::class.java,
                    DATABASE_NAME
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}