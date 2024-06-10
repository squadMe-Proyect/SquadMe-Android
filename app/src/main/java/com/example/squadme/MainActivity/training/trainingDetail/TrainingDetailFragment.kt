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
import com.example.squadme.R
import com.example.squadme.data.Models.Training
import com.example.squadme.databinding.FragmentTrainingDetailBinding
import com.example.squadme.utils.FirestoreSingleton
import com.example.squadme.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class TrainingDetailFragment : Fragment() {
    private lateinit var binding: FragmentTrainingDetailBinding
    private lateinit var exerciseAdapter: TrainingExerciseAdapter
    private val db = FirestoreSingleton.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrainingDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: TrainingDetailFragmentArgs by navArgs()
        val training:Training = args.training


        val timestamp = training.date
        if (timestamp != null) {
            val date = timestamp.toDate()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            val formattedDate = dateFormat.format(date)
            val formattedTime = timeFormat.format(date)

            binding.trainDate.text = getString(R.string.training_detail_date) + " $formattedDate"
            binding.trainHour.text = getString(R.string.training_detail_hour) +  " $formattedTime"
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
            val action = TrainingDetailFragmentDirections.actionTrainingDetailFragmentToTrainingUpdateFragment(training)
            findNavController().navigate(action)
        }

        binding.deleteBtn.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                if (training.completed) {
                    eliminarTraining(training.id)
                } else {
                    Toast.makeText(context, getString(R.string.toast_error_no_completed_training_delete), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, getString(R.string.toast_error_no_connection_deleteTraining), Toast.LENGTH_SHORT).show()
            }
        }
    }

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