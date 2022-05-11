package com.example.crrentals.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.crrentals.R
import com.example.crrentals.data.RentedItem
import com.example.crrentals.databinding.ActivityMainBinding
import com.example.crrentals.ui.RentItemsViewModel

private const val TAG = "MainAct_TAG"

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private lateinit var vm: RentItemsViewModel
    private lateinit var rentalsAdapter: RentalsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding?.apply {
            lifecycleOwner = this@MainActivity
            addRentalFab.setOnClickListener {

                // todo: pop up the bottom sheet and put in the new item info
                //  when the user clicks the checkmark, the item is either inserted or updated
//                vm.insertRental(RentedItem())
//                vm.updateRental(RentedItem())
            }
        }
        vm = ViewModelProvider(this).get(RentItemsViewModel::class.java)
        rentalsAdapter = RentalsAdapter(vm, this, this)
        vm.setUpDatabase(this)
        setObservers()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun setObservers() {
        vm.rentedItems.observe(this) { rentals ->
            rentalsAdapter.submitList(rentals)
        }
    }
}