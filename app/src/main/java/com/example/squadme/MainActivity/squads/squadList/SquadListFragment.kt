package com.example.squadme.MainActivity.squads.squadList

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.squadme.MainActivity.players.playerList.PlayerListAdapter
import com.example.squadme.MainActivity.players.playerList.PlayerListFragmentDirections
import com.example.squadme.data.Models.LineUp
import com.example.squadme.databinding.FragmentSquadListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SquadListFragment : Fragment() {
    private lateinit var binding:FragmentSquadListBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserId: String
    private lateinit var adapter: SquadListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSquadListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUserId = auth.currentUser?.uid ?: ""

        adapter = SquadListAdapter { _, squad ->
            val actionToDetail = SquadListFragmentDirections.actionSquadListFragmentToSquadDetailFragment(squad)
            view.findNavController().navigate(actionToDetail)
        }

        binding.squadList.adapter = adapter

        val squadsRef = firestore.collection("squads")

        squadsRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e(ContentValues.TAG, "Error fetching squads: $exception")
                return@addSnapshotListener
            }

            val squadList = mutableListOf<LineUp>()
            snapshot?.documents?.forEach { document ->
                val player = document.toObject(LineUp::class.java)
                player?.let {
                    // Verificar que coachId no sea nulo y coincida con el ID actual
                    if (it.coachId == currentUserId) {
                        squadList.add(it)
                    }
                }
            }
            adapter.submitList(squadList)
        }

        binding.createBtn.setOnClickListener {
            val action = SquadListFragmentDirections.actionSquadListFragmentToSquadCreationFragment()
            findNavController().navigate(action)
        }
    }


}