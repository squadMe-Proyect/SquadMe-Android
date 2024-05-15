package com.example.squadme.MainActivity.squads.squadList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.squadme.databinding.FragmentSquadListBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SquadListFragment : Fragment() {
    private lateinit var binding:FragmentSquadListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSquadListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createBtn.setOnClickListener {
            val action = SquadListFragmentDirections.actionSquadListFragmentToSquadCreationFragment()
            findNavController().navigate(action)
        }
    }


}