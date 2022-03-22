package com.example.activityworld.booking.adapter

import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.activityworld.booking.model.Participant
import com.example.activityworld.databinding.ListItemParticipantBinding
import com.google.android.material.textfield.TextInputEditText
import java.util.*


/**
 * Adapter for the [RecyclerView] in [BookingFragment]. Displays [Participant] data object.
 *
 * @param clickListener
 * Used to set a listener when an item inside the RecyclerView gets clicked
 */
class ParticipantAdapter(private val clickListener: ParticipantListener): ListAdapter<Participant, ParticipantAdapter.ParticipantViewHolder>(ParticipantDiffCallback()) {

    /**
     * Initialize view elements
     * Provide a reference to the views for each data item
     * Each data item is just an Participant object.
     */
    class ParticipantViewHolder private constructor(val binding: ListItemParticipantBinding): RecyclerView.ViewHolder(binding.root) {

        // Binding the objects
        fun bind(item: Participant, clickListener: ParticipantListener) {
            // Set the availability for dataBinding
            binding.participant = item

            // Set the clickListener
            binding.clickListener = clickListener

            // Slightly improves the performances
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ParticipantViewHolder {
                // Create a new view
                val layoutInflater = LayoutInflater.from(parent.context)
                // Get a reference to the binding object.
                val binding = ListItemParticipantBinding.inflate(layoutInflater, parent, false)

                return ParticipantViewHolder(binding)
            }
        }
    }

    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        return ParticipantViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        // Retrieve the Availability in the given position
        val participant =  getItem(position)

        // Update the view with the participant data
        holder.bind(participant, clickListener)
    }

    private fun setAnimation(viewToAnimate: View) {
        if (viewToAnimate.animation == null) {
            val animation =
                AnimationUtils.loadAnimation(viewToAnimate.context, android.R.anim.slide_in_left)
            viewToAnimate.animation = animation
        }
    }
}

/**
 * DiffUtil helps the List Adapter to find out which item has actually changed and should be updated
 */
class ParticipantDiffCallback : DiffUtil.ItemCallback<Participant>() {
    override fun areItemsTheSame(oldItem: Participant, newItem: Participant): Boolean {
        // Test whether the two passed-in Participant items, oldItem and newItem, are the same
        // through their ID
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Participant,
        newItem: Participant
    ): Boolean {
        // Check whether oldItem and newItem contain the same data; that is, whether they are equal.
        return oldItem == newItem
    }
}

class ParticipantListener(val clickListener: (participantID: Long) -> Unit) {
    fun onClick(participant: Participant) = clickListener(participant.id)
}