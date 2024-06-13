package com.example.squadme.MainActivity.training.trainingList

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.squadme.MainActivity.squads.squadList.SquadListFragmentDirections
import com.example.squadme.R
import com.example.squadme.data.Models.Match
import com.example.squadme.data.Models.Training
import com.example.squadme.databinding.FragmentTrainingListBinding
import com.example.squadme.utils.FirestoreSingleton
import com.example.squadme.utils.NetworkUtils
import com.example.squadme.utils.UserManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.android.gms.tasks.Tasks
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrainingListFragment : Fragment() {
    private lateinit var binding: FragmentTrainingListBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserId: String
    private lateinit var trainingListAdapter: TrainingListAdapter

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
    ): View {
        binding = FragmentTrainingListBinding.inflate(inflater, container, false)
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

        auth = FirebaseAuth.getInstance()
        firestore = FirestoreSingleton.getInstance()
        currentUserId = auth.currentUser?.uid ?: ""



        trainingListAdapter = TrainingListAdapter { view, training ->
            val action = TrainingListFragmentDirections.actionTrainingListFragmentToTrainingDetailFragment(training)
            findNavController().navigate(action)
        }

        binding.trainingList.adapter = trainingListAdapter

        // Verifica si el usuario está autenticado y tiene un UID válido
        if (currentUserId.isNotEmpty()) {
            checkUserRoleAndFetchTrainings()
        } else {
            Log.e("TrainingListFragment", "User ID is null or empty")
        }

        binding.createTrain.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                if (UserManager.isAdmin) {
                    val action = TrainingListFragmentDirections.actionTrainingListFragmentToTrainingCreationFragment()
                    findNavController().navigate(action)
                } else {
                    Toast.makeText(requireContext(), getString(R.string.toast_error_no_perimissions_createTraining), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.toast_no_connection_match_detail), Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Checks the user's role and fetches trainings accordingly.
     */
    private fun checkUserRoleAndFetchTrainings() {
        val coachesRef = firestore.collection("coaches").document(currentUserId)
        val playersRef = firestore.collection("players").document(currentUserId)

        // Realizar ambas consultas en paralelo
        Tasks.whenAllSuccess<DocumentSnapshot>(coachesRef.get(), playersRef.get())
            .addOnSuccessListener { results ->
                val coachDocument = results[0]
                val playerDocument = results[1]

                when {
                    coachDocument.exists() -> {
                        fetchTrainingsByAdmin()
                    }
                    playerDocument.exists() -> {
                        val coachId = playerDocument.getString("coachId")
                        coachId?.let { fetchTrainingsFromCache() }
                    }
                    else -> {
                        Log.e("TrainingListFragment", "User is neither coach nor player.")
                        fetchTrainingsFromCache()
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("TrainingListFragment", "Error checking user role: $exception")
                fetchTrainingsFromCache()
            }
    }

    /**
     * Fetches trainings filtered by admin role.
     */
    private fun fetchTrainingsByAdmin() {
        firestore.collection("trainings")
            .whereEqualTo("coachId", currentUserId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("TrainingListFragment", "Error fetching trainings by admin: $exception")
                    fetchTrainingsFromCache()
                    return@addSnapshotListener
                }

                val trainingList = mutableListOf<Training>()
                snapshot?.documents?.forEach { document ->
                    val training = document.toObject(Training::class.java)
                    training?.let {
                        trainingList.add(it)
                    }
                }
                trainingListAdapter.submitList(trainingList)
            }
    }

    /**
     * Fetches trainings from cache .
     *
     */
    private fun fetchTrainingsFromCache() {
        if (UserManager.isAdmin) {
            firestore.collection("trainings")
                .get(Source.CACHE)
                .addOnSuccessListener { querySnapshot ->
                    val trainingList = mutableListOf<Training>()
                    for (document in querySnapshot.documents) {
                        val training = document.toObject(Training::class.java)
                        training?.let {
                            if (it.coachId == currentUserId) {
                                trainingList.add(it)
                            }
                        }
                    }
                    trainingListAdapter.submitList(trainingList)
                }
                .addOnFailureListener { exception ->
                    Log.e("TrainingListFragment", "Error fetching trainings from cache: $exception")
                }
            return
        } else {
            val prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val coachId = prefs.getString("coachId", "")

            coachId?.let { id ->
                firestore.collection("trainings")
                    .whereEqualTo("coachId", id)
                    .get(Source.CACHE)
                    .addOnSuccessListener { querySnapshot ->
                        val trainingList = mutableListOf<Training>()
                        for (document in querySnapshot.documents) {
                            val training = document.toObject(Training::class.java)
                            training?.let {
                                trainingList.add(it)
                            }
                        }
                        trainingListAdapter.submitList(trainingList)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("TrainingListFragment", "Error fetching trainings from cache: $exception")
                    }
            }
        }
    }
}



