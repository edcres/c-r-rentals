package com.example.crrentals

import androidx.lifecycle.ViewModel
import com.example.crrentals.data.Repository
import com.example.crrentals.data.room.RentsRoomDatabase

class SharedViewModel : ViewModel() {

    private val tag = "ViewModel_TAG"
    private lateinit var roomDb: RentsRoomDatabase
    private lateinit var repo: Repository


}