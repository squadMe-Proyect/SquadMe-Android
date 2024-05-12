package com.example.squadme.MainActivity.players.playerUpdate

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.squadme.MainActivity.matches.matchesUpdate.MatchUpdateFragmentDirections
import com.example.squadme.MainActivity.players.playerDetail.PlayerDetailFragmentArgs
import com.example.squadme.R
import com.example.squadme.data.Models.Match
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.FragmentPlayerUpdateBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerUpdateFragment : Fragment() {
    private lateinit var binding: FragmentPlayerUpdateBinding
    private var selectedImageUri: Uri? = null
    private val db = Firebase.firestore

    override fun onResume() {
        super.onResume()
        val positions = resources.getStringArray(R.array.positions)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, positions)
        binding.positionItem.setAdapter(arrayAdapter)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerUpdateBinding.inflate(layoutInflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: PlayerDetailFragmentArgs by navArgs()
        val player: Player = args.player

        binding.number.maxValue = 50
        binding.number.minValue = 0

        binding.amarillasPicker.maxValue = 50
        binding.amarillasPicker.minValue = 0

        binding.rojasPicker.maxValue = 50
        binding.rojasPicker.minValue = 0

        binding.goles.maxValue = 50
        binding.goles.minValue = 0

        binding.asistenciasPicker.maxValue = 50
        binding.asistenciasPicker.minValue = 0


        binding.apply {
            photo.load(player.picture)
            // Asigna el nombre del jugador al TextInputEditText correspondiente
            nameInput.setText(player.name)

            // Asigna el apellido del jugador al TextInputEditText correspondiente
            surnameInput.setText(player.surname)

            // Asigna la nación del jugador al TextInputEditText correspondiente
            nationInput.setText(player.nation)

            // Asigna la posición del jugador al AutoCompleteTextView correspondiente
            positionItem.setText(player.position)

            // Asigna el dorsal del jugador al TextView correspondiente
            number.value = player.numbers!!

            goles.value = player.goal!!
            asistenciasPicker.value = player.assists!!
            amarillasPicker.value = player.yellowCards!!
            rojasPicker.value = player.redCards!!
        }

        binding.editImageBtn.setOnClickListener {
            openGallery()
        }
        binding.cancelButton.setOnClickListener {
            val action = PlayerUpdateFragmentDirections.actionPlayerUpdateFragmentToPlayerListFragment()
            findNavController().navigate(action)
        }

        binding.editarButton.setOnClickListener {
            updatePlayer(player)
        }


    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        } else {
            Toast.makeText(requireContext(), "No se pudo acceder a la galería", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            binding.photo.setImageURI(selectedImageUri)
        }
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
    }

    private fun updatePlayer(player: Player){
        db.collection("players")
            .whereEqualTo("name", player.name)
            .whereEqualTo("surname", player.surname)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val playerDocument = documents.first()
                    val docId = playerDocument.id

                    val newPictureUri = selectedImageUri ?: player.picture

                    val newName = binding.nameInput.text.toString()
                    val newSurname = binding.surnameInput.text.toString()
                    val newNation = binding.nationInput.text.toString()
                    val newPosition = binding.positionItem.text.toString()
                    val newNumber = binding.number.value
                    val newGoal = binding.goles.value
                    val newAssits = binding.asistenciasPicker.value
                    val newAmarillas = binding.amarillasPicker.value
                    val newRojas = binding.rojasPicker.value

                    // Crear un objeto Map con los nuevos valores del jugador
                    val updatedValues = hashMapOf(
                        "picture" to newPictureUri.toString(),
                        "name" to newName,
                        "surname" to newSurname,
                        "nation" to newNation,
                        "position" to newPosition,
                        "numbers" to newNumber,
                        "goal" to newGoal,
                        "assists" to newAssits,
                        "yellowCards" to newAmarillas,
                        "redCard" to newRojas
                    )

                    // Actualizar el jugador en Firestore
                    db.collection("players").document(docId)
                        .update(updatedValues as Map<String, Any>)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Jugador actualizado", Toast.LENGTH_SHORT).show()
                            val action = PlayerUpdateFragmentDirections.actionPlayerUpdateFragmentToPlayerListFragment()
                            findNavController().navigate(action)
                        }
                        .addOnFailureListener { error ->
                            Toast.makeText(context, "Error al actualizar el jugador", Toast.LENGTH_SHORT).show()
                            Log.e("Firestore", "Error al actualizar los valores del jugador: ${error.message}")
                        }
                } else {
                    Log.d("Firestore", "No se encontró el documento del jugador con el nombre: ${player.name}")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al obtener el documento del jugador", e)
                Toast.makeText(context, "Error al actualizar el jugador", Toast.LENGTH_SHORT).show()
            }
    }


}