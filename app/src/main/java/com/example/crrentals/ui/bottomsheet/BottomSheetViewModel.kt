package com.example.crrentals.ui.bottomsheet

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.crrentals.data.RentedItem
import com.example.crrentals.data.Repository
import com.example.crrentals.data.room.RentsRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BottomSheetViewModel : ViewModel() {

    private lateinit var roomDb: RentsRoomDatabase
    private lateinit var repo: Repository
    lateinit var currentRental: RentedItem

    fun setRentalItem(passedRental: RentedItem) {
        currentRental = passedRental
    }

    fun setUpDatabase(context: Context) {
        roomDb = RentsRoomDatabase.getInstance(context)
        repo = Repository(roomDb)
    }

    // HELPERS //

    // HELPERS //

    // DATABASE QUERIES //
    fun updateRental(rentedItem: RentedItem) = CoroutineScope(Dispatchers.IO).launch {
        repo.updateRental(rentedItem)
    }
    fun deleteRental(rentedItem: RentedItem) = CoroutineScope(Dispatchers.IO).launch {
        repo.deleteRental(rentedItem)
    }
    fun insertRental(rentedItem: RentedItem): MutableLiveData<Long> {
        val itemId = MutableLiveData<Long>()
        CoroutineScope(Dispatchers.IO).launch {
            itemId.postValue(repo.insertRental(rentedItem))
        }
        return itemId
    }
    // DATABASE QUERIES //
}