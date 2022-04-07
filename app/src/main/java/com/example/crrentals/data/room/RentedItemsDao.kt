package com.example.crrentals.data.room

import androidx.room.Dao
import androidx.room.Query
import com.example.crrentals.data.RentedItem
import kotlinx.coroutines.flow.Flow

@Dao
interface RentedItemsDao {
    @Query("SELECT * FROM rented_item_table ORDER BY id ASC")
    fun getOrderedRentedItems(): Flow<List<RentedItem>>
}