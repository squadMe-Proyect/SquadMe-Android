package com.example.squadme.MainActivity.playerCreation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.squadme.R
import com.example.squadme.databinding.FragmentPlayerCreationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerCreationFragment : Fragment() {

    private lateinit var binding: FragmentPlayerCreationBinding

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
    }

}