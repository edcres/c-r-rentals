package com.example.crrentals.data

import com.example.crrentals.data.room.RentsRoomDatabase
import kotlinx.coroutines.flow.Flow

class Repository(private val db: RentsRoomDatabase) {
    private val tag = "WRepo_TAG"
    val allRentedItems: Flow<List<RentedItem>> = db.rentedItemsDao().getOrderedRentedItems()
}