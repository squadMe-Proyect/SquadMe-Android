package com.example.squadme.MainActivity.squads.squadUpdate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.DropdownItemPlayerBinding

class PlayerAdapterDropdownUpdate(
    private val playerList: List<Player>,
    private val selectedPlayers: MutableSet<Player>,
    private val onPlayerSelected: (Player, Boolean) -> Unit
    ) : RecyclerView.Adapter<PlayerAdapterDropdownUpdate.PlayerViewHolder>() {

    inner class PlayerViewHolder(private val binding: DropdownItemPlayerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(player: Player) {
            binding.itemName.text = player.name
            binding.spinnerCheckbox.setOnCheckedChangeListener(null)
            binding.spinnerCheckbox.isChecked = selectedPlayers.contains(player)

            binding.spinnerCheckbox.setOnCheckedChangeListener { _, isChecked ->
                onPlayerSelected(player, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = DropdownItemPlayerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(playerList[position])
    }

    override fun getItemCount(): Int = playerList.size
}