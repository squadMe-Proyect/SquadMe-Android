package com.example.squadme.MainActivity.training.trainingList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.squadme.R
import com.example.squadme.databinding.FragmentTrainingListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrainingListFragment : Fragment() {
    private lateinit var binding: FragmentTrainingListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrainingListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createTrain.setOnClickListener {
            val action = TrainingListFragmentDirections.actionTrainingListFragmentToTrainingCreationFragment()
            findNavController().navigate(action)
        }
    }
}