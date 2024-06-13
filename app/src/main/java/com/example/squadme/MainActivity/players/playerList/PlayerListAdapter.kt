package com.example.squadme.MainActivity.players.playerList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.PlayerListItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


/**
 * Adapter for displaying a list of players in a RecyclerView.
 *
 * @param context The context in which the adapter is used
 * @param onClick Callback function invoked when a player item is clicked
 */
class PlayerListAdapter(private val context: Context, private val onClick:((View, Player) ->Unit)) :
    ListAdapter<Player, PlayerListAdapter.PlayerViewHolder>(DiffCallback) {

    /**
     * ViewHolder class for player items in the RecyclerView.
     *
     * @param binding View binding object for player item layout
     */
    inner class PlayerViewHolder(private val binding: PlayerListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Bind player data to the ViewHolder.
         *
         * @param player The player object to bind
         */
        fun bindPlayer(player: Player) {
            binding.playerTitle.text = player.name
            binding.playerPosition.text = player.position
            Glide.with(context)
                .load(player.picture)
                .apply(RequestOptions().centerCrop())
                .into(binding.playerImg)
            binding.playerCard.setOnClickListener {
                onClick(it, player)
            }
        }
    }

    /**
     * DiffCallback for calculating the difference between old and new items in the list.
     */
    object DiffCallback : DiffUtil.ItemCallback<Player>() {
        override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * Create ViewHolder when needed.
     *
     * @param parent The parent view group into which the ViewHolder will be added
     * @param viewType The type of view to create
     * @return PlayerViewHolder instance
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = PlayerListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlayerViewHolder(binding)
    }

    /**
     * Bind data to ViewHolder.
     *
     * @param holder The ViewHolder instance to bind data to
     * @param position The position of the item within the adapter's data set
     */
    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = getItem(position)
        holder.bindPlayer(player)
    }
}