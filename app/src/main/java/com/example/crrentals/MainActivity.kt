package com.example.crrentals

import android.database.DatabaseUtils
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.crrentals.databinding.ActivityMainBinding

// Have different viewModels.

private const val MAIN_ACT_TAG = "MainAct_TAG"

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private val vm = RentItemsViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding?.apply {
            lifecycleOwner = this@MainActivity
        }
        vm.startApplication(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}