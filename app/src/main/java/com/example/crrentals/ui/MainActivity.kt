package com.example.crrentals.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.crrentals.R
import com.example.crrentals.databinding.ActivityMainBinding
import com.example.crrentals.ui.RentItemsViewModel

// Have different viewModels.

private const val MAIN_ACT_TAG = "MainAct_TAG"

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private lateinit var vm: RentItemsViewModel
    private lateinit var rentalsAdapter: RentalsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding?.apply {
            lifecycleOwner = this@MainActivity
        }
        vm = ViewModelProvider(this).get(RentItemsViewModel::class.java)
        rentalsAdapter = RentalsAdapter(vm, this, this)
        vm.setUpDatabase(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}