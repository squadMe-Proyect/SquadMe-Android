package com.example.squadme.MainActivity.playerList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.squadme.R
import com.example.squadme.databinding.FragmentPlayerListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerListFragment : Fragment() {

    private lateinit var binding: FragmentPlayerListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCreationPlayer.setOnClickListener {
            val action = PlayerListFragmentDirections.actionPlayerListFragmentToCameraPreviewFragment()
            view.findNavController().navigate(action)
        }
    }

}