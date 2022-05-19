package com.example.crrentals.ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.crrentals.R
import com.example.crrentals.databinding.ActivityMainBinding
import com.example.crrentals.ui.bottomsheet.BottomSheetFragment
import com.example.crrentals.util.BottomSheetAction
import com.example.crrentals.util.ItemMoveCallback
import kotlin.math.log

/**
 * todo: put the logic in the VMs, take it off the views
 * todo: set which properties can be null (talk to bruno about this) (app now crashers bc of this sometimes)
 */

/** todo: Bugs
 * When i go add a new item, take a picture, click the img to take another picture but don't take that picture, then add the item, the item has no picture (it should have the picture that was taken bc it's still being displayed on the sheet, as it should)
 * When i went to update a rental, the add rental button was show and when clicked it crashed (have not been able to recreate this bug)
 * when an item is updated, the recyclerview is not refreshed
 *      - I fixed it in a hacky way. Now always return false in areContentsTheSame() in the adapter, bc for some reason the first time an item is updated, the oldItem updates as well right before the function checks if both are the same.
 */

/** todo: future:
 * Compress images as jpg
 * Maybe add a due date to the rental entity
 * Consider also getting hr, minutes, and seconds on the time property on the entity
 * functionality to duplicate an item
 * User can change position of items in recyclerView
 * Maybe start to take a picture right when the user click the add rental btn
 * Set up concurrency for file queries
 */

/**
 * - do todos
 * - clean logs
 * - do warnings
 * - check comments
 * - clean imports
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
        rentalsAdapter = RentalsAdapter(vm, this, this)
        binding?.apply {
            lifecycleOwner = this@MainActivity
            rentalsRecycler.adapter = rentalsAdapter
            rentalsRecycler.layoutManager = LinearLayoutManager(this@MainActivity)

            addRentalFab.setOnClickListener {
                bottomSheetFragment =
                    BottomSheetFragment.newInstance(BottomSheetAction.ADD.toString(), null)
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
            Log.d(TAG, "rentals observed")
            Log.d(TAG, "rental room#: ${rentals[0].roomNumber}")
            rentalsAdapter.submitList(rentals.toList())
        }
        vm.itemToEdit.observe(this) { itemToEdit ->
            if(itemToEdit != null) {
                Log.d(TAG, "itemToEdit observed \n$itemToEdit")
                bottomSheetFragment =
                    BottomSheetFragment.newInstance(BottomSheetAction.UPDATE.toString(), itemToEdit)
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
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // Fill this when integrating a feature to change item position.
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