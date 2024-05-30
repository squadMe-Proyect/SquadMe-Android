package com.example.squadme.MainActivity.squads.squadDetail


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.squadme.data.Models.LineUp
import com.example.squadme.databinding.FragmentSquadDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SquadDetailFragment : Fragment() {
    private lateinit var binding: FragmentSquadDetailBinding
    private lateinit var playerAdapter: SquadPlayerAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSquadDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: SquadDetailFragmentArgs by navArgs()
        val squad: LineUp = args.squad

        binding.squadTitle.text = squad.name
        binding.squadFormation.text = squad.lineUp

        playerAdapter = SquadPlayerAdapter(squad.players)
        binding.playersRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.playersRecyclerView.adapter = playerAdapter

        binding.editBtn.setOnClickListener {
            val action = SquadDetailFragmentDirections.actionSquadDetailFragmentToSquadUpdateFragment(squad)
            findNavController().navigate(action)
        }

        binding.deleteBtn.setOnClickListener {
            eliminarSquad(squad.id)
        }
    }


    private fun eliminarSquad(squadId: String?) {
        if (squadId != null) {
            db.collection("squads").document(squadId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Plantilla eliminado exitosamente", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error al eliminar la plantilla: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "ID de plantilla no válido", Toast.LENGTH_SHORT).show()
        }
    }
}