package com.example.crrentals.ui.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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
import com.example.crrentals.util.stringToInt
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

private const val TAG = "ModalBottomSheet__TAG"

class BottomSheetFragment : BottomSheetDialogFragment() {

    private var binding: FragmentBottomSheetBinding? = null
    private lateinit var dialog: BottomSheetDialog
    private lateinit var vm: BottomSheetViewModel
    private var addOrUpdate: String? = null
    private var currentRental: RentedItem? = null
    private var listSize: Int? = null
    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                setImgOnView(vm.latestTmpUri!!)
                vm.replacePreviousImgIfExists(requireActivity().cacheDir.listFiles() ?: arrayOf())
            } else {
                vm.deleteFileIfImageIsRejected(requireActivity().cacheDir.listFiles() ?: arrayOf())
            }
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        vm.listSize = listSize
        binding!!.apply {
            lifecycleOwner = viewLifecycleOwner
            if (vm.currentRental != null && vm.addOrUpdate == BottomSheetAction.UPDATE.toString())
                setUpUI(vm.currentRental!!)
            addItemBtn.setOnClickListener {
                insertRentalObject()
                vm.itemSentToSave = true
                dialog.dismiss()
            }
            acceptItemBtn.setOnClickListener {
                updateRentalObject(vm.currentRental!!)
                dialog.dismiss()
            }
            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }
            deleteItemBtn.setOnClickListener {
                if (vm.currentRental != null)
                    vm.deleteRental(requireActivity().cacheDir.listFiles(), vm.currentRental!!)
                dialog.dismiss()
            }
            addImgBtn.setOnClickListener {
                takePicture()
            }
            sheetRentalImage.setOnClickListener {
                if (vm.currentRental == null) takePicture()
                else if (vm.currentRental!!.imageUri == null) takePicture()
            }
        }
        showCorrectBtn(vm.addOrUpdate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Delete a file when an item to be inserted is canceled.
        vm.deleteFileIfItemIsCanceled(requireActivity().cacheDir.listFiles() ?: arrayOf())
        vm.currentRental = null
        vm.latestTmpUri = null
        vm.itemSentToSave = false
        binding = null
    }

    // HELPERS //
    private fun setUpUI(rentalToLoad: RentedItem) {
        binding?.apply {
            val rentedOnString = "rented on ${rentalToLoad.time}"
            rentedOnTxt.visibility = View.VISIBLE
            roomNumEt.setText(rentalToLoad.roomNumber?.toString() ?: "")
            when (rentalToLoad.itemType) {
                RentedItem.ItemType.BIKE -> chooseTypeRadio.check(bikeBtn.id)
                RentedItem.ItemType.PADDLE_BOARD -> chooseTypeRadio.check(paddleBoardBtn.id)
                else -> chooseTypeRadio.check(chairBtn.id)
            }
            numEt.setText(rentalToLoad.number?.toString() ?: "")
            lockNumEt.setText(rentalToLoad.lock?.toString() ?: "")
            dailyRentalsSwitch.isChecked = rentalToLoad.dailyRentals
            paidSwitch.isChecked = rentalToLoad.paid
            rentedOnTxt.text = rentedOnString
            setImgOnView(rentalToLoad.imageUri ?: "noUri".toUri())
            if (rentalToLoad.imageUri.isNullOrEmpty()) {
                addImgTxt.text = requireActivity().getString(R.string.new_picture_label)
            }
        }
    }

    private fun insertRentalObject() {
        binding?.apply {
            val itemToInsert = RentedItem(
                itemType = when (chooseTypeRadio.checkedRadioButtonId) {
                    bikeBtn.id -> RentedItem.ItemType.BIKE
                    paddleBoardBtn.id -> RentedItem.ItemType.PADDLE_BOARD
                    else -> RentedItem.ItemType.CHAIR
                },
                imageUri = if (vm.latestTmpUri != null) {
                    Log.d(TAG, "latest temp uri: ${vm.latestTmpUri}")
                    vm.latestTmpUri.toString()
                } else null,
                roomNumber = stringToInt(roomNumEt.text.toString()),
                dailyRentals = dailyRentalsSwitch.isChecked,
                time = vm.getDateString(),
                lock = stringToInt(lockNumEt.text.toString()),
                number = stringToInt(numEt.text.toString()),
                paid = paidSwitch.isChecked,
                listPosition = vm.listSize ?: 0
            )
            vm.insertRental(itemToInsert)
        }
    }

    private fun updateRentalObject(rentalToUpdate: RentedItem) {
        binding?.apply {
            rentalToUpdate.roomNumber = stringToInt(roomNumEt.text.toString())
            rentalToUpdate.itemType = when (chooseTypeRadio.checkedRadioButtonId) {
                bikeBtn.id -> RentedItem.ItemType.BIKE
                paddleBoardBtn.id -> RentedItem.ItemType.PADDLE_BOARD
                else -> RentedItem.ItemType.CHAIR
            }
            rentalToUpdate.number = stringToInt(numEt.text.toString())
            rentalToUpdate.lock = stringToInt(lockNumEt.text.toString())
            rentalToUpdate.dailyRentals = dailyRentalsSwitch.isChecked
            rentalToUpdate.paid = paidSwitch.isChecked
            vm.updateRental(rentalToUpdate)
        }
    }

    private fun showCorrectBtn(addOrUpdatePassed: String) {
        binding!!.apply {
            when (addOrUpdatePassed) {
                BottomSheetAction.ADD.toString() -> addItemBtn.visibility = View.VISIBLE
                BottomSheetAction.UPDATE.toString() -> acceptItemBtn.visibility = View.VISIBLE
            }
        }
    }

    private fun setImgOnView(uri: Any) {
        binding?.apply {
            Glide.with(sheetRentalImage.context)
                .load(uri)
                .apply(
                    RequestOptions()
                        .transform(RoundedCorners(25))
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_placeholder_image_144)
                )
                .into(sheetRentalImage)
        }
    }

    private fun takePicture() {
        val uriCreated = vm.makeTmpFile(
            requireActivity().cacheDir,
            requireActivity().applicationContext
        )
        Log.i(TAG, "takenPicture: uriCreated: \n$uriCreated")
        takeImageResult.launch(
            uriCreated
        )
    }
    // HELPERS //

    companion object {
        fun newInstance(addOrUpdatePassed: String, passedRental: RentedItem?, listSizePassed: Int) =
            BottomSheetFragment().apply {
                addOrUpdate = addOrUpdatePassed
                currentRental = passedRental
                listSize = listSizePassed
            }
    }
}