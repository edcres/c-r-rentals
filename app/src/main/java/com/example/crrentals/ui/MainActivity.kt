package com.example.crrentals.ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crrentals.R
import com.example.crrentals.databinding.ActivityMainBinding
import com.example.crrentals.ui.bottomsheet.BottomSheetFragment
import com.example.crrentals.util.BottomSheetAction
import com.example.crrentals.util.ItemMoveCallback
import java.util.*
import kotlin.math.log

/**
 * App explanation
 *
 * There's a list of Rented items that show:
 *  - A picture of the item, type of the item, date rented, and room number
 * More data is displayed when the user clicks on an item and a bottom sheet pops up
 *  - Here the user can edit the item
 * To create an item, the user clicks the add button on the main screen,
 *  fills out the bottom sheet, and clicks the add button.
 *
 */

private const val TAG = "MainAct__TAG"

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private lateinit var vm: RentItemsViewModel
    private lateinit var rentalsAdapter: RentalsAdapter
    private var bottomSheetFragment: BottomSheetFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpDefaultSettings()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        vm = ViewModelProvider(this)[RentItemsViewModel::class.java]
        rentalsAdapter = RentalsAdapter(vm)
        binding?.apply {
            lifecycleOwner = this@MainActivity
            rentalsRecycler.adapter = rentalsAdapter
            rentalsRecycler.layoutManager = LinearLayoutManager(this@MainActivity)

            addRentalFab.setOnClickListener {
                bottomSheetFragment = BottomSheetFragment.newInstance(
                    BottomSheetAction.ADD.toString(),
                    null,
                    vm.rentedItems.value!!.size
                )
                bottomSheetFragment?.show(supportFragmentManager, bottomSheetFragment?.tag)
            }
        }
        setUpItemAnimation()
        setObservers()
        vm.setUpDatabase(applicationContext)
        setUpItemEdit()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun setUpDefaultSettings() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        }
    }

    private fun setObservers() {
        vm.rentedItems.observe(this) { rentals ->
            rentalsAdapter.submitList(rentals.toList())

            Log.d(TAG, "setObservers: \n${rentals}")

            if (vm.appStarting) {
                // Constraints recyclerView update only for the creation of this activity.
                binding!!.rentalsRecycler.startLayoutAnimation()
                vm.appStarting = false
            }
        }
        vm.itemToEdit.observe(this) { itemToEdit ->
            if(itemToEdit != null) {
                Log.d(TAG, "itemToEdit observed \n$itemToEdit")
                bottomSheetFragment = BottomSheetFragment.newInstance(
                    BottomSheetAction.UPDATE.toString(),
                    itemToEdit,
                    vm.rentedItems.value!!.size
                )
                bottomSheetFragment?.show(supportFragmentManager, bottomSheetFragment?.tag)
                vm.nullItemToEdit()
            }
        }
    }

    private fun setUpItemAnimation() {
        val animController = LayoutAnimationController(
            AnimationUtils
            .loadAnimation(this, R.anim.item_anim))
        animController.delay = 0.20f
        animController.order = LayoutAnimationController.ORDER_NORMAL
        binding!!.rentalsRecycler.layoutAnimation = animController
    }

    private fun setUpItemEdit() {
        val editItemCallback = object : ItemMoveCallback(
            ContextCompat.getColor(this, R.color.delete_color),
            R.drawable.ic_delete_24
        ) {

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                vm.updateRentalsPositions()
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                Collections.swap(vm.rentedItems.value!!, fromPosition, toPosition)

                Log.d(TAG, "onMove: called: $fromPosition -> $toPosition")
                // todo: change position in Room
                rentalsAdapter.notifyItemMoved(fromPosition, toPosition)
//                vm.updateRentalPosition(vm)
//                vm.updateRentalsPositions(fromPosition, toPosition)

                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                vm.deleteRentalAt(cacheDir.listFiles(), viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(editItemCallback)
        itemTouchHelper.attachToRecyclerView(binding!!.rentalsRecycler)
    }
}