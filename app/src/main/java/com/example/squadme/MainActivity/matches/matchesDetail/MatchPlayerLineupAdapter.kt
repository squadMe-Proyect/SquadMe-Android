package com.example.squadme.MainActivity.matches.matchesDetail

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.squadme.R
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.ItemPlayerDetailBinding
/*
/**
 * Adapter class for displaying a list of players in a RecyclerView.
 *
 * @param players List of players to display.
 */
class MatchPlayerLineupAdapter(
    private val players: List<Player>,
) : RecyclerView.Adapter<MatchPlayerLineupAdapter.PlayerViewHolder>() {

    inner class PlayerViewHolder(private val binding: ItemPlayerDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(player: Player) {
            binding.playerName.text = player.name

            Glide.with(itemView.context)
                .load(player.picture)
                .into(binding.playerImage)
        }
    }

    /**
     * Creates a new ViewHolder for a player item.
     *
     * @param parent The parent ViewGroup into which the new view will be added.
     * @param viewType The view type of the new view.
     * @return A new PlayerViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = ItemPlayerDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    /**
     * Binds a player item at the specified position to the ViewHolder.
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
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int = players.size

}
 */


/*
class MatchPlayerLineupAdapter(
    private val players: List<Player>,
) : RecyclerView.Adapter<MatchPlayerLineupAdapter.PlayerViewHolder>() {

    inner class PlayerViewHolder(private val binding: ItemPlayerDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(player: Player) {
            binding.playerName.text = player.name

            val pictureUrl = player.picture
            if (!pictureUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(pictureUrl)
                    .placeholder(R.mipmap.entreno1) // Imagen de marcador de posición mientras se carga la imagen
                    .error(R.mipmap.entreno1) // Imagen a mostrar si hay un error al cargar la imagen
                    .into(binding.playerImage)
            } else {
                binding.playerImage.setImageResource(R.mipmap.entreno1) // Imagen predeterminada si la URL es null o vacía
            }
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

 */

class MatchPlayerLineupAdapter(
    private val players: List<Player>,
) : RecyclerView.Adapter<MatchPlayerLineupAdapter.PlayerViewHolder>() {

    inner class PlayerViewHolder(private val binding: ItemPlayerDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(player: Player) {
            binding.playerName.text = player.name

            val pictureUrl = player.picture
            Log.d("MatchPlayerLineupAdapter", "Loading image URL: $pictureUrl")

            if (!pictureUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(pictureUrl)
                    .placeholder(R.mipmap.entreno1)
                    .error(R.mipmap.entreno1)
                    .into(binding.playerImage)
            } else {
                binding.playerImage.setImageResource(R.mipmap.entreno1)
            }
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

