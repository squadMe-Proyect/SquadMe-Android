package com.example.squadme.MainActivity.players.playerDetail

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.FragmentPlayerDetailBinding
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerDetailFragment : Fragment() {
    private lateinit var binding: FragmentPlayerDetailBinding

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

        /*
        // Obtener el argumento del nombre del jugador
        val playerName = arguments?.getString("name")

        // Establecer el nombre del jugador en la vista
        binding.playerName.text = playerName
    */
        val args: PlayerDetailFragmentArgs by navArgs()
        val player: Player = args.player



        binding.playerImg.load(player.picture)
        binding.playerName.text = "Nombre ${player.name}"
        binding.playerSurname.text = "Apellido ${player.surname}"
        binding.playerEmail.text = "Email ${player.email}"
        binding.playerNation.text = "Nacionalidad ${player.nation}"
        binding.playerTeam.text = "Equipo ${player.teamName}"
        binding.playerPosition.text = "Posición ${player.position}"
        binding.playerDorsal.text = "Dorsal ${player.numbers}"
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
            val db = FirebaseFirestore.getInstance()

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
                                // Regresar al fragmento anterior después de eliminar el jugador
                                findNavController().popBackStack()
                            }
                            ?.addOnFailureListener { e ->
                                // Error al eliminar el jugador
                                Log.w(TAG, "Error deleting player", e)
                                // Manejar el error según sea necesario (por ejemplo, mostrar un mensaje de error)
                            }
                    } else {
                        Log.d(TAG, "No player document found with name: ${player.name}")
                    }
                }
                .addOnFailureListener { e ->
                    // Error al realizar la consulta
                    Log.w(TAG, "Error querying player document", e)
                    // Manejar el error según sea necesario (por ejemplo, mostrar un mensaje de error)
                }
        }


    }

}