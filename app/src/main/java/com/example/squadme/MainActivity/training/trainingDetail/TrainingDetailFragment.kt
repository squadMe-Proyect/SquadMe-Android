package com.example.squadme.MainActivity.training.trainingDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.squadme.R
import com.example.squadme.databinding.FragmentTrainingDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrainingDetailFragment : Fragment() {
    private lateinit var binding: FragmentTrainingDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrainingDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}