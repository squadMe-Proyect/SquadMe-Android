package com.example.squadme.MainActivity.training.trainingList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.squadme.data.Models.Training
import com.example.squadme.databinding.TrainingListItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TrainingListAdapter(
    private val onClick: (View, Training) -> Unit
) : ListAdapter<Training, TrainingListAdapter.TrainingViewHolder>(DiffCallback) {

    inner class TrainingViewHolder(private val binding: TrainingListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindTraining(training: Training) {
            // Formatear la fecha y la hora
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            // Convertir Timestamp a Date antes de formatear
            val date = training.date?.toDate()
            if (date != null) {
                val formattedDate = dateFormat.format(date)
                val formattedTime = timeFormat.format(date)
                binding.trainingDayLabel.text = "Día del entreno: $formattedDate"
                binding.trainingDateTime.text = "Hora: $formattedTime"
            } else {
                binding.trainingDateTime.text = "Fecha no disponible"
            }

            binding.trainingCard.setOnClickListener {
                onClick(it, training)
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<Training>() {
        override fun areItemsTheSame(oldItem: Training, newItem: Training): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Training, newItem: Training): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val binding = TrainingListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrainingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val training = getItem(position)
        holder.bindTraining(training)
    }
}

