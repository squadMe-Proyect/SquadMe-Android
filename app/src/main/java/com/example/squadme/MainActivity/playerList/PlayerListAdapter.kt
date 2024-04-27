package com.example.squadme.MainActivity.playerList

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.PlayerListItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class PlayerListAdapter(private val context: Context) :
    ListAdapter<Player, PlayerListAdapter.PlayerViewHolder>(DiffCallback) {

    inner class PlayerViewHolder(private val binding: PlayerListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindPlayer(player: Player) {
            binding.playerTitle.text = player.name
            Glide.with(context)
                .load(player.picture)
                .apply(RequestOptions().centerCrop())
                .into(binding.playerImg)
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<Player>() {
        override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = PlayerListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = getItem(position)
        holder.bindPlayer(player)
    }
}