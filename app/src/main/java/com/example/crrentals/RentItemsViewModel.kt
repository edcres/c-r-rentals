package com.example.crrentals

import android.app.Application
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

    private val _rentedItem = MutableLiveData<MutableList<RentedItem>>()
    val rentedItem: LiveData<MutableList<RentedItem>> get() = _rentedItem

    // SETUP //
    fun startApplication(application: Application) {
        roomDb = RentsRoomDatabase.getInstance(application)
        repo = Repository(roomDb)
        collectAllRentItems()
    }
    // SETUP //

    // DATABASE QUERIES //
    private fun collectAllRentItems() {
        CoroutineScope(Dispatchers.IO).launch {
            repo.allRentedItems.collect {
                _rentedItem.postValue(it.toMutableList())
            }
        }
    }
    // DATABASE QUERIES //
}