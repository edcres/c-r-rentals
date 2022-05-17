package com.example.crrentals.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.crrentals.R
import com.example.crrentals.data.RentedItem
import com.example.crrentals.databinding.RentalListItemBinding

class RentalsAdapter(
    private val vm: RentItemsViewModel,
    private val context: Context,
    private val viewLifecycleOwner: LifecycleOwner
) : ListAdapter<RentedItem, RentalsAdapter.RentalsViewHolder>(RentalsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RentalsViewHolder.from(vm, context, viewLifecycleOwner, parent)

    override fun onBindViewHolder(rentalsViewHolder: RentalsViewHolder, position: Int) =
        rentalsViewHolder.bind(getItem(position))

    class RentalsViewHolder private constructor(
        private val vm: RentItemsViewModel,
        private val context: Context,
        private val viewLifecycleOwner: LifecycleOwner,
        private val binding: RentalListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(rentedItem: RentedItem) {
            binding.apply {
                Glide.with(rentalImage.context)
                    .load(rentedItem.imageUri?:"noUri".toUri())
                    .apply(
                        RequestOptions()
                            .placeholder(R.drawable.loading_animation)
                            .error(R.drawable.ic_baseline_broken_image))
                    .into(rentalImage)
                binding.executePendingBindings()

                rentalItemContainer.setOnClickListener {
                    vm.setItemToEdit(rentedItem)
                }
            }
        }

        companion object {
            fun from(
                vm: RentItemsViewModel,
                context: Context,
                viewLifecycleOwner: LifecycleOwner,
                parent: ViewGroup
            ): RentalsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RentalListItemBinding.inflate(layoutInflater, parent, false)
                return RentalsViewHolder(vm, context, viewLifecycleOwner, binding)
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