package com.example.crrentals.ui

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.crrentals.BuildConfig
import com.example.crrentals.data.RentedItem
import com.example.crrentals.data.Repository
import com.example.crrentals.data.room.RentsRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File

private const val TAG = "RentalsViewModel__TAG"

class RentItemsViewModel : ViewModel() {

    private lateinit var roomDb: RentsRoomDatabase
    private lateinit var repo: Repository

    // todo: give this a value from the adapter
    private var _itemToEdit = MutableLiveData<RentedItem>()
    val itemToEdit: LiveData<RentedItem> get() = _itemToEdit

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
    private fun deleteRental(rentedItem: RentedItem) = CoroutineScope(Dispatchers.IO).launch {
        repo.deleteRental(rentedItem)
    }
    // DATABASE QUERIES //
}