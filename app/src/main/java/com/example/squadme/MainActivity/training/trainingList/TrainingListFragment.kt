package com.example.squadme.MainActivity.training.trainingList

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.squadme.R
import com.example.squadme.data.Models.Training
import com.example.squadme.databinding.FragmentTrainingListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrainingListFragment : Fragment() {
    private lateinit var binding: FragmentTrainingListBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserId: String
    private lateinit var trainingListAdapter: TrainingListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrainingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUserId = auth.currentUser?.uid ?: ""

        val resourceIds = listOf(
            R.mipmap.entreno1,
            R.mipmap.entreno2,
            R.mipmap.entreno3
        )

        trainingListAdapter = TrainingListAdapter(requireContext(), resourceIds) { view, training ->
            val action = TrainingListFragmentDirections.actionTrainingListFragmentToTrainingDetailFragment(training)
            findNavController().navigate(action)
        }

        binding.trainingList.adapter = trainingListAdapter

        // Verifica si el usuario está autenticado y tiene un UID válido
        if (currentUserId.isNotEmpty()) {
            val trainingsRef = firestore.collection("trainings")

            trainingsRef.whereEqualTo("coachId", currentUserId).addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e(TAG, "Error fetching trainings: $exception")
                    return@addSnapshotListener
                }

                val trainingList = mutableListOf<Training>()
                snapshot?.documents?.forEach { document ->
                    val training = document.toObject(Training::class.java)
                    Log.d(TAG, "Training fetched: $training") // Añade esta línea para ver los datos obtenidos
                    training?.let {
                        trainingList.add(it)
                    }
                }
                trainingListAdapter.submitList(trainingList)
            }
        } else {
            Log.e(TAG, "User ID is null or empty")
        }

        binding.createTrain.setOnClickListener {
            val action = TrainingListFragmentDirections.actionTrainingListFragmentToTrainingCreationFragment()
            findNavController().navigate(action)
        }
    }

    companion object {
        private const val TAG = "TrainingListFragment"
    }
}

