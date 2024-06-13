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

/**
 * Adapter for displaying players in a RecyclerView within the SquadDetailFragment.
 *
 * @param exercises List of players to be displayed.
 */
class TrainingExerciseAdapter(
    private val exercises: List<String>,
) : RecyclerView.Adapter<TrainingExerciseAdapter.ExerciseViewHolder>() {

    /**
     * ViewHolder for displaying individual exerices name.
     *
     * @param binding View binding object for the execise item layout.
     */
    inner class ExerciseViewHolder(private val binding: ItemTrainingDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(exercise: String) {
            binding.exerciseName.text = exercise;
        }
    }

    /**
     * Creates a new ExerciseViewHolder instance.
     *
     * @param parent The parent view group into which the new view will be added.
     * @param viewType The type of the new view.
     * @return A new ExerciseViewHolder instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingExerciseAdapter.ExerciseViewHolder {
        val binding = ItemTrainingDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExerciseViewHolder(binding)
    }

    /**
     * Binds exercise data to the ExerciseViewHolder at the specified position.
     *
     * @param holder The ExerciseViewHolder to bind data to.
     * @param position The position of the exercise item in the list.
     */
    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(exercises[position])
    }

    /**
     * Returns the total number of exercise in the list.
     *
     * @return The total number of exercises in the list.
     */
    override fun getItemCount(): Int = exercises.size

}