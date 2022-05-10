package com.example.crrentals.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.crrentals.data.RentedItem
import com.example.crrentals.databinding.RentalListItemBinding

class RentalsAdapter(
    private val rentItemsViewModel: RentItemsViewModel,
    private val context: Context,
    private val viewLifecycleOwner: LifecycleOwner
) : ListAdapter<RentedItem, RentalsAdapter.RentalsViewHolder>(RentalsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RentalsViewHolder.from(rentItemsViewModel, context, viewLifecycleOwner, parent)

    override fun onBindViewHolder(rentalsViewHolder: RentalsViewHolder, position: Int) =
        rentalsViewHolder.bind(getItem(position))

    class RentalsViewHolder private constructor(
        private val rentItemsViewModel: RentItemsViewModel,
        private val context: Context,
        private val viewLifecycleOwner: LifecycleOwner,
        private val binding: RentalListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(rentedItem: RentedItem) {
            binding.apply {



                binding.executePendingBindings()
            }
        }

        companion object {
            fun from(
                rentItemsViewModel: RentItemsViewModel,
                context: Context,
                viewLifecycleOwner: LifecycleOwner,
                parent: ViewGroup
            ): RentalsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RentalListItemBinding.inflate(layoutInflater, parent, false)
                return RentalsViewHolder(rentItemsViewModel, context, viewLifecycleOwner, binding)
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