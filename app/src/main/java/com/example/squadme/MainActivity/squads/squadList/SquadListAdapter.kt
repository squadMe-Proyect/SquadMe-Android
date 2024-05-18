package com.example.squadme.MainActivity.squads.squadList

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.squadme.data.Models.LineUp
import com.example.squadme.databinding.SquadListItemBinding

class SquadListAdapter(private val onClick:((View, LineUp) ->Unit)) :
    ListAdapter<LineUp, SquadListAdapter.SquadViewHolder>(DiffCallback) {

    inner class SquadViewHolder(private val binding: SquadListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindLineUp(lineUp: LineUp) {
            binding.squadTitle.text = lineUp.name
            binding.squadFormation.text = lineUp.lineUp
            //binding.squadFormation.text = lineUp.formation
            Log.d("SquadListAdapter", "Binding LineUp: ${lineUp.name}, Formation: ${lineUp.lineUp}")
            binding.squadCard.setOnClickListener {
                onClick(it, lineUp)
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<LineUp>() {
        override fun areItemsTheSame(oldItem: LineUp, newItem: LineUp): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: LineUp, newItem: LineUp): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SquadViewHolder {
        val binding = SquadListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SquadViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SquadViewHolder, position: Int) {
        val lineUp = getItem(position)
        holder.bindLineUp(lineUp)
    }
}