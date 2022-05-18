package com.example.crrentals.ui.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.crrentals.R
import com.example.crrentals.data.RentedItem
import com.example.crrentals.databinding.FragmentBottomSheetBinding
import com.example.crrentals.util.BottomSheetAction
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File

private const val TAG = "ModalBottomSheet_TAG"

class BottomSheetFragment : BottomSheetDialogFragment() {

    private var binding: FragmentBottomSheetBinding? = null
    private lateinit var dialog: BottomSheetDialog
    private lateinit var vm: BottomSheetViewModel
    private var addOrUpdate: String? = null
    private var currentRental: RentedItem? = null

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (!isSuccess) {
                if (vm.latestTmpUri != null) {
                    val fileName = File(vm.latestTmpUri!!.path!!).name
                    val fileDeleted =
                        vm.deleteFileWithName(fileName, requireActivity().cacheDir.listFiles())
                    Log.d(TAG, "deleted: $fileDeleted")
                } else Log.d(TAG, "latestTmpUri: is null")
            }
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[BottomSheetViewModel::class.java]
        vm.setUpDatabase(requireActivity().applicationContext)
        vm.setRentalItem(currentRental)
        if (addOrUpdate != null) vm.addOrUpdate = addOrUpdate!!
        binding!!.apply {
            lifecycleOwner = viewLifecycleOwner
            if (vm.currentRental != null && vm.addOrUpdate == BottomSheetAction.UPDATE.toString())
                setUpUI(vm.currentRental!!)
            addItemBtn.setOnClickListener {
                insertRentalObject()
                dialog.dismiss()
            }
            acceptItemBtn.setOnClickListener {
                updateRentalObject(vm.currentRental!!)
                dialog.dismiss()
            }
            deleteItemBtn.setOnClickListener {
                if (vm.currentRental != null) vm.deleteRental(vm.currentRental!!)
                dialog.dismiss()
            }
            addImgBtn.setOnClickListener {
                takeImageResult.launch(
                    vm.makeTmpFile(
                        requireActivity().cacheDir,
                        requireActivity().applicationContext
                    )
                )
            }
            duplicateItemBtn.setOnClickListener {}
        }
        showCorrectBtn(vm.addOrUpdate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    // HELPERS //
    private fun setUpUI(rentalToLoad: RentedItem) {
        binding?.apply {
            val rentedOnString = "rented on ${rentalToLoad.time}"
            rentedOnTxt.visibility = View.VISIBLE
            roomNumEt.setText(rentalToLoad.roomNumber.toString())
            when (rentalToLoad.itemType) {
                RentedItem.ItemType.BIKE -> chooseTypeRadio.check(bikeBtn.id)
                RentedItem.ItemType.PADDLE_BOARD -> chooseTypeRadio.check(paddleBoardBtn.id)
                else -> chooseTypeRadio.check(chairBtn.id)
            }
            numEt.setText(rentalToLoad.number.toString())
            lockNumEt.setText(rentalToLoad.lock?.toString() ?: "")
            dailyRentalsSwitch.isChecked = rentalToLoad.dailyRentals
            paidSwitch.isChecked = rentalToLoad.paid
            rentedOnTxt.text = rentedOnString
            Glide.with(sheetRentalImage.context)
                .load(rentalToLoad.imageUri?:"noUri".toUri())
                .apply(
                    RequestOptions()
                        .transform(RoundedCorners(25))
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_baseline_broken_image))
                .into(sheetRentalImage)
            if (rentalToLoad.imageUri.isNullOrEmpty()) addImgTxt.text = "NEW PICTURE"
        }
    }
    private fun insertRentalObject() {
        binding?.apply {
            vm.insertRental(RentedItem(
                itemType = when (chooseTypeRadio.checkedRadioButtonId) {
                    bikeBtn.id -> RentedItem.ItemType.BIKE
                    paddleBoardBtn.id -> RentedItem.ItemType.PADDLE_BOARD
                    else -> RentedItem.ItemType.CHAIR
                },
                imageUri = vm.latestTmpUri.toString(),
                roomNumber = roomNumEt.text.toString().toInt(),
                dailyRentals = dailyRentalsSwitch.isChecked,
                time = vm.getDateString(),
                lock = lockNumEt.text.toString().toInt(),
                number = numEt.text.toString().toInt(),
                paid = paidSwitch.isChecked
            ))
        }
    }
    private fun updateRentalObject(rentalToUpdate: RentedItem) {
        binding?.apply {
            rentalToUpdate.roomNumber = roomNumEt.text.toString().toInt()
            rentalToUpdate.itemType = when (chooseTypeRadio.checkedRadioButtonId) {
                bikeBtn.id -> RentedItem.ItemType.BIKE
                paddleBoardBtn.id -> RentedItem.ItemType.PADDLE_BOARD
                else -> RentedItem.ItemType.CHAIR
            }
            rentalToUpdate.number = numEt.text.toString().toInt()
            rentalToUpdate.lock = lockNumEt.text.toString().toInt()
            rentalToUpdate.dailyRentals = dailyRentalsSwitch.isChecked
            rentalToUpdate.paid = paidSwitch.isChecked
            vm.updateRental(rentalToUpdate)
        }
    }
    private fun showCorrectBtn(addOrUpdatePassed: String) {
        Log.d(TAG, "showCorrectFab: add/update = $addOrUpdatePassed")
        binding!!.apply {
            when (addOrUpdatePassed) {
                BottomSheetAction.ADD.toString() -> addItemBtn.visibility = View.VISIBLE
                BottomSheetAction.UPDATE.toString() -> acceptItemBtn.visibility = View.VISIBLE
            }
        }
    }
    // HELPERS //

    companion object {
        fun newInstance(addOrUpdatePassed: String, passedRental: RentedItem?) =
            BottomSheetFragment().apply {
                addOrUpdate = addOrUpdatePassed
                currentRental = passedRental
        }
    }
}