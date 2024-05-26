package com.example.squadme.MainActivity.squads.squadCreation

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

class PlayerAdapterDropdown(
    private val players: List<Player>,
    private val onPlayerSelected: (Player, Boolean) -> Unit
) : RecyclerView.Adapter<PlayerAdapterDropdown.PlayerViewHolder>() {

    private val selectedPlayers = mutableSetOf<Player>()

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerName: TextView = itemView.findViewById(R.id.itemName)
        val playerCheckbox: CheckBox = itemView.findViewById(R.id.spinnerCheckbox)
        val playerImage: ImageView = itemView.findViewById(R.id.playerImage)

        fun bind(player: Player) {
            playerName.text = player.name
            playerCheckbox.isChecked = selectedPlayers.contains(player)

            Glide.with(itemView.context)
                .load(player.picture)
                .into(playerImage)

            playerCheckbox.setOnCheckedChangeListener { _, isChecked ->
                onPlayerSelected(player, isChecked)
                if (isChecked) {
                    selectedPlayers.add(player)
                } else {
                    selectedPlayers.remove(player)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dropdown_item_player, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(players[position])
    }

    override fun getItemCount(): Int = players.size

    fun getSelectedPlayers(): List<Player> = selectedPlayers.toList()
}



