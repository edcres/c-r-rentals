package com.example.crrentals.ui.bottomsheet

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.crrentals.BuildConfig
import com.example.crrentals.data.RentedItem
import com.example.crrentals.data.Repository
import com.example.crrentals.data.room.RentsRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

private const val TAG = "SheetViewModel__TAG"
private const val JPG_SUFFIX = ".jpg"

class BottomSheetViewModel : ViewModel() {

    private lateinit var roomDb: RentsRoomDatabase
    private lateinit var repo: Repository
    var currentRental: RentedItem? = null
    var latestTmpUri: Uri? = null

    fun setRentalItem(passedRental: RentedItem?) {
        currentRental = passedRental
    }

    fun setUpDatabase(context: Context) {
        roomDb = RentsRoomDatabase.getInstance(context)
        repo = Repository(roomDb)
    }

    // HELPERS //
    fun getDateString(): String {
        val calendar = Calendar.getInstance()
        return "${calendar.get(Calendar.MONTH)+1}/" +
                "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.YEAR)}"
    }
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

    // FILE QUERIES //
    fun makeTmpFile(cacheDir: File, appContext: Context): Uri {
        // todo: pass in the chosen name of the file instead of "tmp_image_file"
        val tmpFile = File.createTempFile("tmp_image_file", JPG_SUFFIX, cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        latestTmpUri = FileProvider.getUriForFile(appContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
        return latestTmpUri!!
    }
    fun deleteFileWithName(name: String, files: Array<File>?): Boolean {
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
    // FILE QUERIES //
}