package com.example.squadme.MainActivity.squads.squadCreation

import android.app.AlertDialog
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.squadme.R
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.FragmentSquadCreationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
/*
@AndroidEntryPoint
class SquadCreationFragment : Fragment() {
    private lateinit var binding: FragmentSquadCreationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserId: String
    private lateinit var playerList: MutableList<Player>
    private lateinit var selectedPlayers: MutableSet<Player>
    private lateinit var playerAdapter: PlayerAdapterDropdown

    override fun onResume() {
        super.onResume()
        val formations = resources.getStringArray(R.array.formation)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, formations)
        binding.formationItem.setAdapter(arrayAdapter)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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
        selectedPlayers = mutableSetOf()

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
                    if (it.coachId == currentUserId && !it.name.isNullOrEmpty()) {
                        playerList.add(it)
                    }
                }
            }

            setupPlayerAdapter()
        }

        binding.spinnerTextView.setOnClickListener {
            showPlayerSelectionDialog()
        }

        binding.calcelButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupPlayerAdapter() {
        playerAdapter = PlayerAdapterDropdown(playerList) { player, isSelected ->
            if (isSelected) {
                selectedPlayers.add(player)
            } else {
                selectedPlayers.remove(player)
            }
            updateSpinnerView()
            updateSelectedPlayersCount()
        }
    }

    private fun updateSpinnerView() {
        val selectedPlayerNames = selectedPlayers.joinToString(", ") { it.name ?: "" }
        binding.spinnerTextView.text = selectedPlayerNames
    }

    private fun updateSelectedPlayersCount() {
        val countText = "${selectedPlayers.size}/11 jugadores seleccionados"
        binding.selectedPlayersCount.text = countText
    }

    private fun showPlayerSelectionDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_player_selection, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.dialogRecyclerView)

        recyclerView.adapter = playerAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Select Players")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                updateSpinnerView()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun createSquad() {
        val squadName = binding.nameInput.text.toString().trim()
        val formation = binding.formationItem.text.toString().trim()

        if (squadName.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, ingrese un nombre para la plantilla.", Toast.LENGTH_SHORT).show()
            return
        }

        if (formation.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, seleccione una formación.", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedPlayers.size != 11) {
            Toast.makeText(requireContext(), "Debe seleccionar exactamente 11 jugadores.", Toast.LENGTH_SHORT).show()
            return
        }

        val squad = hashMapOf(
            "name" to squadName,
            "formation" to formation,
            "players" to selectedPlayers.map { it.id },
            "coachId" to currentUserId
        )

        firestore.collection("squads")
            .add(squad)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Plantilla creada exitosamente.", Toast.LENGTH_SHORT).show()
                // Aquí puedes limpiar los campos o hacer cualquier otra acción post creación
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al crear la plantilla: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

 */

@AndroidEntryPoint
class SquadCreationFragment : Fragment() {
    private lateinit var binding: FragmentSquadCreationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserId: String
    private lateinit var playerList: MutableList<Player>
    private lateinit var selectedPlayers: MutableSet<Player>
    private lateinit var playerAdapter: PlayerAdapterDropdown

    override fun onResume() {
        super.onResume()
        val formations = resources.getStringArray(R.array.formation)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, formations)
        binding.formationItem.setAdapter(arrayAdapter)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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
        selectedPlayers = mutableSetOf()

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
                    if (it.coachId == currentUserId && !it.name.isNullOrEmpty()) {
                        playerList.add(it)
                    }
                }
            }

            setupPlayerAdapter()
        }

        binding.spinnerTextView.setOnClickListener {
            showPlayerSelectionDialog()
        }

        binding.createButton.setOnClickListener {
            createSquad()
        }

        binding.calcelButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupPlayerAdapter() {
        playerAdapter = PlayerAdapterDropdown(playerList) { player, isSelected ->
            if (isSelected) {
                selectedPlayers.add(player)
            } else {
                selectedPlayers.remove(player)
            }
            updateSpinnerView()
            updateSelectedPlayersCount()
        }
    }

    private fun updateSpinnerView() {
        val selectedPlayerNames = selectedPlayers.joinToString(", ") { it.name ?: "" }
        binding.spinnerTextView.text = selectedPlayerNames
    }

    private fun updateSelectedPlayersCount() {
        val countText = "${selectedPlayers.size}/11 jugadores seleccionados"
        binding.selectedPlayersCount.text = countText
    }

    private fun showPlayerSelectionDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_player_selection, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.dialogRecyclerView)

        recyclerView.adapter = playerAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Select Players")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                updateSpinnerView()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun createSquad() {
        val squadName = binding.nameInput.text.toString()
        val formation = binding.formationItem.text.toString()

        if (squadName.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, ingrese un nombre para la plantilla.", Toast.LENGTH_SHORT).show()
            return
        }

        if (formation.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, seleccione una formación.", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedPlayers.size != 2) {
            Toast.makeText(requireContext(), "Debe seleccionar exactamente 11 jugadores.", Toast.LENGTH_SHORT).show()
            return
        }

        val playersArray = selectedPlayers.map { player ->
            mapOf(
                "id" to player.id,
                "name" to player.name,
                "picture" to player.picture,
                "position" to player.position,
                "surname" to player.surname
            )
        }

        val squad = hashMapOf(
            "name" to squadName,
            "lineUp" to formation,
            "players" to playersArray,
            "coachId" to currentUserId
        )

        firestore.collection("squads")
            .add(squad)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Plantilla creada exitosamente.", Toast.LENGTH_SHORT).show()
                // Aquí puedes limpiar los campos o hacer cualquier otra acción post creación
                val action = SquadCreationFragmentDirections.actionSquadCreationFragmentToSquadListFragment()
                findNavController().navigate(action)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al crear la plantilla: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}








