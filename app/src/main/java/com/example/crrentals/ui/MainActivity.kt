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
 * todo: make fragment .xml
 * todo: make fragment .kt
 * todo: set up the bottom sheet fragment into it's parent fragment
 * todo: make rounded edges on the sheet
 * todo: The bottom sheet has a button for the user to add a picture
 *
 *
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

    private var latestTmpUri: Uri? = null
    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                    // todo:
                    //  have a loading and error image
//                      Glide.with(binding.photoImg.context)
//                          .load(latestTmpUri)
//                          .into(binding.photoImg)
            } else {
                if (latestTmpUri != null) {
                    val fileName = File(latestTmpUri!!.path!!).name
                    val fileDeleted = vm.deleteFilWithName(fileName, cacheDir.listFiles())
                    Log.d(TAG, "deleted: $fileDeleted")
                } else Log.d(TAG, "latestTmpUri: is null")

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.apply {
            lifecycleOwner = this@MainActivity
            addRentalFab.setOnClickListener {

                // todo: pop up the bottom sheet and put in the new item info
                //  when the user clicks the checkmark, the item is either inserted or updated
//                vm.insertRental(RentedItem())
//                vm.updateRental(RentedItem())
            }
        }
        vm = ViewModelProvider(this)[RentItemsViewModel::class.java]
        rentalsAdapter = RentalsAdapter(vm, this, this)
        setUpItemAnimation()
        setObservers()
        vm.setUpDatabase(this)
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