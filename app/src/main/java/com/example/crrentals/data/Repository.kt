package com.example.crrentals.data

import androidx.annotation.WorkerThread
import com.example.crrentals.data.room.RentsRoomDatabase
import kotlinx.coroutines.flow.Flow

private const val TAG = "WRepo_TAG"

class Repository(private val db: RentsRoomDatabase) {

    val allRentedItems: Flow<List<RentedItem>> = db.rentedItemsDao().getOrderedRentedItems()

    @WorkerThread
    suspend fun insertRental(rentedItem: RentedItem): Long {
        return db.rentedItemsDao().insert(rentedItem)
    }

    @WorkerThread
    suspend fun updateRental(rentedItem: RentedItem) {
        db.rentedItemsDao().update(rentedItem)
    }

    @WorkerThread
    suspend fun deleteRental(rentedItem: RentedItem) {
        db.rentedItemsDao().delete(rentedItem)
    }
}