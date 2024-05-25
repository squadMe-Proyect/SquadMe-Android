package com.example.squadme.MainActivity.training.trainingUpdate

import androidx.recyclerview.widget.RecyclerView
import com.example.squadme.databinding.ItemExerciseUpdateBinding
import android.view.ViewGroup
import android.view.LayoutInflater

class TrainingExerciseUpdateAdapter(
    private val exercises: MutableList<String>,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<TrainingExerciseUpdateAdapter.ExerciseViewHolder>() {

    inner class ExerciseViewHolder(private val binding: ItemExerciseUpdateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(exercise: String) {
            binding.exerciseTextView.text = exercise
            binding.deleteButton.setOnClickListener {
                onDeleteClick(exercise)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemExerciseUpdateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(exercises[position])
    }

    override fun getItemCount(): Int = exercises.size
}