package com.example.squadme.MainActivity.matches.matchesList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.squadme.data.Models.Match
import com.example.squadme.databinding.MatchListItemBinding
/**
 * Adapter class for displaying a list of matches in a RecyclerView.
 *
 * @param onClick Callback function to handle click events on match items.
 */
class MatchListAdapter( private val onClick:((View, Match) ->Unit)) :
    ListAdapter<Match, MatchListAdapter.MatchViewHolder>(DiffCallback) {

    /**
     * ViewHolder class for match items.
     *
     * @param binding The ViewBinding for a single match item.
     */
    inner class MatchViewHolder(private val binding: MatchListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds a match object to the ViewHolder.
         *
         * @param match The match object to bind.
         */
        fun bindMatch(match: Match) {
            binding.matchDate.text = match.date
            binding.matchOpponent.text = match.opponent
            binding.matchResult.text = match.result
            binding.matchCard.setOnClickListener {
                onClick(it, match)
            }
        }
    }

    /**
     * DiffCallback object for calculating the difference between two non-null items in a list.
     */
    object DiffCallback : DiffUtil.ItemCallback<Match>() {
        /**
         * Checks if two items represent the same match.
         *
         * @param oldItem The old item.
         * @param newItem The new item.
         * @return True if the items represent the same match, false otherwise.
         */
        override fun areItemsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem == newItem
        }

        /**
         * Checks if the contents of two items are the same.
         *
         * @param oldItem The old item.
         * @param newItem The new item.
         * @return True if the contents of the items are the same, false otherwise.
         */
        override fun areContentsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * Creates a new ViewHolder for a match item.
     *
     * @param parent The parent ViewGroup into which the new view will be added.
     * @param viewType The view type of the new view.
     * @return A new MatchViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val binding = MatchListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MatchViewHolder(binding)
    }

    /**
     * Binds a match item at the specified position to the ViewHolder.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = getItem(position)
        holder.bindMatch(match)
    }
}