package com.example.crrentals.util

import com.example.crrentals.data.RentedItem

fun getRentalAtListPosition(position: Int, rentals: MutableList<RentedItem>): RentedItem {
    return rentals[position]
}