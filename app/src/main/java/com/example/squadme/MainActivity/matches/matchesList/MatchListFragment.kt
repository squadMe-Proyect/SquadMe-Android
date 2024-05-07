package com.example.squadme.MainActivity.matches.matchesList

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.squadme.data.Models.Match
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.FragmentMatchListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchListFragment : Fragment() {
    private lateinit var binding: FragmentMatchListBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserId: String
    private lateinit var adapter: MatchListAdapter

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
        firestore = FirebaseFirestore.getInstance()
        currentUserId = auth.currentUser?.uid ?: ""

        adapter = MatchListAdapter(requireContext()) { _, match ->
            val actionToDetail = MatchListFragmentDirections.actionMatchListFragmentToMatchDetailFragment(match)
            view.findNavController().navigate(actionToDetail)
        }
        binding.matchList.adapter = adapter

        val playersRef = firestore.collection("matches")

        playersRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e(ContentValues.TAG, "Error fetching matches: $exception")
                return@addSnapshotListener
            }

            val matchList = mutableListOf<Match>()
            snapshot?.documents?.forEach { document ->
                val player = document.toObject(Match::class.java)
                player?.let {
                    // Verificar que coachId no sea nulo y coincida con el ID actual
                    if (it.coachId == currentUserId) {
                        matchList.add(it)
                    }
                }
            }
            adapter.submitList(matchList)
        }

        binding.btnCreationMatch.setOnClickListener {
            val action = MatchListFragmentDirections.actionMatchListFragmentToMatchCreationFragment()
            view.findNavController().navigate(action)
        }
    }

}