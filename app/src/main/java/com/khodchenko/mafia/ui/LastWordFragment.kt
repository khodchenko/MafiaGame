package com.khodchenko.mafia.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.khodchenko.mafia.databinding.FragmentLastWordBinding


class LastWordFragment : Fragment() {
    private var _binding: FragmentLastWordBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLastWordBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}