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

/**
 * // HARD //
 * todo: set up concurrency
 * // HARD //
 *
 * todo: test if setting the value of '_itemToEdit' to null (when it was already null) will trigger an observation
 * todo: put the logic in the VMs, take it off the views
 * todo: set which properties can be null (talk to bruno about this) (app now crashers bc of this sometimes)
 */

/** todo: Bugs
 * when an item is updated, the recyclerview is not refreshed
 * when adding an item with a picture, adding another item without a picture uses the same picture as the previous item
 * when i go add a new item, take a picture, click the img to take another picture but don't take that picture, then add the item, the item has not picture (it should have the picture that was takek bc it's still being displayed on the sheet, as it should)
 */

/** todo: future:
 * Compress images as jpg
 * maybe add a due date to the rental entity
 * consider also getting hr, minutes, and seconds on the time property on the entity
 * functionality to duplicate an item
 * User can change position of items in recyclerView
 * Maybe start to take a picture right when the user click the add rental btn
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
            rentalsAdapter.submitList(rentals)
        }
        vm.itemToEdit.observe(this) { itemToEdit ->
            bottomSheetFragment =
                BottomSheetFragment.newInstance(BottomSheetAction.UPDATE.toString(), itemToEdit)
            bottomSheetFragment?.show(supportFragmentManager, bottomSheetFragment?.tag)
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