package com.example.crrentals.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.crrentals.R
import com.example.crrentals.data.RentedItem
import com.example.crrentals.databinding.RentalListItemBinding

private const val TAG = "RentAdapter__TAG"

class RentalsAdapter(
    private val vm: RentItemsViewModel,
) : ListAdapter<RentedItem, RentalsAdapter.RentalsViewHolder>(RentalsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RentalsViewHolder.from(vm, parent)

    override fun onBindViewHolder(rentalsViewHolder: RentalsViewHolder, position: Int) =
        rentalsViewHolder.bind(getItem(position))

    class RentalsViewHolder private constructor(
        private val vm: RentItemsViewModel,
        private val binding: RentalListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(rentedItem: RentedItem) {
            binding.apply {
                if (rentedItem.imageUri != null) {
                    Glide.with(rentalImage.context)
                        .load(rentedItem.imageUri!!.toUri())
                        .apply(
                            RequestOptions()
                                .placeholder(R.drawable.loading_animation)
                                .error(R.drawable.ic_baseline_broken_image)
                        )
                        .into(rentalImage)
                    Log.d(TAG, "bind: uri:\n${rentedItem.roomNumber}\t${rentedItem.imageUri}")
                } else {
                    rentalImage.visibility = View.GONE
                    Log.d(TAG, "bind: uri:\n${rentedItem.roomNumber}\t${rentedItem.imageUri}")
                }
                rentalNameTxt.text = rentedItem.itemType.type.uppercase()
                roomNumTxt.text = rentedItem.roomNumber?.toString() ?: ""
                val rentedOnString = "Rented on ${rentedItem.time}"
                timeStartedTxt.text = rentedOnString
                rentalItemContainer.setOnClickListener {
                    vm.setItemToEdit(rentedItem)
                }
                binding.executePendingBindings()
            }
        }

        companion object {
            fun from(
                vm: RentItemsViewModel,
                parent: ViewGroup
            ): RentalsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RentalListItemBinding.inflate(layoutInflater, parent, false)
                return RentalsViewHolder(vm, binding)
            }
        }
    }

    class RentalsDiffCallback : DiffUtil.ItemCallback<RentedItem>() {
        override fun areItemsTheSame(oldItem: RentedItem, newItem: RentedItem): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: RentedItem, newItem: RentedItem): Boolean {
            return oldItem == newItem
        }
    }
}