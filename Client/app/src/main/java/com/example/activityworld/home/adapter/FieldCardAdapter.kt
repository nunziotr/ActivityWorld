package com.example.activityworld.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.activityworld.databinding.GridListFieldBinding
import com.example.activityworld.home.constant.FieldType

const val TAG_FIELD_CARD2 = "Test"

/**
 * Adapter for the [RecyclerView] in [FieldListFragment]. Displays [FieldType] data object.
 *
 * @param clickListener
 * Used to set a listener when an item inside the RecyclerView gets clicked
 */
class FieldCardAdapter(private val clickListener: FieldCardListener): ListAdapter<FieldType, FieldCardAdapter.FieldCardViewHolder>(FieldTypeDiffCallBack()) {

    /**
     * Initialize view elements
     * Provide a reference to the views for each data item
     * Each data item is just an FieldType object.
     */
    class FieldCardViewHolder private constructor(private val binding: GridListFieldBinding): RecyclerView.ViewHolder(binding.root) {

        // Binding the objects
        fun bind(fieldType: FieldType, clickListener: FieldCardListener) {
            binding.type = fieldType
            binding.clickListener = clickListener

            binding.executePendingBindings()
        }

        companion object {
            fun from(fieldCardAdapter: FieldCardAdapter, parent: ViewGroup): FieldCardViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = GridListFieldBinding.inflate(layoutInflater, parent, false)
                return FieldCardViewHolder(binding)
            }
        }
    }

    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldCardViewHolder {
        // Create a new view
        return FieldCardViewHolder.from(this, parent)
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: FieldCardViewHolder, position: Int) {
        // Retrieve the FieldType in the given position
        val fieldType = getItem(position)

        // Update the view with the fieldType data
        holder.bind(fieldType, clickListener)
    }
}

class FieldTypeDiffCallBack: DiffUtil.ItemCallback<FieldType>() {
    override fun areItemsTheSame(oldItem: FieldType, newItem: FieldType): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: FieldType, newItem: FieldType): Boolean {
        return oldItem == newItem
    }
}

class FieldCardListener(val clickListener: (FieldType) -> Unit) {
    fun onClick(type: FieldType) = clickListener(type)
}