package com.example.squadme.MainActivity.playerList

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
    ): View {
        binding = FragmentPlayerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUserId = auth.currentUser?.uid ?: ""

        adapter = PlayerListAdapter(requireContext())
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



