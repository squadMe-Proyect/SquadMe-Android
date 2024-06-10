package com.example.squadme.MainActivity.squads.squadList

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.squadme.R
import com.example.squadme.data.Models.LineUp
import com.example.squadme.databinding.FragmentSquadListBinding
import com.example.squadme.utils.FirestoreSingleton
import com.example.squadme.utils.NetworkUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import dagger.hilt.android.AndroidEntryPoint
    @AndroidEntryPoint
    class SquadListFragment : Fragment() {
        private lateinit var binding: FragmentSquadListBinding
        private lateinit var auth: FirebaseAuth
        private lateinit var firestore: FirebaseFirestore
        private lateinit var currentUserId: String
        private lateinit var adapter: SquadListAdapter
        private var isAdmin: Boolean = false

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            binding = FragmentSquadListBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            auth = FirebaseAuth.getInstance()
            firestore = FirestoreSingleton.getInstance()
            currentUserId = auth.currentUser?.uid ?: ""

            adapter = SquadListAdapter { _, squad ->
                val actionToDetail =
                    SquadListFragmentDirections.actionSquadListFragmentToSquadDetailFragment(squad)
                view.findNavController().navigate(actionToDetail)
            }

            binding.squadList.adapter = adapter

            // Verifica si el usuario está autenticado y tiene un UID válido
            if (currentUserId.isNotEmpty()) {
                checkUserRoleAndFetchSquads()
            } else {
                Log.e("SquadListFragment", "User ID is null or empty")
            }

            binding.createBtn.setOnClickListener {
                if (NetworkUtils.isNetworkAvailable(requireContext())) {
                    if (isAdmin) {
                        val action =
                            SquadListFragmentDirections.actionSquadListFragmentToSquadCreationFragment()
                        findNavController().navigate(action)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "No tienes permiso para crear un jugador.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "No hay conexión a Internet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        private fun checkUserRoleAndFetchSquads() {
            val coachesRef = firestore.collection("coaches").document(currentUserId)
            val playersRef = firestore.collection("players").document(currentUserId)

            // Realizar ambas consultas en paralelo
            Tasks.whenAllSuccess<DocumentSnapshot>(coachesRef.get(), playersRef.get())
                .addOnSuccessListener { results ->
                    val coachDocument = results[0]
                    val playerDocument = results[1]

                    when {
                        coachDocument.exists() -> {
                            isAdmin = true
                            fetchSquadsByAdmin()
                        }

                        playerDocument.exists() -> {
                            isAdmin = false
                            val coachId = playerDocument.getString("coachId")
                            coachId?.let { fetchSquadsFromCache("player") }
                        }

                        else -> {
                            Log.e("SquadListFragment", "User is neither coach nor player.")
                            fetchSquadsFromCache("unknown")
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("SquadListFragment", "Error checking user role: $exception")
                    fetchSquadsFromCache("unknown")
                }
        }

        private fun fetchSquadsByAdmin() {
            firestore.collection("squads")
                .whereEqualTo("coachId", currentUserId)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        Log.e("SquadListFragment", "Error fetching squads by admin: $exception")
                        fetchSquadsFromCache("coach")
                        return@addSnapshotListener
                    }

                    val squadList = mutableListOf<LineUp>()
                    snapshot?.documents?.forEach { document ->
                        val squad = document.toObject(LineUp::class.java)
                        squad?.let {
                            squadList.add(it)
                        }
                    }
                    adapter.submitList(squadList)
                }
        }


        private fun fetchSquadsFromCache(userType: String) {
            if (isAdmin) {
                firestore.collection("squads")
                    .get(Source.CACHE)
                    .addOnSuccessListener { querySnapshot ->
                        val squadList = mutableListOf<LineUp>()
                        for (document in querySnapshot.documents) {
                            val squad = document.toObject(LineUp::class.java)
                            squad?.let {
                                if (it.coachId == currentUserId) {
                                    squadList.add(it)
                                }
                            }
                        }
                        adapter.submitList(squadList)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("SquadListFragment", "Error fetching squads from cache: $exception")
                    }
            } else {
                val prefs =
                    //requireActivity()
                    requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val coachId = prefs.getString("coachId", "")

                coachId?.let { id ->
                    firestore.collection("squads")
                        .whereEqualTo("coachId", id)
                        .get(Source.CACHE)
                        .addOnSuccessListener { querySnapshot ->
                            val squadList = mutableListOf<LineUp>()
                            for (document in querySnapshot.documents) {
                                val squad = document.toObject(LineUp::class.java)
                                squad?.let {
                                    squadList.add(it)
                                }
                            }
                            adapter.submitList(squadList)
                        }
                        .addOnFailureListener { exception ->
                            Log.e(
                                "SquadListFragment",
                                "Error fetching squads from cache: $exception"
                            )
                        }
                }
            }
        }
    }







