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

/**
 * Adapter for displaying a list of trainings in a RecyclerView within the TrainingListFragment.
 *
 * @param onClick Callback function triggered when a training item is clicked.
 */
class TrainingListAdapter(
    private val onClick: (View, Training) -> Unit
) : ListAdapter<Training, TrainingListAdapter.TrainingViewHolder>(DiffCallback) {

    /**
     * ViewHolder for displaying individual training items.
     *
     * @param binding View binding object for the training item layout.
     */
    inner class TrainingViewHolder(private val binding: TrainingListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds training data to the TrainingViewHolder.
         *
         * @param training The training object to bind.
         */
        fun bindTraining(training: Training) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

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

    /**
     * DiffUtil callback for calculating the difference between two lists of trainings.
     */
    object DiffCallback : DiffUtil.ItemCallback<Training>() {
        override fun areItemsTheSame(oldItem: Training, newItem: Training): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Training, newItem: Training): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * Creates a new TrainingViewHolder instance.
     *
     * @param parent The parent view group into which the new view will be added.
     * @param viewType The type of the new view.
     * @return A new TrainingViewHolder instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val binding = TrainingListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrainingViewHolder(binding)
    }

    /**
     * Binds training data to the TrainingViewHolder at the specified position.
     *
     * @param holder The TrainingViewHolder to bind data to.
     * @param position The position of the training item in the list.
     */
    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val training = getItem(position)
        holder.bindTraining(training)
    }
}

