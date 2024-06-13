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
import com.google.firebase.firestore.Source

import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class PlayerListFragment : Fragment() {
    private lateinit var binding: FragmentPlayerListBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserId: String
    private lateinit var adapter: PlayerListAdapter

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
        binding = FragmentPlayerListBinding.inflate(inflater, container, false)
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
                    Toast.makeText(requireContext(), getString(R.string.toast_error_no_perimissions_createPlayer), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.toast_no_connection_match_detail), Toast.LENGTH_SHORT).show()
            }
        }

        checkUserRoleAndFetchPlayers()
    }

    /**
     * Check user role (admin or coach) and fetch players from Firestore based on the role
     */
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
                            fetchPlayersByAdmin()
                        }
                        playerDocument.exists() -> {
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

    /**
     * Fetch players from Firestore filtered by admin role
     */
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

    /**
     * Fetch players from Firestore cache based on user type
     *
     * @param userType The type of user ('admin', 'coach', or 'unknown')
     */
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












