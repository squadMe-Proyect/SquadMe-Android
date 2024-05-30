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
import com.example.squadme.MainActivity.squads.squadDetail.SquadPlayerAdapter
import com.example.squadme.data.Models.Match
import com.example.squadme.databinding.FragmentMatchDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchDetailFragment : Fragment() {
    private lateinit var binding: FragmentMatchDetailBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var playerAdapter: MatchPlayerLineupAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMatchDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val args: MatchDetailFragmentArgs by navArgs()
        val match: Match = args.match



        binding.matchOpponent.text =  "Oponente: ${match.opponent}"
        binding.matchResult.text = "Resultado: ${match.result}"
        binding.matchDate.text = "Fecha: ${match.date}"

        binding.squadTitle.text = match.squad?.name
        binding.squadFormation.text = match.squad?.lineUp

        playerAdapter = MatchPlayerLineupAdapter(match.squad!!.players)
        binding.playersRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.playersRecyclerView.adapter = playerAdapter

        binding.matchStatus.text = if (match.finished) {
            "Estado: Completado"
        } else {
            "Estado: No Completado"
        }

        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.editBtn.setOnClickListener {
            val action = MatchDetailFragmentDirections.actionMatchDetailFragmentToMatchUpdateFragment(match)
            findNavController().navigate(action)
        }

        binding.deleteBtn.setOnClickListener {
            if (match.finished){
                eliminarMatch(match.id)
            }else{
                Toast.makeText(context, "No puedes eliminar un partido no completado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun eliminarMatch(matchId: String?){
        if (matchId != null) {
            db.collection("matches").document(matchId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Partido eliminado exitosamente", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error al eliminar el partido: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "ID de partido no válido", Toast.LENGTH_SHORT).show()
        }
    }

}