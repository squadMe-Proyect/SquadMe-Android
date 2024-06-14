package com.example.squadme.MainActivity.matches.matchesDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.squadme.R
import com.example.squadme.data.Models.Match
import com.example.squadme.databinding.FragmentMatchDetailBinding
import com.example.squadme.utils.FirestoreSingleton
import com.example.squadme.utils.NetworkUtils
import android.util.Log
import com.example.squadme.utils.UserManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchDetailFragment : Fragment() {
    private lateinit var binding: FragmentMatchDetailBinding
    private lateinit var playerAdapter: MatchPlayerLineupAdapter
    private val db = FirestoreSingleton.getInstance()
    private var isAdmin: Boolean = false

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     *                  The fragment should not add the view itself, but this can be used to generate
     *                  the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMatchDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called immediately after `onCreateView` has returned, but before any saved state has been restored in the view.
     *
     * @param view The View returned by `onCreateView`.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: MatchDetailFragmentArgs by navArgs()
        val match: Match = args.match

        // Mostrar detalles del partido
        binding.matchOpponent.text = getString(R.string.match_detail_oponent_text) + "${match.opponent}"
        binding.matchResult.text = getString(R.string.match_detail_result_text) + "${match.result}"
        binding.matchDate.text = getString(R.string.match_detail_date_text) + "${match.date}"

        // Mostrar detalles de la plantilla
        binding.squadTitle.text = match.squad?.name
        binding.squadFormation.text = match.squad?.lineUp

        // Asegúrate de que la lista de jugadores no sea null
        match.squad?.players?.let { players ->
            playerAdapter = MatchPlayerLineupAdapter(players)
            binding.playersRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.playersRecyclerView.adapter = playerAdapter
        }

        binding.matchStatus.text = if (match.finished) {
            getString(R.string.match_detail_state_completed)
        } else {
            getString(R.string.match_detail_state_non_completed)
        }

        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.editBtn.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                if (UserManager.isAdmin) {
                    val action = MatchDetailFragmentDirections.actionMatchDetailFragmentToMatchUpdateFragment(match)
                    findNavController().navigate(action)
                } else {
                    Toast.makeText(requireContext(), getString(R.string.toast_no_permissions_edit_match), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.toast_no_connection_match_detail), Toast.LENGTH_SHORT).show()
            }
        }

        binding.deleteBtn.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                if (UserManager.isAdmin) {
                    if (match.finished) {
                        eliminarMatch(match.id)
                    } else {
                        Toast.makeText(context, getString(R.string.toast_error_no_completed_match_delete), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.toast_no_permissions_delete_match), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, getString(R.string.toast_error_no_connection_deleteMatch), Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Deletes a match from Firestore.
     *
     * @param matchId The ID of the match to be deleted.
     */
    private fun eliminarMatch(matchId: String?) {
        if (matchId != null) {
            db.collection("matches").document(matchId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context, getString(R.string.toast_match_delete), Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                .addOnFailureListener { e ->
                    Log.d("MatchDetailFragment", "Error al eliminar el partido: ${e.message}")
                    Toast.makeText(context, getString(R.string.toast_match_delete_error), Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.d("MatchDetailFragment", "ID de partido no válido")
        }
    }
}
