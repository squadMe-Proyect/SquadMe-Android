package com.example.squadme.MainActivity.players.playerList


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.squadme.MainActivity.matches.matchesList.MatchListFragmentDirections
import com.example.squadme.R
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.FragmentPlayerListBinding
import com.example.squadme.utils.FirestoreSingleton
import com.example.squadme.utils.NetworkUtils
import com.example.squadme.utils.UserManager
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Source

import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class PlayerListFragment : Fragment() {
    private lateinit var binding: FragmentPlayerListBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserId: String
    private lateinit var adapter: PlayerListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirestoreSingleton.getInstance()
        currentUserId = auth.currentUser?.uid ?: ""

        adapter = PlayerListAdapter(requireContext()) { _, player ->
            val actionToDetail = PlayerListFragmentDirections.actionPlayerListFragmentToPlayerDetailFragment(player)
            view.findNavController().navigate(actionToDetail)
        }
        binding.playerList.adapter = adapter

        binding.btnCreationPlayer.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                if (UserManager.isAdmin) {
                    val action = PlayerListFragmentDirections.actionPlayerListFragmentToCameraPreviewFragment()
                    view.findNavController().navigate(action)
                } else {
                    Toast.makeText(requireContext(), "No tienes permiso para crear un jugador.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
            }
        }

        checkUserRoleAndFetchPlayers()
    }

    private fun checkUserRoleAndFetchPlayers() {
        val coachesRef = firestore.collection("coaches").document(currentUserId)
        val playersRef = firestore.collection("players").document(currentUserId)

        try {
            Tasks.whenAllSuccess<DocumentSnapshot>(coachesRef.get(), playersRef.get())
                .addOnSuccessListener { results ->
                    val coachDocument = results[0]
                    val playerDocument = results[1]

                    when {
                        coachDocument.exists() -> {
                            //isAdmin = true
                            fetchPlayersByAdmin()
                        }
                        playerDocument.exists() -> {
                            //isAdmin = false
                            val coachId = playerDocument.getString("coachId")
                            coachId?.let { fetchPlayersFromCache("player") }
                        }
                        else -> {
                            Log.e("PlayerListFragment", "Unknown user type")
                            fetchPlayersFromCache("unknown")
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("PlayerListFragment", "Error checking user role: $exception")
                    fetchPlayersFromCache("unknown")
                }
        } catch (e: Exception) {
            Log.e("PlayerListFragment", "Error: $e")
            fetchPlayersFromCache("unknown")
        }
    }

    private fun fetchPlayersByAdmin() {
        firestore.collection("players")
            .whereEqualTo("coachId", currentUserId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val playerList = mutableListOf<Player>()
                for (document in querySnapshot.documents) {
                    val player = document.toObject(Player::class.java)
                    player?.let {
                        playerList.add(it)
                    }
                }
                adapter.submitList(playerList)
            }
            .addOnFailureListener { exception ->
                Log.e("PlayerListFragment", "Error fetching players by admin: $exception")
                fetchPlayersFromCache("coach")
            }
    }

    private fun fetchPlayersFromCache(userType: String) {
        if (UserManager.isAdmin) {
            firestore.collection("players")
                .get(Source.CACHE)
                .addOnSuccessListener { querySnapshot ->
                    val playerList = mutableListOf<Player>()
                    for (document in querySnapshot.documents) {
                        val player = document.toObject(Player::class.java)
                        player?.let {
                            if (it.coachId == currentUserId) {
                                playerList.add(it)
                            }
                        }
                    }
                    adapter.submitList(playerList)
                }
                .addOnFailureListener { exception ->
                    Log.e("PlayerListFragment", "Error fetching players from cache: $exception")
                }
        } else {
            val prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val coachId = prefs.getString("coachId", "")

            coachId?.let { id ->
                firestore.collection("players")
                    .whereEqualTo("coachId", id)
                    .get(Source.CACHE)
                    .addOnSuccessListener { querySnapshot ->
                        val playerList = mutableListOf<Player>()
                        for (document in querySnapshot.documents) {
                            val player = document.toObject(Player::class.java)
                            player?.let {
                                playerList.add(it)
                            }
                        }
                        adapter.submitList(playerList)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("PlayerListFragment", "Error fetching players from cache: $exception")
                    }
            }
        }
    }
}












