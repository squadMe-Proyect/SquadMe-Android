package com.example.squadme.MainActivity.matches.matchesDetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.squadme.R
import com.example.squadme.data.Models.Player

class MatchPlayerLineupAdapter(
    private val players: List<Player>,
) : RecyclerView.Adapter<MatchPlayerLineupAdapter.PlayerViewHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_player_detail, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(players[position])
    }

    override fun getItemCount(): Int = players.size

}