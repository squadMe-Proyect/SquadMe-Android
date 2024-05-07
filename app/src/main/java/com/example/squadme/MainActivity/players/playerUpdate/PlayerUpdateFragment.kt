package com.example.squadme.MainActivity.players.playerUpdate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.squadme.R
import com.example.squadme.databinding.FragmentPlayerUpdateBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerUpdateFragment : Fragment() {
    private lateinit var binding: FragmentPlayerUpdateBinding

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
    }
}