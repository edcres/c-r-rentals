package com.example.crrentals.ui

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crrentals.data.RentedItem
import com.example.crrentals.data.Repository
import com.example.crrentals.data.room.RentsRoomDatabase
import com.example.crrentals.util.JPG_SUFFIX
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File

private const val TAG = "RentalsViewModel__TAG"

class RentItemsViewModel : ViewModel() {

    private lateinit var roomDb: RentsRoomDatabase
    private lateinit var repo: Repository
    var appStarting = true
    var positionJustUpdated = false

    private var _itemToEdit = MutableLiveData<RentedItem?>()
    val itemToEdit: LiveData<RentedItem?> get() = _itemToEdit

    private val _rentedItems = MutableLiveData<MutableList<RentedItem>>()
    val rentedItems: LiveData<MutableList<RentedItem>> get() = _rentedItems

    // HELPERS //
    fun refreshRecycler() {
        // To cover up a bug
        // Because sometimes it doesn't update the view the first time an item is updated.
        if(_rentedItems.value!!.size > 0) {
            _rentedItems.value!![0].time = "${_rentedItems.value!![0].time} "
            updateRental(_rentedItems.value!![0])
            _rentedItems.value!![0].time = _rentedItems.value!![0].time.dropLast(1)
            updateRental(_rentedItems.value!![0])
        }
    }
    fun nullItemToEdit() {
        _itemToEdit.postValue(null)
    }
    fun deleteRentalAt(files: Array<File>?, position: Int) {
        if (rentedItems.value != null) {
            deleteRental(files, rentedItems.value!![position])
        }
    }
    fun setItemToEdit(rentedItem: RentedItem) {
        _itemToEdit.postValue(rentedItem)
    }
    fun updateRentalsPositions() {
        // maybe do this in a background thread
        // todo:
        val rentalsList = _rentedItems.value!!.toList()

        var str1 = ""
        for(i in rentalsList.indices) { str1 += "\n${rentalsList[i].id}-${rentalsList[i].roomNumber}-${rentalsList[i].listPosition}" }
        Log.d(TAG, "updateRentalsPositions1: $str1")
        for(i in rentalsList.indices) {
            rentalsList[i].listPosition = i
        }

        var str2 = ""
        for(i in rentalsList.indices) { str2 += "\n${rentalsList[i].id}-${rentalsList[i].roomNumber}-${rentalsList[i].listPosition}" }
        Log.d(TAG, "updateRentalsPositions2: $str2")
        positionJustUpdated = true
        updateRentals(rentalsList)
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
        viewModelScope.launch {
            repo.allRentedItems.collect {
                _rentedItems.postValue(it.toMutableList())
            }
        }
    }
    private fun updateRentals(rentedItems: List<RentedItem>) = viewModelScope.launch {
        repo.updateRentals(rentedItems)
    }
    private fun updateRental(rentedItem: RentedItem) = viewModelScope.launch {
        repo.updateRental(rentedItem)
    }
    private fun deleteRental(filesList: Array<File>?, rentedItem: RentedItem) =
        viewModelScope.launch {
            if (rentedItem.imageUri != null) {
                val fileName = File(rentedItem.imageUri!!.toUri().path!!).name
                Log.i(TAG, "file deleted: ${deleteFileWithName(fileName, filesList)}")
            }
            repo.deleteRental(rentedItem)
        }
    // DATABASE QUERIES //

    // FILE QUERIES //
    private fun deleteFileWithName(name: String, files: Array<File>?): Boolean {
        // todo: concurrency here
        if (files.isNullOrEmpty()) {
            Log.e(TAG, "deleteFile: Error loading files.")
            return false
        } else {
            files.filter {
                it.canRead() && it.isFile && it.name.endsWith(JPG_SUFFIX)
            }.forEach { if (it.name == name) return it.delete() }
            return false
        }
    }
    // FILE QUERIES //
}