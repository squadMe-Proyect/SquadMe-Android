package com.example.squadme.MainActivity.matches.matchesUpdate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.squadme.data.Models.LineUp
import com.example.squadme.databinding.DropdownItemLineupBinding

/**
 * Adapter for displaying a list of LineUps in a dropdown menu for updating purposes.
 *
 * @param lineUps List of LineUp objects to display.
 * @param selectedLineUp The currently selected LineUp.
 * @param onLineUpSelected Callback function when a LineUp is selected.
 */
class LineUpUpdateAdapterDropdown(
    private val lineUps: List<LineUp>,
    private var selectedLineUp: LineUp?,
    private val onLineUpSelected: (LineUp) -> Unit
) : RecyclerView.Adapter<LineUpUpdateAdapterDropdown.LineUpViewHolder>() {

    /**
     * ViewHolder class to hold and manage the UI elements of each LineUp item.
     *
     * @param binding View binding object for each LineUp item.
     */
    inner class LineUpViewHolder(private val binding: DropdownItemLineupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        /**
         * Binds a LineUp object to the ViewHolder.
         *
         * @param lineUp The LineUp object to bind.
         */
        fun bindLineUp(lineUp: LineUp) {
            binding.itemName.text = lineUp.name
            binding.spinnerCheckbox.isChecked = lineUp == selectedLineUp

            binding.spinnerCheckbox.setOnClickListener {
                selectedLineUp = lineUp
                onLineUpSelected(lineUp)
                notifyDataSetChanged()
            }

            itemView.setOnClickListener {
                selectedLineUp = lineUp
                onLineUpSelected(lineUp)
                notifyDataSetChanged()
            }
        }
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineUpViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DropdownItemLineupBinding.inflate(inflater, parent, false)
        return LineUpViewHolder(binding)
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: LineUpViewHolder, position: Int) {
        val lineUp = lineUps[position]
        holder.bindLineUp(lineUp)
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int = lineUps.size
}