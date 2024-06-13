package com.example.squadme.MainActivity.squads.squadList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.squadme.data.Models.LineUp
import com.example.squadme.databinding.SquadListItemBinding


/**
 * Adapter for displaying a list of squads in a RecyclerView.
 *
 * @param onClick Callback function invoked when a squad item is clicked.
 */
class SquadListAdapter(private val onClick:((View, LineUp) ->Unit)) :
    ListAdapter<LineUp, SquadListAdapter.SquadViewHolder>(DiffCallback) {

    /**
     * ViewHolder for displaying individual squad items.
     *
     * @param binding View binding object for the squad item layout.
     */
    inner class SquadViewHolder(private val binding: SquadListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds squad data to the ViewHolder.
         *
         * @param lineUp The LineUp object containing squad data to be displayed.
         */
        fun bindLineUp(lineUp: LineUp) {
            binding.squadTitle.text = lineUp.name
            binding.squadFormation.text = lineUp.lineUp
            binding.squadCard.setOnClickListener {
                onClick(it, lineUp)
            }
        }
    }

    /**
     * DiffUtil callback for calculating the difference between two lists of squads.
     */
    object DiffCallback : DiffUtil.ItemCallback<LineUp>() {
        override fun areItemsTheSame(oldItem: LineUp, newItem: LineUp): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: LineUp, newItem: LineUp): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * Creates a new SquadViewHolder instance.
     *
     * @param parent The parent view group into which the new view will be added.
     * @param viewType The type of the new view.
     * @return A new SquadViewHolder instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SquadViewHolder {
        val binding = SquadListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SquadViewHolder(binding)
    }

    /**
     * Binds squad data to the SquadViewHolder at the specified position.
     *
     * @param holder The SquadViewHolder to bind data to.
     * @param position The position of the squad item in the list.
     */
    override fun onBindViewHolder(holder: SquadViewHolder, position: Int) {
        val lineUp = getItem(position)
        holder.bindLineUp(lineUp)
    }
}