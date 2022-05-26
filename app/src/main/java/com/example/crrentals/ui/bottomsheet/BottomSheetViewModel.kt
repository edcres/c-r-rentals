package com.example.crrentals.ui.bottomsheet

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crrentals.BuildConfig
import com.example.crrentals.data.RentedItem
import com.example.crrentals.data.Repository
import com.example.crrentals.data.room.RentsRoomDatabase
import com.example.crrentals.util.BottomSheetAction
import com.example.crrentals.util.JPG_SUFFIX
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

private const val TAG = "SheetViewModel__TAG"

class BottomSheetViewModel : ViewModel() {

    private lateinit var roomDb: RentsRoomDatabase
    private lateinit var repo: Repository
    var latestTmpUri: Uri? = null
    var itemSentToSave = false
    lateinit var addOrUpdate: String
    var currentRental: RentedItem? = null
    var listSize: Int? = null

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "sheetVM clear called")
    }

    fun setUpDatabase(context: Context) {
        roomDb = RentsRoomDatabase.getInstance(context)
        repo = Repository(roomDb)
    }

    // HELPERS //
    fun replacePreviousImgIfExists(filesList: Array<File>) {
        if (currentRental != null) {
            // If previous rental had an image, replace previous image.
            if(currentRental!!.imageUri != null) {
                // Delete previous file
                val fileName = File(currentRental!!.imageUri!!.toUri().path!!).name
                val fileDeleted =
                    deleteFileWithName(fileName, filesList)
                Log.i(TAG, "file deleted: $fileDeleted")
            }
            // Set the Uri
            currentRental!!.imageUri =
                if (latestTmpUri != null) latestTmpUri.toString()
                else null
            Log.i(TAG, "new uri: \n${currentRental!!.imageUri}")
        }
    }
    fun deleteFileIfImageIsRejected(filesList: Array<File>) {
        // If the image was not accepted, remove the file created
        if (filesList.isNotEmpty()) {
            if (latestTmpUri != null) {
                val fileName = File(latestTmpUri!!.path!!).name
                val fileDeleted =
                    deleteFileWithName(fileName, filesList)
                Log.i(TAG, "deleted: $fileDeleted")
            }
        }
    }
    fun deleteFileIfItemIsCanceled(filesList: Array<File>) {
        // Delete a file when an item to be inserted is canceled
        if (filesList.isNotEmpty()) {
            if (!itemSentToSave && latestTmpUri != null &&
                addOrUpdate == BottomSheetAction.ADD.toString()
            ) {
                // Delete the img file if the item is not saved.
                val fileName = File(latestTmpUri!!.path!!).name
                Log.i(TAG, "file deleted: ${deleteFileWithName(fileName, filesList)}")
            }
        }
    }
    fun setRentalItem(passedRental: RentedItem?) {
        currentRental = passedRental
    }
    fun getDateString(): String {
        val calendar = Calendar.getInstance()
        return "${calendar.get(Calendar.MONTH)+1}/" +
                "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.YEAR)}"
    }
    // HELPERS //

    // DATABASE QUERIES //
    fun updateRental(rentedItem: RentedItem) = viewModelScope.launch {
        repo.updateRental(rentedItem)
    }

    fun deleteRental(filesList: Array<File>?, rentedItem: RentedItem) =
        viewModelScope.launch {
            if (rentedItem.imageUri != null) {
                val fileName = File(rentedItem.imageUri!!.toUri().path!!).name
                Log.i(TAG, "file deleted: ${deleteFileWithName(fileName, filesList)}")
            }
            repo.deleteRental(rentedItem)
        }
    fun insertRental(rentedItem: RentedItem): MutableLiveData<Long> {
        val itemId = MutableLiveData<Long>()
        viewModelScope.launch {
            itemId.postValue(repo.insertRental(rentedItem))
        }
        return itemId
    }
    // DATABASE QUERIES //

    // FILE QUERIES //
    fun makeTmpFile(cacheDir: File, appContext: Context): Uri {
        // todo: concurrency here
        val tmpFile = File.createTempFile("tmp_image_file", JPG_SUFFIX, cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        latestTmpUri = FileProvider
            .getUriForFile(appContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
        return latestTmpUri!!
    }
    fun deleteFileWithName(name: String, files: Array<File>?): Boolean {
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