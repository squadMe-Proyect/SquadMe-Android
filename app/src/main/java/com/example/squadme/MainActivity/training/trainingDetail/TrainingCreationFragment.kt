package com.example.squadme.MainActivity.training.trainingDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.squadme.databinding.FragmentTrainingCreationBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TrainingCreationFragment : Fragment() {
    private lateinit var binding: FragmentTrainingCreationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrainingCreationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}