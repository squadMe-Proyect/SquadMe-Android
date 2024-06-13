package com.example.squadme.MainActivity.training.trainingUpdate

import androidx.recyclerview.widget.RecyclerView
import com.example.squadme.databinding.ItemExerciseUpdateBinding
import android.view.ViewGroup
import android.view.LayoutInflater

/**
 * Adapter for updating exercises in a RecyclerView within the TrainingExerciseUpdateFragment.
 *
 * @param exercises List of exercises to be displayed and updated.
 * @param onDeleteClick Callback function to handle exercise deletion.
 */
class TrainingExerciseUpdateAdapter(
    private val exercises: MutableList<String>,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<TrainingExerciseUpdateAdapter.ExerciseViewHolder>() {

    /**
     * ViewHolder for displaying and updating individual exercises.
     *
     * @param binding View binding object for the exercise update item layout.
     */
    inner class ExerciseViewHolder(private val binding: ItemExerciseUpdateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        /**
         * Binds exercise data to the ExerciseViewHolder at the specified position.
         *
         * @param exercise The exercise string to bind.
         */
        fun bind(exercise: String) {
            binding.exerciseTextView.text = exercise
            binding.deleteButton.setOnClickListener {
                onDeleteClick(exercise)
            }
        }
    }

    /**
     * Creates a new ExerciseViewHolder instance.
     *
     * @param parent The parent view group into which the new view will be added.
     * @param viewType The type of the new view.
     * @return A new ExerciseViewHolder instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemExerciseUpdateBinding.inflate(
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
     * Returns the total number of exercises in the list.
     *
     * @return The total number of exercises in the list.
     */
    override fun getItemCount(): Int = exercises.size
}