package com.example.squadme.MainActivity.players.playerCreation

import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.squadme.R
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.FragmentPlayerCreationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerCreationFragment : Fragment() {
    private lateinit var binding: FragmentPlayerCreationBinding
    private lateinit var firebaseAuth: FirebaseAuth
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
        binding = FragmentPlayerCreationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        firebaseAuth = Firebase.auth
        val currentUser: FirebaseUser? = firebaseAuth.currentUser

        var teamName:String? = ""

        // Verifica si el usuario está autenticado
        if (currentUser != null) {
            val uid = currentUser.uid

            db.collection("coaches").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        teamName = document.getString("team")
                    }
                }


            val imageUri = arguments?.getString("imageUri")
            if (!imageUri.isNullOrEmpty()) {
                // Convertir la URI a un objeto Uri
                val uri = Uri.parse(imageUri)
                binding.photo.setImageURI(uri)



                binding.number.maxValue = 50
                binding.number.minValue = 0

                binding.Cancelar.setOnClickListener {
                    findNavController().navigate(R.id.action_playerCreationFragment_to_playerListFragment)
                }

                binding.Crear.setOnClickListener {
                    val coachId = firebaseAuth.currentUser?.uid
                    val name = binding.nameInput.text.toString()
                    val surname = binding.surnameInput.text.toString()
                    val pictureUri = imageUri // Aquí deberías tener la URI de la imagen
                    val email = binding.emailInput.text.toString()
                    val nation = binding.nationInput.text.toString()
                    val number = binding.number.value
                    val position = binding.positionItem.text.toString()

                    // Verificamos que todos los campos obligatorios estén llenos
                    if (coachId != null && name.isNotEmpty() && surname.isNotEmpty() && pictureUri != null &&
                        email.isNotEmpty() && nation.isNotEmpty()
                    ) {
                        // Creamos el objeto Player con los datos recolectados
                        val player = Player(
                            coachId = coachId,
                            picture = pictureUri.toString(), // Convertimos la URI a String
                            email = email,
                            name = name,
                            surname = surname,
                            teamName = teamName?:"", // Debes obtener este valor correctamente del documento del entrenador
                            nation = nation,
                            numbers = number,
                            position = position,
                            goal = 0,
                            assists = 0,
                            yellowCards = 0,
                            redCards = 0
                        )

                        // Llamamos a la función para crear el jugador en Firestore
                        createPlayer(player)
                    }
                }
            }
        }
    }
    private fun createPlayer(player: Player) {
        // Agregamos el jugador a la colección "players" en Firestore
        db.collection("players")
            .add(player)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Jugador agregado con ID: ${documentReference.id}")
                Toast.makeText(context, "Jugador creado exitosamente", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.action_playerCreationFragment_to_playerListFragment)

            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Error al agregar jugador", e)
                Toast.makeText(context, "Error al crear jugador", Toast.LENGTH_SHORT).show()
            }
    }
}