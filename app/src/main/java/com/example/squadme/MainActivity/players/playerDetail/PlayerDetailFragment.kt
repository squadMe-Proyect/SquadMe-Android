package com.example.squadme.MainActivity.players.playerDetail

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.squadme.MainActivity.players.playerList.PlayerListFragmentDirections
import com.example.squadme.R
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.FragmentPlayerDetailBinding
import com.example.squadme.utils.FirestoreSingleton
import com.example.squadme.utils.NetworkUtils
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerDetailFragment : Fragment() {
    private lateinit var binding: FragmentPlayerDetailBinding
    private val db = FirestoreSingleton.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: PlayerDetailFragmentArgs by navArgs()
        val player: Player = args.player



        binding.playerImg.load(player.picture)
        binding.playerName.text = getString(R.string.player_detail_name_text) + " ${player.name}"
        binding.playerSurname.text = getString(R.string.player_detail_surname_text) +  " ${player.surname}"
        binding.playerEmail.text = getString(R.string.player_detail_email_text) +  " ${player.email}"
        binding.playerNation.text =getString(R.string.player_detail_nacionality_text) +  " ${player.nation}"
        binding.playerTeam.text = getString(R.string.player_detail_team_text) + " ${player.teamName}"
        binding.playerPosition.text = getString(R.string.player_detail_position_text) +  " ${player.position}"
        binding.playerDorsal.text = getString(R.string.player_detail_number_text) +  " ${player.numbers}"
        binding.goles.text = "${player.goal}"
        binding.asistencias.text =  "${player.assists}"
        binding.amarillas.text = "${player.yellowCards}"
        binding.rojas.text = "${player.redCards}"

        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.editBtn.setOnClickListener {
            val action = PlayerDetailFragmentDirections.actionPlayerDetailFragmentToPlayerUpdateFragment(player)
            findNavController().navigate(action)
        }

        binding.deleteBtn.setOnClickListener {
            if(NetworkUtils.isNetworkAvailable(requireContext())){
                var playerRef: DocumentReference? = null

                db.collection("players")
                    .whereEqualTo("name", player.name)
                    .whereEqualTo("surname", player.surname)
                    .whereEqualTo("numbers", player.numbers)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            val playerDocument = documents.first()
                            playerRef = playerDocument.reference
                            Log.d("Documento" ,"${playerRef.toString()}")

                            // Eliminar el jugador de Firestore
                            playerRef?.delete()
                                ?.addOnSuccessListener {
                                    // Éxito al eliminar el jugador
                                    Log.d(TAG, "Player deleted successfully")
                                    Toast.makeText(requireContext(), getString(R.string.toast_player_delete), Toast.LENGTH_SHORT).show()
                                    // Regresar al fragmento anterior después de eliminar el jugador
                                    findNavController().popBackStack()
                                }
                                ?.addOnFailureListener { e ->
                                    // Error al eliminar el jugador
                                    Log.w(TAG, "Error deleting player", e)
                                    Toast.makeText(requireContext(), getString(R.string.toast_player_delete_error), Toast.LENGTH_SHORT).show()
                                    // Manejar el error según sea necesario (por ejemplo, mostrar un mensaje de error)
                                }
                        } else {
                            Log.d(TAG, "No player document found with name: ${player.name}")
                        }
                    }
                    .addOnFailureListener { e ->
                        // Error al realizar la consulta
                        Log.w(TAG, "Error querying player document", e)
                    }
            }else{
                Toast.makeText(context, getString(R.string.toast_error_no_connection_deletePlayer), Toast.LENGTH_SHORT).show()
            }
        }
    }
}