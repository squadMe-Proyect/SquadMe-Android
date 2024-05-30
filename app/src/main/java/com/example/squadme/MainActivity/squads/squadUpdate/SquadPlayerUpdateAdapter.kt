package com.example.squadme.MainActivity.squads.squadUpdate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.ItemPlayerUpdateBinding

class SquadPlayerUpdateAdapter(
    private val players: MutableList<Player>,
    private val onDeleteClick: (Player) -> Unit
) : RecyclerView.Adapter<SquadPlayerUpdateAdapter.PlayerViewHolder>() {

    inner class PlayerViewHolder(private val binding: ItemPlayerUpdateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(player: Player) {
            binding.playerName.text = player.name
            binding.deleteButton.setOnClickListener {
                onDeleteClick(player)
                players.remove(player)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = ItemPlayerUpdateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(players[position])
    }

    fun updateData(newPlayers: List<Player>) {
        players.clear()
        players.addAll(newPlayers)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = players.size
}