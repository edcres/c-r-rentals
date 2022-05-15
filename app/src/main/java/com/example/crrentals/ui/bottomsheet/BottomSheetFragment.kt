package com.example.crrentals.ui.bottomsheet

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.crrentals.R
import com.example.crrentals.databinding.FragmentBottomSheetBinding
import com.example.crrentals.ui.RentItemsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragment : BottomSheetDialogFragment() {

    private var binding: FragmentBottomSheetBinding? = null
    private lateinit var dialog: BottomSheetDialog
    private lateinit var vm: BottomSheetViewModel

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
        binding!!.apply {
            lifecycleOwner = viewLifecycleOwner
            // todo: to dismiss the dialog
            addItemBtn.setOnClickListener {
                dialog.dismiss()
            }
            acceptItemBtn.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}