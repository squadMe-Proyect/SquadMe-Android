package com.example.squadme.MainActivity.training.trainingDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import com.example.squadme.MainActivity.matches.matchesDetail.MatchDetailFragmentDirections
import com.example.squadme.R
import com.example.squadme.data.Models.Training
import com.example.squadme.databinding.FragmentTrainingDetailBinding
import com.example.squadme.utils.FirestoreSingleton
import com.example.squadme.utils.NetworkUtils
import com.example.squadme.utils.UserManager
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class TrainingDetailFragment : Fragment() {
    private lateinit var binding: FragmentTrainingDetailBinding
    private lateinit var exerciseAdapter: TrainingExerciseAdapter
    private val db = FirestoreSingleton.getInstance()

    /**
     * Inflate the layout for this fragment and initialize view binding
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     * @return Return the View for the fragment's UI
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrainingDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Set up the view once it has been created
     *
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle)
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: TrainingDetailFragmentArgs by navArgs()
        val training: Training = args.training


        val timestamp = training.date
        if (timestamp != null) {
            val date = timestamp.toDate()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            val formattedDate = dateFormat.format(date)
            val formattedTime = timeFormat.format(date)

            binding.trainDate.text = getString(R.string.training_detail_date) + " $formattedDate"
            binding.trainHour.text = getString(R.string.training_detail_hour) + " $formattedTime"
        } else {
            binding.trainDate.text = getString(R.string.training_detail_date_non)
            binding.trainHour.text = getString(R.string.training_detail_hour_non)
        }

        binding.trainStatus.text = if (training.completed) {
            //"Estado: Completado"
            getString(R.string.training_detail_state_completed)
        } else {
            //"Estado: No Completado"
            getString(R.string.training_detail_state_non_completed)
        }

        exerciseAdapter = TrainingExerciseAdapter(training.exercises)
        binding.exercisesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.exercisesRecyclerView.adapter = exerciseAdapter


        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }


        binding.editBtn.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                if (UserManager.isAdmin) {
                    val action = TrainingDetailFragmentDirections.actionTrainingDetailFragmentToTrainingUpdateFragment(training)
                    findNavController().navigate(action)
                } else {
                    Toast.makeText(requireContext(), getString(R.string.toast_training_edit_perimissions_error), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.toast_no_connection_match_detail), Toast.LENGTH_SHORT)
                    .show()
            }
        }



        binding.deleteBtn.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                if (UserManager.isAdmin) {
                    if (training.completed) {
                        eliminarTraining(training.id)
                    } else {
                        Toast.makeText(
                            context,
                            getString(R.string.toast_error_no_completed_training_delete),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.toast_no_permissions_delete_training),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.toast_error_no_connection_deleteTraining),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    /**
     * Method to delete a training session from Firestore.
     *
     * @param trainingId The ID of the training session to delete.
     */
    private fun eliminarTraining(trainingId: String?) {
        if (trainingId != null) {
            db.collection("trainings").document(trainingId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context, getString(R.string.toast_training_delete), Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                .addOnFailureListener { e ->
                    //Toast.makeText(context, "Error al eliminar el entrenamiento: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.d("TrainingDetailFragment", "Error al eliminar el entrenamiento: ${e.message}")
                    Toast.makeText(context, getString(R.string.toast_training_delete_error), Toast.LENGTH_SHORT).show()
                }
        } else {
            //Toast.makeText(context, "ID de entrenamiento no válido", Toast.LENGTH_SHORT).show()
            Log.d("TrainingDetailFragment", "ID de entrenamiento no válido")
            Toast.makeText(context, getString(R.string.toast_training_delete_error), Toast.LENGTH_SHORT).show()
        }
    }
}