package com.example.squadme.MainActivity.players.playerCreation

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

    /**
     * Initialize the spinner adapter with formations on resume
     */
    override fun onResume() {
        super.onResume()
        val positions = resources.getStringArray(R.array.positions)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, positions)
        binding.positionItem.setAdapter(arrayAdapter)
    }

    /**
     * Inflate the layout for this fragment and initialize view binding
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     * @return Return the View for the fragment's UI
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPlayerCreationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    /**
     * Set up the view once it has been created
     *
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle)
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = Firebase.auth
        val currentUser: FirebaseUser? = firebaseAuth.currentUser

        var teamName: String? = ""

        if (currentUser != null) {
            val uid = currentUser.uid

            db.collection("coaches").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        teamName = document.getString("team")
                        coachEmail = document.getString("email") ?: ""
                        coachPassword = getPasswordFromPreferences()
                        saveCoachCredentialsToPreferences(coachEmail, coachPassword)
                        Log.d("PlayerCreationFragment", "Retrieved email: $coachEmail and password: $coachPassword")
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

                        if (coachId != null && name.isNotEmpty() && surname.isNotEmpty() &&
                            email.isNotEmpty() && password.isNotEmpty() &&
                            nation.isNotEmpty() && imageUri != null) {

                            val uri = Uri.parse(imageUri)
                            uploadImageToFirebaseStorage(uri) { downloadUrl ->
                                registerPlayerAuth(email, password) { playerUid ->
                                    val player = Player(
                                        id = playerUid,
                                        coachId = coachId,
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
                                        signInCoachAgain()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(context, getString(R.string.toast_error_player_values_empty), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, getString(R.string.toast_error_no_connection_createPlayer), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /**
     * Uploads the player's image to Firebase Storage.
     *
     * @param imageUri The URI of the player's image.
     * @param callback The callback function called after uploading the image with the download URL.
     */
    private fun uploadImageToFirebaseStorage(imageUri: Uri, callback: (String) -> Unit) {
        val storageReference = FirebaseStorage.getInstance().reference.child("images/${System.currentTimeMillis()}.jpg")

        storageReference.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                Log.e("PlayerCreationFragment", "Error al subir la imagen", e)
                Toast.makeText(context, getString(R.string.toast_error_player_img_notUpload), Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Registers the player in Firebase Authentication.
     *
     * @param email The player's email address.
     * @param password The player's password.
     * @param onSuccess The callback function called after successfully registering the player with the player's UID.
     */
    private fun createPlayer(player: Player, playerUid: String, onSuccess: () -> Unit) {
        db.collection("players").document(playerUid)
            .set(player)
            .addOnSuccessListener {
                Log.d("PlayerCreationFragment", "Jugador agregado con ID: $playerUid")
                Toast.makeText(context, getString(R.string.toast_player_create), Toast.LENGTH_SHORT).show()
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.d("PlayerCreationFragment", "Error al agregar jugador", e)
                Toast.makeText(context, getString(R.string.toast_player_create_error), Toast.LENGTH_SHORT).show()
            }
    }


    /**
     * Obtiene la contraseña del coach desde SharedPreferences.
     *
     * @return Contraseña del coach.
     */

    private fun registerPlayerAuth(email: String, password: String, onSuccess: (String) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        onSuccess(user.uid)
                    } else {
                        Log.d("PlayerCreationFragment", "Error al registrar el jugador: Usuario no encontrado")
                        Toast.makeText(context, getString(R.string.toast_player_error_register), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val exceptionMessage = task.exception?.message ?: "Unknown error"
                    Log.d("PlayerCreationFragment", "Error al registrar el jugador: $exceptionMessage")
                    Toast.makeText(context, getString(R.string.toast_player_error_register) + ": " + exceptionMessage, Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.d("PlayerCreationFragment", "Error al registrar el jugador: ${e.message}")
                Toast.makeText(context, getString(R.string.toast_player_error_register) + ": " + e.message, Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Retrieves the coach's email from SharedPreferences.
     *
     * @return The coach's email.
     */
    private fun getEmailFromPreferences(): String {
        return sharedPreferences.getString("coachEmail", "") ?: ""
    }

    /**
     * Retrieves the coach's password from SharedPreferences.
     *
     * @return The coach's password.
     */
    private fun getPasswordFromPreferences(): String {
        return sharedPreferences.getString("coachPassword", "") ?: ""
    }

    /**
     * Save the coach's password and email from SharedPreferences.
     *
     */
    private fun saveCoachCredentialsToPreferences(email: String, password: String) {
        with(sharedPreferences.edit()) {
            putString("coachEmail", email)
            putString("coachPassword", password)
            apply()
        }
    }

    /**
     * Re-logs in as a coach after creating the player.
     */
    private fun signInCoachAgain() {
        coachEmail = getEmailFromPreferences()
        coachPassword = getPasswordFromPreferences()
        if (coachEmail.isNotEmpty() && coachPassword.isNotEmpty()) {
            Log.d("PlayerCreationFragment", "Attempting to sign in coach with email: $coachEmail and password: $coachPassword")
            firebaseAuth.signInWithEmailAndPassword(coachEmail, coachPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("PlayerCreationFragment", "Coach ha vuelto a iniciar sesión correctamente")
                        findNavController().navigate(R.id.action_playerCreationFragment_to_playerListFragment)
                    } else {
                        Log.d("PlayerCreationFragment", "Error al volver a iniciar sesión como coach: ${task.exception?.message}")
                        Toast.makeText(context, getString(R.string.toast_player_error_register_login_as_coach), Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}



