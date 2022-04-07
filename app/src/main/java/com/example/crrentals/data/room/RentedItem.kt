package com.example.crrentals.data.room

import java.util.*

data class RentedItem (
    var item: String,
    var roomNumber: Int,
    var dailyRentals: Boolean,
    var time: Date,
    var lock: Int?,
    var number: Int,
    var paid: Boolean
) {
    companion object {
        const val BIKE_TYPE = "bike"
        const val PADDLE_BOARD_TYPE = "paddleBoard"
        const val CHAIRS_TYPE = "chairs"
    }
}