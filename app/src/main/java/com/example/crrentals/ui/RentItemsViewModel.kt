package com.example.crrentals.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.crrentals.data.RentedItem
import com.example.crrentals.data.Repository
import com.example.crrentals.data.room.RentsRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RentItemsViewModel : ViewModel() {

    private val tag = "ViewModel_TAG"
    private lateinit var roomDb: RentsRoomDatabase
    private lateinit var repo: Repository

    private val _rentedItems = MutableLiveData<MutableList<RentedItem>>()
    val rentedItems: LiveData<MutableList<RentedItem>> get() = _rentedItems

    // HELPERS //
    fun deleteRentalAt(position: Int) {
        if (rentedItems.value != null) {
            deleteRental(rentedItems.value!![position])
        }
    }
    // HELPERS //

    // SETUP //
    fun setUpDatabase(context: Context) {
        roomDb = RentsRoomDatabase.getInstance(context)
        repo = Repository(roomDb)
        collectAllRentItems()
    }
    // SETUP //

    // DATABASE QUERIES //
    private fun collectAllRentItems() {
        CoroutineScope(Dispatchers.IO).launch {
            repo.allRentedItems.collect {
                _rentedItems.postValue(it.toMutableList())
            }
        }
    }
    fun insertRental(rentedItem: RentedItem): MutableLiveData<Long> {
        val itemId = MutableLiveData<Long>()
        CoroutineScope(Dispatchers.IO).launch {
            itemId.postValue(repo.insertRental(rentedItem))
        }
        return itemId
    }
    fun updateRental(rentedItem: RentedItem) = CoroutineScope(Dispatchers.IO).launch {
        repo.updateRental(rentedItem)
    }
    fun deleteRental(rentedItem: RentedItem) = CoroutineScope(Dispatchers.IO).launch {
        repo.deleteRental(rentedItem)
    }
    // DATABASE QUERIES //
}