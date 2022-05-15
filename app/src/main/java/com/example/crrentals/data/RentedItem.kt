package com.example.crrentals.data

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "rented_item_table")
data class RentedItem (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0,
    @ColumnInfo(name = "itemType")
    var itemType: ItemType,
    @ColumnInfo(name = "image_name")
    var imageName: String?,
    @ColumnInfo(name = "image_uri")
    var imageUri: Uri?,
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
    enum class ItemType(val type: String) {
        BIKE("bike"),
        PADDLE_BOARD("paddleBoard"),
        CHAIR("chair")
    }
}