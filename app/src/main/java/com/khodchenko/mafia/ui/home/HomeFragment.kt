package com.khodchenko.mafia.ui.home

import TimerFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.khodchenko.mafia.data.Game
import com.khodchenko.mafia.databinding.FragmentHomeBinding
import com.khodchenko.mafia.ui.PlayerListFragment


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var containerTimerFragment: FrameLayout
    private lateinit var containerPlayerListFragment: FrameLayout
    private val timerFragment = TimerFragment()
    private val playerListFragment = PlayerListFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        containerTimerFragment = binding.containerFragmentTimer
        containerPlayerListFragment = binding.containerFragmentPlayer
        childFragmentManager.beginTransaction()
            .add(containerTimerFragment.id, timerFragment)
            .add(containerPlayerListFragment.id, playerListFragment)
            .commit()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}