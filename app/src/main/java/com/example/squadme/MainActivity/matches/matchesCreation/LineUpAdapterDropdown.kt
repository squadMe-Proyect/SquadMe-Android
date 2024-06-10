package com.example.squadme.MainActivity.matches.matchesCreation


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.squadme.data.Models.LineUp
import com.example.squadme.databinding.DropdownItemLineupBinding

/**
 * A RecyclerView Adapter for displaying a list of LineUps in a dropdown menu.
 *
 * @property lineUps The list of LineUp items to be displayed.
 * @property onLineUpSelected A lambda function to be invoked when a LineUp item is selected.
 */
class LineUpAdapterDropdown(
    private val lineUps: List<LineUp>,
    private val onLineUpSelected: (LineUp) -> Unit
): RecyclerView.Adapter<LineUpAdapterDropdown.LineUpViewHolder>(){

    // The currently selected LineUp item
    private var selectedLineUp : LineUp? = null

    /**
     * ViewHolder class for the LineUpAdapterDropdown. Manages individual item views.
     *
     * @property binding The binding object for the item layout.
     */
    inner class LineUpViewHolder(private val binding: DropdownItemLineupBinding):
        RecyclerView.ViewHolder(binding.root) {
        /**
         * Binds a LineUp item to the item view.
         *
         * @param lineUp The LineUp item to be bound.
         */
            fun bindLineUp(lineUp: LineUp){
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
     * Creates a new ViewHolder for the RecyclerView.
     *
     * @param parent The parent ViewGroup.
     * @param viewType The view type of the new View.
     * @return A new LineUpViewHolder.
     */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineUpViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DropdownItemLineupBinding.inflate(inflater, parent, false)
        return LineUpViewHolder(binding)
    }

    /**
     * Binds the data to the ViewHolder.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item within the adapter's data set.
     */

    override fun onBindViewHolder(holder: LineUpViewHolder, position: Int) {
        val lineUp = lineUps[position]
        holder.bindLineUp(lineUp)
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The number of items in the data set.
     */

    override fun getItemCount(): Int = lineUps.size
}