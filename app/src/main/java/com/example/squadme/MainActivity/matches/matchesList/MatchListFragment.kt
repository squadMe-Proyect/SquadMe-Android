package com.example.squadme.MainActivity.matches.matchesList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.squadme.R
import com.example.squadme.databinding.FragmentMatchListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchListFragment : Fragment() {
    private lateinit var binding: FragmentMatchListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMatchListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCreationMatch.setOnClickListener {
            val action = MatchListFragmentDirections.actionMatchListFragmentToMatchCreationFragment()
            view.findNavController().navigate(action)
        }
    }

}