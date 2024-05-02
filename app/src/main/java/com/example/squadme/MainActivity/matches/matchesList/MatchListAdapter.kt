package com.example.squadme.MainActivity.matches.matchesList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.squadme.data.Models.Match
import com.example.squadme.databinding.MatchListItemBinding
class MatchListAdapter(private val context: Context, private val onClick:((View, Match) ->Unit)) :
    ListAdapter<Match, MatchListAdapter.MatchViewHolder>(DiffCallback) {

    inner class MatchViewHolder(private val binding: MatchListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindMatch(match: Match) {
            binding.matchOpponent.text = match.opponent
            binding.matchResult.text = match.result
            binding.matchCard.setOnClickListener {
                onClick(it, match)
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<Match>() {
        override fun areItemsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val binding = MatchListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = getItem(position)
        holder.bindMatch(match)
    }
}