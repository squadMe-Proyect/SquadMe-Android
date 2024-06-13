package com.example.squadme.MainActivity.squads.squadUpdate

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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.squadme.R
import com.example.squadme.data.Models.LineUp
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.FragmentSquadUpdateBinding
import com.example.squadme.utils.FirestoreSingleton
import com.example.squadme.utils.NetworkUtils
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SquadUpdateFragment : Fragment() {
    private lateinit var binding: FragmentSquadUpdateBinding
    private lateinit var auth: FirebaseAuth
    private var firestore = FirestoreSingleton.getInstance()
    private lateinit var currentUserId: String
    private lateinit var playerList: MutableList<Player>
    private lateinit var selectedPlayers: MutableSet<Player>
    private lateinit var playerAdapter: PlayerAdapterDropdownUpdate
    private lateinit var selectedPlayerAdapter: SquadPlayerUpdateAdapter

    /**
     * Initialize the spinner adapter with formations on resume
     */
    override fun onResume() {
        super.onResume()
        val formations = resources.getStringArray(R.array.formation)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, formations)
        binding.formationDropdown.setAdapter(arrayAdapter)
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
        binding = FragmentSquadUpdateBinding.inflate(inflater, container, false)
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

        val args: SquadUpdateFragmentArgs by navArgs()
        val squad: LineUp = args.squad

        // Set the initial values
        binding.nameInput.setText(squad.name)
        binding.formationDropdown.setText(squad.lineUp)


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
                    it.id = document.id  // Set the document ID as the player's ID
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

        binding.updateButton.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())){
                updateSquad(squad.id!!)
            }else{
                Toast.makeText(context, getString(R.string.toast_error_no_connection_editSquad), Toast.LENGTH_SHORT).show()
            }
        }

        binding.cancelButton.setOnClickListener {
            findNavController().popBackStack()
        }

        setupRecyclerView()
        updateSelectedPlayersCount()
    }

    /**
     * Sets up the adapter for the dropdown list of players.
     */
    private fun setupPlayerAdapter() {
        playerAdapter = PlayerAdapterDropdownUpdate(playerList, selectedPlayers) { player, isSelected ->
            if (isSelected) {
                selectedPlayers.add(player)
            } else {
                selectedPlayers.remove(player)
            }
            updateSelectedPlayersCount()
            updateSelectedPlayerRecyclerView()
        }
    }

    /**
     * Sets up the RecyclerView for displaying selected players.
     */
    private fun setupRecyclerView() {
        selectedPlayerAdapter = SquadPlayerUpdateAdapter(selectedPlayers.toMutableList()) { player ->
            selectedPlayers.remove(player)
            updateSelectedPlayersCount()
            updateSelectedPlayerRecyclerView()
        }
        binding.selectedPlayersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = selectedPlayerAdapter
        }
    }

    /**
     * Updates the count of selected players displayed in the UI.
     */
    private fun updateSelectedPlayersCount() {
        val countText = "${selectedPlayers.size}" + getString(R.string.player_creation_squads)
        binding.selectedPlayersCount.text = countText
    }

    /**
     * Updates the RecyclerView displaying selected players with new data.
     */
    private fun updateSelectedPlayerRecyclerView() {
        selectedPlayerAdapter.updateData(selectedPlayers.toList())
    }

    /**
     * Shows a dialog for selecting players to add to the squad.
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
                updateSelectedPlayerRecyclerView()
            }
            .setNegativeButton(getString(R.string.dialog_squad_negative_btn)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    /**
     * Updates the squad details in Firestore.
     *
     * @param squadId The ID of the squad to update.
     */
    private fun updateSquad(squadId: String) {
        val squadName = binding.nameInput.text.toString()
        val formation = binding.formationDropdown.text.toString()

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
            id = squadId,
            name = squadName,
            lineUp = formation,
            players = selectedPlayers.toList(),
            coachId = currentUserId
        )

        firestore.collection("squads").document(squadId)
            .set(squad)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), getString(R.string.toast_squad_update), Toast.LENGTH_SHORT).show()
                val action =
                    SquadUpdateFragmentDirections.actionSquadUpdateFragmentToSquadListFragment()
                findNavController().navigate(action)
            }
            .addOnFailureListener { e ->
                //Toast.makeText(requireContext(), "Error al actualizar la plantilla: ${e.message}", Toast.LENGTH_SHORT).show()
                Toast.makeText(requireContext(), getString(R.string.toast_squad_update_error), Toast.LENGTH_SHORT).show()
                Log.d("SquadUpdateFragment","Error al actualizar la plantilla: ${e.message}" )
            }
    }
}

