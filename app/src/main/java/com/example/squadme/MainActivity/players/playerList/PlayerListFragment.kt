package com.example.squadme.MainActivity.players.playerList

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController

import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.FragmentPlayerListBinding
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

import dagger.hilt.android.AndroidEntryPoint


/*
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
    ): View {
        binding = FragmentPlayerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUserId = auth.currentUser?.uid ?: ""

        adapter = PlayerListAdapter(requireContext()) { _, player ->
            val actionToDetail = PlayerListFragmentDirections.actionPlayerListFragmentToPlayerDetailFragment(player)
            view.findNavController().navigate(actionToDetail)
        }
        binding.playerList.adapter = adapter

        val playersRef = firestore.collection("players")

        playersRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e(TAG, "Error fetching players: $exception")
                return@addSnapshotListener
            }

            val playerList = mutableListOf<Player>()
            snapshot?.documents?.forEach { document ->
                val player = document.toObject(Player::class.java)
                player?.let {
                    // Verificar que coachId no sea nulo y coincida con el ID actual
                    if (it.coachId == currentUserId) {
                        playerList.add(it)
                    }
                }
            }
            adapter.submitList(playerList)
        }

        // Configurar el clic en el botón de creación de jugador
        binding.btnCreationPlayer.setOnClickListener {
            val action = PlayerListFragmentDirections.actionPlayerListFragmentToCameraPreviewFragment()
            view.findNavController().navigate(action)
        }
    }
}

 */
/*
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
    ): View {
        binding = FragmentPlayerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUserId = auth.currentUser?.uid ?: ""

        adapter = PlayerListAdapter(requireContext()) { _, player ->
            val actionToDetail = PlayerListFragmentDirections.actionPlayerListFragmentToPlayerDetailFragment(player)
            view.findNavController().navigate(actionToDetail)
        }
        binding.playerList.adapter = adapter

        checkUserRoleAndFetchPlayers()
    }

    private fun checkUserRoleAndFetchPlayers() {
        val userRef = firestore.collection("coaches").document(currentUserId)
        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    fetchPlayersByAdmin()
                } else {
                    checkIfUserIsPlayer()
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error checking user role: $exception")
            }
    }

    private fun checkIfUserIsPlayer() {
        val userRef = firestore.collection("players").document(currentUserId)
        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val coachId = documentSnapshot.getString("coachId")
                    coachId?.let {
                        fetchPlayersByCoachId(it)
                    }
                } else {
                    Log.e(TAG, "User is neither coach nor player.")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error checking if user is player: $exception")
            }
    }

    private fun fetchPlayersByAdmin() {
        val playersRef = firestore.collection("players")
        playersRef.whereEqualTo("coachId", currentUserId)
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
                Log.e(TAG, "Error fetching players by admin: $exception")
            }
    }

    private fun fetchPlayersByCoachId(coachId: String) {
        val playersRef = firestore.collection("players")
        playersRef.whereEqualTo("coachId", coachId)
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
                Log.e(TAG, "Error fetching players by coachId: $exception")
            }
    }
}

 */

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
    ): View {
        binding = FragmentPlayerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUserId = auth.currentUser?.uid ?: ""


        adapter = PlayerListAdapter(requireContext()) { _, player ->
            val actionToDetail = PlayerListFragmentDirections.actionPlayerListFragmentToPlayerDetailFragment(player)
            view.findNavController().navigate(actionToDetail)
        }
        binding.playerList.adapter = adapter

        binding.btnCreationPlayer.setOnClickListener {
            val action = PlayerListFragmentDirections.actionPlayerListFragmentToCameraPreviewFragment()
            view.findNavController().navigate(action)
        }

        // Mostrar un indicador de carga mientras se obtienen los datos
        checkUserRoleAndFetchPlayers()
    }

    private fun checkUserRoleAndFetchPlayers() {
        val coachesRef = firestore.collection("coaches").document(currentUserId)
        val playersRef = firestore.collection("players").document(currentUserId)

        // Realizar ambas consultas en paralelo
        Tasks.whenAllSuccess<DocumentSnapshot>(coachesRef.get(), playersRef.get())
            .addOnSuccessListener { results ->
                val coachDocument = results[0]
                val playerDocument = results[1]

                when {
                    coachDocument.exists() -> fetchPlayersByAdmin()
                    playerDocument.exists() -> {
                        val coachId = playerDocument.getString("coachId")
                        coachId?.let { fetchPlayersByCoachId(it) }
                    }
                    else -> {
                        Log.e(TAG, "User is neither coach nor player.")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error checking user role: $exception")
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
                Log.e(TAG, "Error fetching players by admin: $exception")
            }
    }

    private fun fetchPlayersByCoachId(coachId: String) {
        firestore.collection("players")
            .whereEqualTo("coachId", coachId)
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
                Log.e(TAG, "Error fetching players by coachId: $exception")
            }
    }
}







