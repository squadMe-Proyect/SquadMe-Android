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
import com.example.squadme.MainActivity.players.playerDetail.PlayerDetailFragmentArgs
import com.example.squadme.R
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.FragmentPlayerUpdateBinding
import com.example.squadme.utils.FirestoreSingleton
import com.example.squadme.utils.NetworkUtils
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerUpdateFragment : Fragment() {
    private lateinit var binding: FragmentPlayerUpdateBinding
    private var selectedImageUri: Uri? = null
    private val db = FirestoreSingleton.getInstance()

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
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerUpdateBinding.inflate(layoutInflater, container, false)
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
            nameInput.setText(player.name)
            surnameInput.setText(player.surname)
            nationInput.setText(player.nation)
            positionItem.setText(player.position)
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
            if (NetworkUtils.isNetworkAvailable(requireContext())){
                updatePlayer(player)
            }else{
                Toast.makeText(context, getString(R.string.toast_error_no_connection_editPlayer), Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Open gallery to select an image for updating player's profile picture.
     */
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        } else {
            Toast.makeText(requireContext(), getString(R.string.error_toast_not_galery_access), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Handle result from gallery activity for selecting an image.
     *
     * @param requestCode The request code passed to startActivityForResult()
     * @param resultCode The result code returned by the child activity
     * @param data An Intent that carries the result data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            binding.photo.setImageURI(selectedImageUri)
        }
    }

    /**
     * Upload selected image to Firebase Storage.
     *
     * @param imageUri The URI of the image to upload
     * @param callback Callback function invoked after image upload completes
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
                Log.e(TAG, "Error al subir la imagen", e)
                Toast.makeText(context, getString(R.string.toast_error_player_img_notUpload), Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Update player details in Firestore database.
     *
     * @param player The Player object whose details are being updated
     */


    private fun updatePlayer(player: Player) {
        db.collection("players")
            .whereEqualTo("name", player.name)
            .whereEqualTo("surname", player.surname)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val playerDocument = documents.first()
                    val docId = playerDocument.id

                    // Obtener los nuevos valores desde los campos de entrada
                    val newPicture = binding.photo.toString()
                    val newName = binding.nameInput.text.toString()
                    val newSurname = binding.surnameInput.text.toString()
                    val newNation = binding.nationInput.text.toString()
                    val newPosition = binding.positionItem.text.toString()
                    val newNumber = binding.number.value
                    val newGoal = binding.goles.value
                    val newAssists = binding.asistenciasPicker.value
                    val newAmarillas = binding.amarillasPicker.value
                    val newRojas = binding.rojasPicker.value

                    // Crear un mapa con los valores actualizados
                    val updatedValues = mutableMapOf<String, Any>()
                    if(newPicture != player.picture) updatedValues["picture"] = newPicture
                    if (newName != player.name) updatedValues["name"] = newName
                    if (newSurname != player.surname) updatedValues["surname"] = newSurname
                    if (newNation != player.nation) updatedValues["nation"] = newNation
                    if (newPosition != player.position) updatedValues["position"] = newPosition
                    if (newNumber != player.numbers) updatedValues["numbers"] = newNumber
                    if (newGoal != player.goal) updatedValues["goal"] = newGoal
                    if (newAssists != player.assists) updatedValues["assists"] = newAssists
                    if (newAmarillas != player.yellowCards) updatedValues["yellowCards"] = newAmarillas
                    if (newRojas != player.redCards) updatedValues["redCards"] = newRojas

                    // Verificar si se han realizado cambios
                    if (updatedValues.isEmpty()) {
                        Toast.makeText(context, getString(R.string.toast_player_not_changes), Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    // Actualizar la imagen del jugador si se ha seleccionado una nueva
                    if (selectedImageUri != null) {
                        uploadImageToFirebaseStorage(selectedImageUri!!) { downloadUrl ->
                            updatedValues["picture"] = downloadUrl
                            updatePlayerInFirestore(docId, updatedValues)
                        }
                    } else {
                        updatedValues["picture"] = player.picture ?: ""
                        updatePlayerInFirestore(docId, updatedValues)
                    }
                } else {
                    Log.d("Firestore", "No se encontró el documento del jugador con el nombre: ${player.name}")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al obtener el documento del jugador", e)
                Toast.makeText(context, getString(R.string.toast_player_update_error), Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Update player document in Firestore with new values.
     *
     * @param docId The document ID of the player in Firestore
     * @param updatedValues Map containing updated player details
     */
    private fun updatePlayerInFirestore(docId: String, updatedValues: Map<String, Any>) {
        db.collection("players").document(docId)
            .update(updatedValues)
            .addOnSuccessListener {
                Toast.makeText(context, getString(R.string.toast_player_update), Toast.LENGTH_SHORT).show()
                val action = PlayerUpdateFragmentDirections.actionPlayerUpdateFragmentToPlayerListFragment()
                findNavController().navigate(action)
            }
            .addOnFailureListener { error ->
                Toast.makeText(context, getString(R.string.toast_player_update_error), Toast.LENGTH_SHORT).show()
                Log.e("Firestore", "Error al actualizar los valores del jugador: ${error.message}")
            }
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
        private const val TAG = "PlayerUpdateFragment"
    }
}
