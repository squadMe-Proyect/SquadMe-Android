package com.example.squadme.MainActivity.matches.matchesDetail


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.ItemPlayerDetailBinding


/**
 * Adapter for displaying a list of players in a match detail view.
 *
 * @param players List of Player objects to display.
 */
class MatchPlayerLineupAdapter(
    private val players: List<Player>,
) : RecyclerView.Adapter<MatchPlayerLineupAdapter.PlayerViewHolder>() {

    /**
     * ViewHolder class for holding player item views.
     *
     * @param binding ItemPlayerDetailBinding generated class based on the XML layout.
     */
    inner class PlayerViewHolder(private val binding: ItemPlayerDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        /**
         * Binds player data to the ViewHolder.
         *
         * @param player Player object to bind.
         */
        fun bind(player: Player) {
            binding.playerName.text = player.name
            Glide.with(itemView.context)
                .load(player.picture)
                .into(binding.playerImage)
        }
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The type of the new View.
     * @return A new PlayerViewHolder that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = ItemPlayerDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(players[position])
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of players in the list.
     */
    override fun getItemCount(): Int = players.size
}

