package com.example.crrentals.ui.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.crrentals.data.RentedItem
import com.example.crrentals.databinding.FragmentBottomSheetBinding
import com.example.crrentals.util.BottomSheetAction
import com.example.crrentals.util.SHEET_STR_KEY
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

private const val TAG = "ModalBottomSheet_TAG"

class BottomSheetFragment : BottomSheetDialogFragment() {

    private var binding: FragmentBottomSheetBinding? = null
    private lateinit var dialog: BottomSheetDialog
    private lateinit var vm: BottomSheetViewModel
    private lateinit var addOrUpdate: String
    private lateinit var currentRental: RentedItem

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
        binding!!.apply {
            lifecycleOwner = viewLifecycleOwner
            // todo: set UI data

            addItemBtn.setOnClickListener {
//                 todo: add a new Date, turn it into a string (check if it can be turned back into a Date)
                vm.insertRental(RentedItem(
                    // todo:
                ))
                dialog.dismiss()
            }
            acceptItemBtn.setOnClickListener {
                updateRentalObject()
                dialog.dismiss()
            }
            deleteItemBtn.setOnClickListener {
                // todo:
            }
            addImgBtn.setOnClickListener {
                // todo:
            }
            duplicateItemBtn.setOnClickListener {
                // todo:
            }
        }
        showCorrectFab(addOrUpdate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    // HELPERS //
    private fun updateRentalObject() {
        binding?.apply {
            vm.currentRental.roomNumber = roomNumEt.text.toString().toInt();
            vm.currentRental.itemType = when (chooseTypeRadio.checkedRadioButtonId) {
                bikeBtn.id -> RentedItem.ItemType.BIKE
                paddleBoardBtn.id -> RentedItem.ItemType.PADDLE_BOARD
                else -> RentedItem.ItemType.CHAIR
            }
            vm.currentRental.number =numEt.text.toString().toInt();
            vm.currentRental.lock = lockNumEt.text.toString().toInt();
            vm.currentRental.dailyRentals = dailyRentalsSwitch.isChecked;
            vm.currentRental.paid = paidSwitch.isChecked;
            vm.updateRental(vm.currentRental)
        }
    }
    private fun showCorrectFab(addOrUpdatePassed: String) {
        binding!!.apply {
            when (addOrUpdatePassed) {
                BottomSheetAction.ADD.toString() -> addItemBtn.visibility = View.VISIBLE
                BottomSheetAction.UPDATE.toString() -> acceptItemBtn.visibility = View.VISIBLE
            }
        }
    }
    // HELPERS //

    companion object {
        fun newInstance(addOrUpdatePassed: String, passedRental: RentedItem) =
            BottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(SHEET_STR_KEY, addOrUpdatePassed)
                    addOrUpdate = addOrUpdatePassed
                    currentRental = passedRental
                }
        }
    }
}