package com.example.squadme.MainActivity.training.trainingDetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.squadme.MainActivity.training.trainingList.TrainingListAdapter
import com.example.squadme.R
import com.example.squadme.databinding.ItemTrainingDetailBinding
import com.example.squadme.databinding.TrainingListItemBinding

class TrainingExerciseAdapter(
    private val exercises: List<String>,
) : RecyclerView.Adapter<TrainingExerciseAdapter.ExerciseViewHolder>() {

    inner class ExerciseViewHolder(private val binding: ItemTrainingDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(exercise: String) {
            binding.exerciseName.text = exercise;

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingExerciseAdapter.ExerciseViewHolder {
        val binding = ItemTrainingDetailBinding.inflate(
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