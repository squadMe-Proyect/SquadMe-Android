package com.example.squadme.MainActivity.matches.matchesCreation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.squadme.R
import com.example.squadme.databinding.FragmentMatchCreationBinding
import com.example.squadme.databinding.FragmentPlayerCreationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchCreationFragment : Fragment() {
    private lateinit var binding: FragmentMatchCreationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMatchCreationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.Cancelar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}