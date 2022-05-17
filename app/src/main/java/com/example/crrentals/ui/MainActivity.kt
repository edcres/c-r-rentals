package com.example.crrentals.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
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

/**
 * - do todos
 * - clean logs
 * - do warnings
 * - check comments
 * - clean imports
 */

/**
 *
 * todo: app crashes when pressing on a recycler item
 *
 * todo: set add or update btn visibility to gone
 *
 * todo: app icon
 *
 * todo: put everything together
 *
 * todo: appbar
 *
 * todo: colors
 *
 * todo: make string resources
 * todo: check if all drawables are used
 *
 * todo: set which items are null
 * todo: maybe start to take a picture right when the user click the add rental btn
 *
 * todo: set up concurrency
 */

/** Eventually
 *
 * - Compress images as jpg
 * - User has the option to change the image.
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

    private fun setObservers() {
        vm.rentedItems.observe(this) { rentals ->
            Log.d(TAG, "setObservers: rentals observed. Size = ${rentals.size}")
            Log.d(TAG, "setObservers: rentals observed. items = ${rentals.size}")
            rentalsAdapter.submitList(rentals)
        }
        // Update a rental item
        vm.itemToEdit.observe(this) { itemToEdit ->
            // todo: open the sheet and send the item as a value
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
                vm.deleteRentalAt(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(editItemCallback)
        itemTouchHelper.attachToRecyclerView(binding!!.rentalsRecycler)
    }
}