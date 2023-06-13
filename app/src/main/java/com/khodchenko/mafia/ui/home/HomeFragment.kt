package com.khodchenko.mafia.ui.home

import TimerFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.khodchenko.mafia.databinding.FragmentHomeBinding



class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var containerFragment: FrameLayout

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        containerFragment = binding.containerFragmentTimer
        val timerFragment = TimerFragment()
        childFragmentManager.beginTransaction()
            .add(containerFragment.id, timerFragment)
            .commit()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}