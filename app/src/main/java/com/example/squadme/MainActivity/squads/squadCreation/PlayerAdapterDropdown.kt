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


/**
 * Adapter for displaying a list of players in a dropdown style RecyclerView.
 *
 * @param players List of Player objects to be displayed.
 * @param onPlayerSelected Callback function invoked when a player is selected or deselected.
 */
class PlayerAdapterDropdown(
    private val players: List<Player>,
    private val onPlayerSelected: (Player, Boolean) -> Unit
) : RecyclerView.Adapter<PlayerAdapterDropdown.PlayerViewHolder>() {

    private val selectedPlayers = mutableSetOf<Player>()

    /**
     * ViewHolder class for holding player item views.
     *
     * @param itemView View representing each item in the RecyclerView.
     */
    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val playerName: TextView = itemView.findViewById(R.id.itemName)
        private val playerCheckbox: CheckBox = itemView.findViewById(R.id.spinnerCheckbox)
        private val playerImage: ImageView = itemView.findViewById(R.id.playerImage)

        /**
         * Binds player data to the ViewHolder.
         *
         * @param player Player object containing player data to be displayed.
         */
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

    /**
     * Creates a new PlayerViewHolder instance.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return New instance of PlayerViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dropdown_item_player, parent, false)
        return PlayerViewHolder(view)
    }

    /**
     * Binds data to the PlayerViewHolder at the specified position.
     *
     * @param holder The PlayerViewHolder to bind data to.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(players[position])
    }

    /**
     * Returns the total number of players in the data set held by the adapter.
     *
     * @return The total number of players.
     */
    override fun getItemCount(): Int = players.size
}



