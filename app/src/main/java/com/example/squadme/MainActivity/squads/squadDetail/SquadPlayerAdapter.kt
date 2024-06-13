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

/**
 * Adapter for displaying players in a RecyclerView within the SquadDetailFragment.
 *
 * @param players List of players to be displayed.
 */
class SquadPlayerAdapter(
    private val players: List<Player>,
) : RecyclerView.Adapter<SquadPlayerAdapter.PlayerViewHolder>() {


    /**
     * ViewHolder for displaying individual player items.
     *
     * @param binding View binding object for the player item layout.
     */
    inner class PlayerViewHolder(private val binding: ItemPlayerDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(player: Player) {
            binding.playerName.text = player.name

            Glide.with(itemView.context)
                .load(player.picture)
                .into(binding.playerImage)
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
        val binding = ItemPlayerDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
     * Returns the total number of players in the list.
     *
     * @return The total number of players in the list.
     */
    override fun getItemCount(): Int = players.size

}
