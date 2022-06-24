package com.example.crrentals.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rented_item_table")
data class RentedItem (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0,
    @ColumnInfo(name = "item_type")
    var itemType: ItemType,
    @ColumnInfo(name = "image_uri")
    var imageUri: String?,  // Turn this String into a Uri type
    @ColumnInfo(name = "room_number")
    var roomNumber: Int?,
    @ColumnInfo(name = "daily_rentals")
    var dailyRentals: Boolean,
    @ColumnInfo(name = "time")
    var time: String,
    @ColumnInfo(name = "lock")
    var lock: Int?,
    @ColumnInfo(name = "number")
    var number: Int?,
    @ColumnInfo(name = "paid")
    var paid: Boolean,
    @ColumnInfo(name = "list_position")
    var listPosition: Int   // Starts at 0
) {
    enum class ItemType(val type: String) {
        BIKE("bike"),
        PADDLE_BOARD("paddle board"),
        CHAIR("chair")
    }
}