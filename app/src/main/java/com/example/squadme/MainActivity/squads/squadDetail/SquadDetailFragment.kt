package com.example.squadme.MainActivity.squads.squadDetail


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.squadme.MainActivity.players.playerDetail.PlayerDetailFragmentArgs
import com.example.squadme.data.Models.LineUp
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.FragmentSquadDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SquadDetailFragment : Fragment() {
    private lateinit var binding: FragmentSquadDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSquadDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: SquadDetailFragmentArgs by navArgs()
        val squad: LineUp = args.squad
    }
}