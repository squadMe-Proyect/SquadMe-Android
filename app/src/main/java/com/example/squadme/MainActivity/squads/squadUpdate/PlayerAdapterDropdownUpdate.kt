package com.example.squadme.MainActivity.squads.squadUpdate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.DropdownItemPlayerBinding

/**
 * Adapter for displaying a list of players in a dropdown with checkboxes.
 *
 * @param playerList The list of players to display.
 * @param selectedPlayers Set containing currently selected players.
 * @param onPlayerSelected Callback function invoked when a player is selected or deselected.
 */
class PlayerAdapterDropdownUpdate(
    private val playerList: List<Player>,
    private val selectedPlayers: MutableSet<Player>,
    private val onPlayerSelected: (Player, Boolean) -> Unit
    ) : RecyclerView.Adapter<PlayerAdapterDropdownUpdate.PlayerViewHolder>() {

    /**
     * ViewHolder for displaying individual player items in the dropdown.
     *
     * @param binding View binding object for the player item layout.
     */
    inner class PlayerViewHolder(private val binding: DropdownItemPlayerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds player data to the ViewHolder.
         *
         * @param player The Player object containing data to be displayed.
         */
        fun bind(player: Player) {
            binding.itemName.text = player.name
            binding.spinnerCheckbox.setOnCheckedChangeListener(null)
            binding.spinnerCheckbox.isChecked = selectedPlayers.contains(player)
            Glide.with(itemView.context)
                .load(player.picture)
                .into(binding.playerImage)

            binding.spinnerCheckbox.setOnCheckedChangeListener { _, isChecked ->
                onPlayerSelected(player, isChecked)
            }
        }
    }

    /**
     * Creates a new PlayerViewHolder instance.
     *
     * @param parent The parent view group into which the new view will be added.
     * @param viewType The type of the new view.
     * @return A new PlayerViewHolder instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = DropdownItemPlayerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlayerViewHolder(binding)
    }

    /**
     * Binds player data to the PlayerViewHolder at the specified position.
     *
     * @param holder The PlayerViewHolder to bind data to.
     * @param position The position of the player item in the list.
     */
    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(playerList[position])
    }

    /**
     * Returns the total number of player items in the list.
     *
     * @return The total number of player items.
     */
    override fun getItemCount(): Int = playerList.size
}