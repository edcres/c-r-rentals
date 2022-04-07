package com.example.crrentals.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "rented_item_table")
data class RentedItem (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0,
    @ColumnInfo(name = "item")
    var item: String,
    @ColumnInfo(name = "room_number")
    var roomNumber: Int,
    @ColumnInfo(name = "daily_rentals")
    var dailyRentals: Boolean,
    @ColumnInfo(name = "time")
    var time: Date,
    @ColumnInfo(name = "lock")
    var lock: Int?,
    @ColumnInfo(name = "number")
    var number: Int,
    @ColumnInfo(name = "paid")
    var paid: Boolean
) {
    companion object {
        const val BIKE_TYPE = "bike"
        const val PADDLE_BOARD_TYPE = "paddleBoard"
        const val CHAIRS_TYPE = "chairs"
    }
}