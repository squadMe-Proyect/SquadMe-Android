package com.example.squadme.MainActivity.squads.squadUpdate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.ItemPlayerUpdateBinding

/**
 * Adapter for displaying a list of players in a RecyclerView for squad update.
 *
 * @param players The list of players to display.
 * @param onDeleteClick Callback function invoked when the delete button is clicked for a player.
 */
class SquadPlayerUpdateAdapter(
    private val players: MutableList<Player>,
    private val onDeleteClick: (Player) -> Unit
) : RecyclerView.Adapter<SquadPlayerUpdateAdapter.PlayerViewHolder>() {

    /**
     * ViewHolder for displaying individual player items in the squad update list.
     *
     * @param binding View binding object for the player item layout.
     */
    inner class PlayerViewHolder(private val binding: ItemPlayerUpdateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        /**
         * Binds player data to the ViewHolder.
         *
         * @param player The Player object containing data to be displayed.
         */
        fun bind(player: Player) {
            binding.playerName.text = player.name
            binding.deleteButton.setOnClickListener {
                onDeleteClick(player)
                players.remove(player)
                notifyDataSetChanged()
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
        val binding = ItemPlayerUpdateBinding.inflate(
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
        holder.bind(players[position])
    }

    /**
     * Updates the adapter's data with new list of players.
     *
     * @param newPlayers The new list of players to display.
     */
    fun updateData(newPlayers: List<Player>) {
        players.clear()
        players.addAll(newPlayers)
        notifyDataSetChanged()
    }

    /**
     * Returns the total number of player items in the list.
     *
     * @return The total number of player items.
     */
    override fun getItemCount(): Int = players.size
}