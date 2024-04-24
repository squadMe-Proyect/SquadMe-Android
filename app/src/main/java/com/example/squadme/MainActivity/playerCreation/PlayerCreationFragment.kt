package com.example.squadme.MainActivity.playerCreation

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import androidx.navigation.fragment.findNavController
import com.example.squadme.R
import com.example.squadme.databinding.FragmentPlayerCreationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerCreationFragment : Fragment() {
    private lateinit var binding: FragmentPlayerCreationBinding




    override fun onResume() {
        super.onResume()
        val positions =resources.getStringArray(R.array.positions)
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

        val imageUri = arguments?.getString("imageUri")
        if (!imageUri.isNullOrEmpty()) {
            // Convertir la URI a un objeto Uri
            val uri = Uri.parse(imageUri)
            binding.photo.setImageURI(uri)



            binding.number.maxValue=50
            binding.number.minValue=0

            binding.Cancelar.setOnClickListener {
                findNavController().navigate(R.id.action_playerCreationFragment_to_playerListFragment)
            }
        }
    }

}