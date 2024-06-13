package com.example.squadme.MainActivity.squads.squadCreation

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.squadme.R
import com.example.squadme.data.Models.LineUp
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.FragmentSquadCreationBinding
import com.example.squadme.utils.FirestoreSingleton
import com.example.squadme.utils.NetworkUtils
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SquadCreationFragment : Fragment() {
    private lateinit var binding: FragmentSquadCreationBinding
    private lateinit var auth: FirebaseAuth
    private var firestore = FirestoreSingleton.getInstance()
    private lateinit var currentUserId: String
    private lateinit var playerList: MutableList<Player>
    private lateinit var selectedPlayers: MutableSet<Player>
    private lateinit var playerAdapter: PlayerAdapterDropdown

    /**
     * Initialize the spinner adapter with formations on resume
     */
    override fun onResume() {
        super.onResume()
        val formations = resources.getStringArray(R.array.formation)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, formations)
        binding.formationItem.setAdapter(arrayAdapter)
    }

    /**
     * Inflate the layout for this fragment and initialize view binding
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     * @return Return the View for the fragment's UI
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSquadCreationBinding.inflate(inflater, container, false)
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
                    it.id = document.id
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
            if (NetworkUtils.isNetworkAvailable(requireContext())){
                createSquad()
            }else{
                Toast.makeText(context, getString(R.string.toast_error_no_connection_createSquad), Toast.LENGTH_SHORT).show()
            }
        }

        binding.calcelButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    /**
     * Set up the player adapter for the player selection dialog
     */
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


    /**
     * Update the spinner view with selected player names
     */

    private fun updateSpinnerView() {
        val selectedPlayerNames = selectedPlayers.joinToString(", ") { it.name ?: "" }
        binding.spinnerTextView.text = selectedPlayerNames
    }

    /**
     * Update the selected players count displayed
     */

    private fun updateSelectedPlayersCount() {
        val countText = "${selectedPlayers.size}" + getString(R.string.player_creation_squads)
        binding.selectedPlayersCount.text = countText
    }

    /**
     * Show player selection dialog with a list of players
     */

    private fun showPlayerSelectionDialog() {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_player_selection, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.dialogRecyclerView)

        recyclerView.adapter = playerAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_title_create_squad))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.dialog_squad_positive_btn)) { dialog, _ ->
                dialog.dismiss()
                updateSpinnerView()
            }
            .setNegativeButton(getString(R.string.dialog_squad_negative_btn)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    /**
      * Validate and create the squad in Firestore
      */
    private fun createSquad() {
        val squadName = binding.nameInput.text.toString()
        val formation = binding.formationItem.text.toString()

        if (squadName.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.toast_error_empty_name_squad_value), Toast.LENGTH_SHORT).show()
            return
        }

        if (formation.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.toast_error_empty_formation_squad_value), Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedPlayers.size != 11) {
            Toast.makeText(requireContext(), getString(R.string.toast_error_empty_11players_squad_value), Toast.LENGTH_SHORT).show()
            return
        }


        val squad = LineUp(
            name = squadName,
            lineUp = formation,
            players = selectedPlayers.toList(),
            coachId = currentUserId
        )

        firestore.collection("squads")
            .add(squad)
            .addOnSuccessListener { documentReference ->
                val squadId = documentReference.id
                firestore.collection("squads").document(squadId)
                    .update("id", squadId)
                    .addOnSuccessListener {
                        squad.id = squadId
                        Toast.makeText(requireContext(), getString(R.string.toast_squad_create), Toast.LENGTH_SHORT).show()
                        // Aquí puedes limpiar los campos o hacer cualquier otra acción post creación
                        val action = SquadCreationFragmentDirections.actionSquadCreationFragmentToSquadListFragment()
                        findNavController().navigate(action)
                    }
                    .addOnFailureListener { e ->
                        //Toast.makeText(requireContext(), "Error al crear la plantilla: ${e.message}", Toast.LENGTH_SHORT).show()
                        Toast.makeText(requireContext(), getString(R.string.toast_squad_create_error), Toast.LENGTH_SHORT).show()
                        Log.d("SquadCreationFragment", "Error al crear la plantilla: ${e.message}")
                    }
            }
    }
}









