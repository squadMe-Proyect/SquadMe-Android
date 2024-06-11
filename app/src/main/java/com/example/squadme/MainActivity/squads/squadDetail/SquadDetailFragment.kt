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
import com.example.squadme.R
import com.example.squadme.data.Models.LineUp
import com.example.squadme.databinding.FragmentSquadDetailBinding
import com.example.squadme.utils.FirestoreSingleton
import com.example.squadme.utils.NetworkUtils
import android.util.Log
import com.example.squadme.MainActivity.matches.matchesDetail.MatchDetailFragmentDirections
import com.example.squadme.utils.UserManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SquadDetailFragment : Fragment() {
    private lateinit var binding: FragmentSquadDetailBinding
    private lateinit var playerAdapter: SquadPlayerAdapter
    private val db = FirestoreSingleton.getInstance()

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

        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.editBtn.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                if (UserManager.isAdmin) {
                    val action = SquadDetailFragmentDirections.actionSquadDetailFragmentToSquadUpdateFragment(squad)
                    findNavController().navigate(action)
                } else {
                    Toast.makeText(requireContext(), "No tienes permiso para editar un jugador.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
            }
        }


        binding.deleteBtn.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                if (UserManager.isAdmin) {
                    eliminarSquad(squad.id)
                } else {
                    Toast.makeText(requireContext(), "No tienes permiso para eliminar una plantilla.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, getString(R.string.toast_error_no_connection_deleteSquad), Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun eliminarSquad(squadId: String?) {
        if (squadId != null) {
            db.collection("squads").document(squadId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context, getString(R.string.toast_squad_delete), Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                .addOnFailureListener { e ->
                    Log.d("SquadDetailFragment","Error al eliminar la plantilla: ${e.message}" )
                    Toast.makeText(context, getString(R.string.toast_squad_delete_error), Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.d("SquadDetailFragment", "ID de plantilla no válido")
            Toast.makeText(context, getString(R.string.toast_squad_delete_error), Toast.LENGTH_SHORT).show()
        }
    }
}