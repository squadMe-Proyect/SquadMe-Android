package com.example.squadme.MainActivity.matches.matchesList

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.squadme.data.Models.Match
import com.example.squadme.databinding.FragmentMatchListBinding
import com.example.squadme.utils.FirestoreSingleton
import com.example.squadme.utils.NetworkUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.android.gms.tasks.Tasks
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchListFragment : Fragment() {
    private lateinit var binding: FragmentMatchListBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserId: String
    private lateinit var adapter: MatchListAdapter
    private var isAdmin: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMatchListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirestoreSingleton.getInstance()
        currentUserId = auth.currentUser?.uid ?: ""


        adapter = MatchListAdapter { _, match ->
            val action = MatchListFragmentDirections
                .actionMatchListFragmentToMatchDetailFragment(match, isAdmin)
            view.findNavController().navigate(action)
        }


        binding.matchList.adapter = adapter

        binding.btnCreationMatch.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                if (isAdmin) {
                    val action = MatchListFragmentDirections.actionMatchListFragmentToMatchCreationFragment()
                    view.findNavController().navigate(action)
                } else {
                    Toast.makeText(requireContext(), "No tienes permiso para crear un jugador.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
            }
        }

        checkUserRoleAndFetchMatches()
    }

    private fun checkUserRoleAndFetchMatches() {
        val coachesRef = firestore.collection("coaches").document(currentUserId)
        val playersRef = firestore.collection("players").document(currentUserId)

        try {
            Tasks.whenAllSuccess<DocumentSnapshot>(coachesRef.get(), playersRef.get())
                .addOnSuccessListener { results ->
                    val coachDocument = results[0]
                    val playerDocument = results[1]

                    when {
                        coachDocument.exists() -> {
                            isAdmin = true
                            fetchMatchesByAdmin()
                        }
                        playerDocument.exists() -> {
                            isAdmin = false
                            val coachId = playerDocument.getString("coachId")
                            coachId?.let { fetchMatchesFromCache("player") }
                        }
                        else -> {
                            Log.e("MatchListFragment", "User is neither coach nor player.")
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("MatchListFragment", "Error checking user role: $exception")
                    fetchMatchesFromCache("unknown")
                }
        } catch (e: Exception) {
            Log.e("MatchListFragment", "Error: $e")
            fetchMatchesFromCache("unknown")
        }
    }

    private fun fetchMatchesByAdmin() {
        firestore.collection("matches")
            .whereEqualTo("coachId", currentUserId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val matchList = mutableListOf<Match>()
                for (document in querySnapshot.documents) {
                    val match = document.toObject(Match::class.java)
                    match?.let {
                        matchList.add(it)
                    }
                }
                adapter.submitList(matchList)
            }
            .addOnFailureListener { exception ->
                Log.e("MatchListFragment", "Error fetching matches by admin: $exception")
                fetchMatchesFromCache("coach")
            }
    }

    private fun fetchMatchesFromCache(userType: String) {
        if (isAdmin) {
            firestore.collection("matches")
                .get(Source.CACHE)
                .addOnSuccessListener { querySnapshot ->
                    val matchList = mutableListOf<Match>()
                    for (document in querySnapshot.documents) {
                        val match = document.toObject(Match::class.java)
                        match?.let {
                            if (it.coachId == currentUserId) {
                                matchList.add(it)
                            }
                        }
                    }
                    adapter.submitList(matchList)
                }
                .addOnFailureListener { exception ->
                    Log.e("MatchListFragment", "Error fetching matches from cache: $exception")
                    //Toast.makeText(requireContext(), "No se pudieron cargar los datos desde la caché", Toast.LENGTH_SHORT).show()
                }
        } else {
            val prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val coachId = prefs.getString("coachId", "")

            coachId?.let { id ->
                firestore.collection("matches")
                    .whereEqualTo("coachId", id)
                    .get(Source.CACHE)
                    .addOnSuccessListener { querySnapshot ->
                        val matchList = mutableListOf<Match>()
                        for (document in querySnapshot.documents) {
                            val match = document.toObject(Match::class.java)
                            match?.let {
                                matchList.add(it)
                            }
                        }
                        adapter.submitList(matchList)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("MatchListFragment", "Error fetching matches from cache: $exception")
                        //Toast.makeText(requireContext(), "No se pudieron cargar los datos desde la caché", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}



