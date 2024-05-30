package com.example.squadme.MainActivity.matches.matchesUpdate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.squadme.data.Models.LineUp
import com.example.squadme.databinding.DropdownItemLineupBinding

class LineUpUpdateAdapterDropdown(
    private val lineUps: List<LineUp>,
    private var selectedLineUp: LineUp?,
    private val onLineUpSelected: (LineUp) -> Unit
) : RecyclerView.Adapter<LineUpUpdateAdapterDropdown.LineUpViewHolder>() {

    inner class LineUpViewHolder(private val binding: DropdownItemLineupBinding) :
        RecyclerView.ViewHolder(binding.root) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineUpViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DropdownItemLineupBinding.inflate(inflater, parent, false)
        return LineUpViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LineUpViewHolder, position: Int) {
        val lineUp = lineUps[position]
        holder.bindLineUp(lineUp)
    }

    override fun getItemCount(): Int = lineUps.size
}