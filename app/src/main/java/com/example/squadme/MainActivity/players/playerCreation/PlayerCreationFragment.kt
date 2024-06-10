package com.example.squadme.MainActivity.players.playerCreation

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.squadme.R
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.FragmentPlayerCreationBinding
import com.example.squadme.utils.FirestoreSingleton
import com.example.squadme.utils.NetworkUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerCreationFragment : Fragment() {
    private lateinit var binding: FragmentPlayerCreationBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = FirestoreSingleton.getInstance()
    private var imageUri: String? = null
    private val sharedPreferences by lazy { requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE) }

    private lateinit var coachEmail: String
    private lateinit var coachPassword: String

    override fun onResume() {
        super.onResume()
        val positions = resources.getStringArray(R.array.positions)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, positions)
        binding.positionItem.setAdapter(arrayAdapter)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPlayerCreationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = Firebase.auth
        val currentUser: FirebaseUser? = firebaseAuth.currentUser

        var teamName: String? = ""

        // Verifica si el usuario está autenticado
        if (currentUser != null) {
            val uid = currentUser.uid

            db.collection("coaches").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        teamName = document.getString("team")
                        coachEmail = document.getString("email") ?: ""
                        coachPassword = getPasswordFromPreferences()
                    }
                }

            imageUri = arguments?.getString("imageUri")
            if (!imageUri.isNullOrEmpty()) {
                val uri = Uri.parse(imageUri)
                binding.photo.setImageURI(uri)

                binding.number.maxValue = 50
                binding.number.minValue = 0

                binding.Cancelar.setOnClickListener {
                    findNavController().navigate(R.id.action_playerCreationFragment_to_playerListFragment)
                }

                binding.Crear.setOnClickListener {
                    if (NetworkUtils.isNetworkAvailable(requireContext())){
                        val coachId = firebaseAuth.currentUser?.uid
                        val name = binding.nameInput.text.toString()
                        val surname = binding.surnameInput.text.toString()
                        val email = binding.emailInput.text.toString()
                        val password = binding.passwordInput.text.toString()
                        val nation = binding.nationInput.text.toString()
                        val number = binding.number.value
                        val position = binding.positionItem.text.toString()
                        coachPassword = binding.passwordInput.text.toString()

                        if (coachId != null && name.isNotEmpty() && surname.isNotEmpty() &&
                            email.isNotEmpty() && password.isNotEmpty() &&
                            nation.isNotEmpty() && imageUri != null) {

                            val uri = Uri.parse(imageUri)
                            uploadImageToFirebaseStorage(uri) { downloadUrl ->
                                registerPlayerAuth(email, password) { playerUid ->
                                    val player = Player(
                                        id = playerUid,
                                        coachId = coachId, // Use the coach's UID here
                                        picture = downloadUrl,
                                        email = email,
                                        name = name,
                                        surname = surname,
                                        teamName = teamName ?: "",
                                        nation = nation,
                                        numbers = number,
                                        position = position,
                                        goal = 0,
                                        assists = 0,
                                        yellowCards = 0,
                                        redCards = 0,
                                        role = "PLAYER"
                                    )
                                    createPlayer(player, playerUid) {
                                        // Volver a iniciar sesión como el coach después de crear el jugador
                                        signInCoachAgain()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(context, getString(R.string.toast_error_player_values_empty), Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(context, getString(R.string.toast_error_no_connection_createPlayer), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri, callback: (String) -> Unit) {
        val storageReference = FirebaseStorage.getInstance().reference.child("images/${System.currentTimeMillis()}.jpg")

        storageReference.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                Log.e("PlayercreationFragment", "Error al subir la imagen", e)
                Toast.makeText(context, getString(R.string.toast_error_player_img_notUpload), Toast.LENGTH_SHORT).show()
            }
    }

    private fun createPlayer(player: Player, playerUid: String, onSuccess: () -> Unit) {
        db.collection("players").document(playerUid)
            .set(player)
            .addOnSuccessListener {
                Log.d("PlayercreationFragment", "Jugador agregado con ID: $playerUid")
                Toast.makeText(context, getString(R.string.toast_player_create), Toast.LENGTH_SHORT).show()
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.d("PlayercreationFragment", "Error al agregar jugador", e)
                Toast.makeText(context, getString(R.string.toast_player_create_error), Toast.LENGTH_SHORT).show()
            }
    }

    private fun registerPlayerAuth(email: String, password: String, onSuccess: (String) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        onSuccess(user.uid)
                    } else {
                        Toast.makeText(context, getString(R.string.toast_player_error_register), Toast.LENGTH_SHORT).show()

                    }
                } else {
                    Log.d("PlayerCreationFragment","Error al registrar el jugador: ${task.exception?.message}" )
                    Toast.makeText(context, getString(R.string.toast_player_error_register), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getPasswordFromPreferences(): String {
        return sharedPreferences.getString("coachPassword", "") ?: ""
    }

    private fun signInCoachAgain() {
        if (coachEmail.isNotEmpty() && coachPassword.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(coachEmail, coachPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Coach ha vuelto a iniciar sesión correctamente")
                        findNavController().navigate(R.id.action_playerCreationFragment_to_playerListFragment)
                    } else {
                        //Toast.makeText(context, "Error al volver a iniciar sesión como coach: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        Log.d("PlayerCreationFragment", "Error al volver a iniciar sesión como coach: ${task.exception?.message}")
                        Toast.makeText(context, getString(R.string.toast_player_error_register_login_as_coach), Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}


