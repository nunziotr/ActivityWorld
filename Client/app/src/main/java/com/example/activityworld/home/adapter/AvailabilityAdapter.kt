package com.example.activityworld.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.activityworld.databinding.GridListAvailabilityBinding
import com.example.activityworld.home.model.FieldAvailability
import com.example.activityworld.home.utilities.convertTimeToFormatted
import java.util.*


const val TAG_AVAILABILITY = "AvailabilityAdapter"

/**
 * Adapter for the [RecyclerView] in [AvailabilityListFragment]. Displays [FieldAvailability] data object.
 * Use ListAdapter to optimize the refresh of the RecyclerView List
 */
class AvailabilityAdapter(private val clickListener: AvailabilityListener) :
    ListAdapter<FieldAvailability, AvailabilityAdapter.AvailabilityViewHolder>(
        AvailabilityDiffCallback()
    ) {

    // -1: no default selection
    // 0: 1st item is selected
    var selectedItemPos = -1



    /**
     * Initialize view elements
     * Provide a reference to the views for each data item
     * Each data item is just a FieldType object.
     */
    class AvailabilityViewHolder private constructor(val binding: GridListAvailabilityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /*
        // Binding the objects
        fun bind(item: FieldAvailability, clickListener: AvailabilityListener) {
            // Set the availability for dataBinding
            binding.availability = item

            // If the item is the clicked one, then make it selected
            Log.v("AVAILABILITY_ADAPTER_B", "Is selectedPos? ${selectedItemPos==adapterPosition}")
            binding.buttonAvailability.isSelected = selectedItemPos == adapterPosition
            Log.v("AVAILABILITY_ADAPTER_B", "Is button selected? ${binding.buttonAvailability.isSelected}")

            binding.buttonAvailability.setOnClickListener {
                it.isSelected = !it.isSelected
                //notifyItemChanged(selectedItemPos)

                selectedItemPos = adapterPosition
                clickListener.onClick(item)
            }

            // Slightly improves the performances
            binding.executePendingBindings()
        }
        */

        companion object {
            fun from(parent: ViewGroup): AvailabilityViewHolder {
                // Create a new view
                val layoutInflater = LayoutInflater.from(parent.context)
                // Get a reference to the binding object.
                val binding = GridListAvailabilityBinding.inflate(layoutInflater, parent, false)

                return AvailabilityViewHolder(binding)
            }
        }

    }

    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AvailabilityViewHolder {

        return AvailabilityViewHolder.from(parent)
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: AvailabilityViewHolder, position: Int) {
        // Retrieve the Availability in the given position
        val item = getItem(position)
        holder.binding.availability = item
        // Update the view with the fieldType data
        //holder.bind(item, clickListener)

        // If the item is the clicked one, then make it selected
        Log.v("AVAILABILITY_ADAPTER_B", "Is selectedPos? ${selectedItemPos==position}")
        holder.binding.buttonAvailability.isSelected = selectedItemPos == position
        Log.v("AVAILABILITY_ADAPTER_B", "Is button selected? ${holder.binding.buttonAvailability.isSelected}")

        holder.binding.buttonAvailability.setOnClickListener {
            it.isSelected = !it.isSelected
            notifyItemChanged(selectedItemPos)

            selectedItemPos = position
            clickListener.onClick(item)
        }

        holder.binding.executePendingBindings()
    }
}

/**
 * DiffUtil helps the List Adapter to find out which item has actually changed and should be updated
 */
class AvailabilityDiffCallback : DiffUtil.ItemCallback<FieldAvailability>() {
    override fun areItemsTheSame(oldItem: FieldAvailability, newItem: FieldAvailability): Boolean {
        // Test whether the two passed-in FieldAvailability items, oldItem and newItem, are the same
        // through their ID
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: FieldAvailability,
        newItem: FieldAvailability
    ): Boolean {
        // Check whether oldItem and newItem contain the same data; that is, whether they are equal.
        return oldItem == newItem
    }
}

class AvailabilityListener(val clickListener: (availabilityID: Long) -> Unit) {
    fun onClick(availability: FieldAvailability) {


        Log.v(TAG_AVAILABILITY, "Availability Clicked ID: ${availability.id}")
        Log.v(TAG_AVAILABILITY, "Availability Clicked FIELD: ${availability.field.id}")
        Log.v(TAG_AVAILABILITY, "Availability Clicked Date: ${availability.date}")
        val time = convertTimeToFormatted(availability.startingTime, availability.endingTime)
        Log.v(TAG_AVAILABILITY, "Availability Clicked TIME: $time")
        Log.v(TAG_AVAILABILITY, "Availability Clicked Status: ${availability.status}")

        clickListener(availability.id)
    }
}