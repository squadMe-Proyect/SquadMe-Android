package com.example.squadme.MainActivity.squads.squadCreation

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.squadme.R
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.FragmentSquadCreationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SquadCreationFragment : Fragment() {
    private lateinit var binding: FragmentSquadCreationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserId: String
    private lateinit var playerList: MutableList<Player>
    private lateinit var selectedPlayers: MutableList<Player>
    override fun onResume() {
        super.onResume()
        val formations = resources.getStringArray(R.array.formation)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, formations)
        binding.formationItem.setAdapter(arrayAdapter)

        /*
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        val uid = currentUser?.uid

        val playersCollection = FirebaseFirestore.getInstance().collection("players")

        playersCollection.whereEqualTo("coachId", uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val playerName = document.getString("name")
                    playerName?.let {
                        Log.d("Firestore", "Player name: $playerName")
                        playerList?.add(Player(playerName))
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error al leer los datos de Firestore: ", exception)
            }

         */
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSquadCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUserId = auth.currentUser?.uid ?: ""

        playerList = mutableListOf()
        selectedPlayers = mutableListOf()

        val playersRef = firestore.collection("players")

        playersRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e(TAG, "Error fetching players: $exception")
                return@addSnapshotListener
            }

            playerList.clear()
            snapshot?.documents?.forEach { document ->
                val player = document.toObject(Player::class.java)
                player?.let {
                    // Verificar que coachId no sea nulo y coincida con el ID actual
                    // Verificar también que el nombre no sea nulo o vacío
                    if (it.coachId == currentUserId && !it.name.isNullOrEmpty()) {
                        Log.e(TAG, "Playerssszz: $it")
                        playerList.add(it)
                    }
                }
            }

            setupSpinner()
        }
    }

    private fun setupSpinner() {
        val spinnerAdapter = PlayerAdapterDropdown(requireContext(), playerList)
        binding.spinner.adapter = spinnerAdapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val player = playerList[position]

                // Si el jugador no está en la lista de jugadores seleccionados, agrégalo
                if (!selectedPlayers.contains(player)) {
                    selectedPlayers.add(player)
                } else {
                    // Si el jugador ya está en la lista de jugadores seleccionados, quítalo
                    selectedPlayers.remove(player)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No se necesita implementación en este caso
            }
        }
    }
}