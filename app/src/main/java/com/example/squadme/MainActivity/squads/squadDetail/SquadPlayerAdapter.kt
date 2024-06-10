package com.example.squadme.MainActivity.squads.squadDetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.squadme.R
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.ItemPlayerDetailBinding


class SquadPlayerAdapter(
    private val players: List<Player>,
) : RecyclerView.Adapter<SquadPlayerAdapter.PlayerViewHolder>() {

    /*
    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerName: TextView = itemView.findViewById(R.id.playerName)
        val playerImage: ImageView = itemView.findViewById(R.id.playerImage)

        fun bind(player: Player) {
            playerName.text = player.name

            Glide.with(itemView.context)
                .load(player.picture)
                .into(playerImage)

        }
    }
     */

    inner class PlayerViewHolder(private val binding: ItemPlayerDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(player: Player) {
            binding.playerName.text = player.name

            Glide.with(itemView.context)
                .load(player.picture)
                .into(binding.playerImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = ItemPlayerDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(players[position])
    }

    override fun getItemCount(): Int = players.size

}
