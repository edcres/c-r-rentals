package com.example.crrentals.ui

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.crrentals.R
import com.example.crrentals.data.RentedItem
import com.example.crrentals.databinding.ActivityMainBinding
import com.example.crrentals.ui.bottomsheet.BottomSheetFragment
import com.example.crrentals.util.BottomSheetAction
import com.example.crrentals.util.ItemMoveCallback
import java.io.File
import java.util.*

/**
 * - do todos
 * - do warnings
 * - check comments
 * - clean imports
 */

/**
 * todo: Bottom sheet
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
 */

/** Eventually
 *
 * - Compress images as jpg
 * - User has the option to change the image.
 *
 */

private const val TAG = "MainAct_TAG"

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
        binding?.apply {
            lifecycleOwner = this@MainActivity
            addRentalFab.setOnClickListener {
                // todo: send the item to edit to the sheet fragment. (maybe use and observer from the adapter)
                bottomSheetFragment =
                    BottomSheetFragment.newInstance(BottomSheetAction.ADD.toString(), )
                bottomSheetFragment?.show(supportFragmentManager, bottomSheetFragment?.tag)

                // todo: probably get rid of this
//                if (listener != null) listener!!.sendTestString(BottomSheetAction.ADD.toString())
            }
        }
        rentalsAdapter = RentalsAdapter(vm, this, this)
        setUpItemAnimation()
        setObservers()
        vm.setUpDatabase(applicationContext)
        setUpItemEdit()

        // todo: call this when going to take a picture
//        vm.makeTmpFile(cacheDir, applicationContext).let { uri ->
//            latestTmpUri = uri
//            takeImageResult.launch(uri)
//        }
        // todo: delete file
//        vm.deleteFilWithName("sds", cacheDir.listFiles())
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