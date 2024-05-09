package com.example.squadme.MainActivity.players.playerUpdate

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.squadme.MainActivity.players.playerDetail.PlayerDetailFragmentArgs
import com.example.squadme.R
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.FragmentPlayerUpdateBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerUpdateFragment : Fragment() {
    private lateinit var binding: FragmentPlayerUpdateBinding
    private var selectedImageUri: Uri? = null

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


    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
            //startActivityForResult(intent, GALLERY_REQUEST_CODE)
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

}