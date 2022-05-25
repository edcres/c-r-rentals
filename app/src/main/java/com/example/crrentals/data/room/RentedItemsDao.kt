package com.example.crrentals.data.room

import androidx.room.*
import com.example.crrentals.data.RentedItem
import kotlinx.coroutines.flow.Flow

@Dao
interface RentedItemsDao {

    @Query("SELECT * FROM rented_item_table ORDER BY list_position ASC")
    fun getOrderedRentedItems(): Flow<List<RentedItem>>

    @Delete
    suspend fun delete(rentedItem: RentedItem)

    @Update
    suspend fun update(rentedItem: RentedItem)

    @Update
    suspend fun update(rentedItems: List<RentedItem>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(rentedItem: RentedItem): Long
}