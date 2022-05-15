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

private const val TAG = "ViewModel_TAG"
private const val JPG_SUFFIX = ".jpg"

class RentItemsViewModel : ViewModel() {

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
    fun deleteFilWithName(name: String, files: Array<File>?): Boolean {
        // todo: do this in a background thread
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
    // todo: probable delete this function
//    fun deleteFileAt(position: Int, files: Array<File>?): Boolean {
//        files?.filter {
//            it.canRead() && it.isFile && it.name.endsWith(".jpg")
//        } ?: return false
//        return files[position].delete()
//    }
    fun makeTmpFile(cacheDir: File, appContext: Context): Uri {
        // pass in the chosen name of the file instead of "tmp_image_file"
        val tmpFile = File.createTempFile("tmp_image_file", JPG_SUFFIX, cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(appContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
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
    private fun deleteRental(rentedItem: RentedItem) = CoroutineScope(Dispatchers.IO).launch {
        repo.deleteRental(rentedItem)
    }
    // DATABASE QUERIES //
}