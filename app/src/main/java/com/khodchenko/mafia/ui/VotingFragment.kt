package com.khodchenko.mafia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.khodchenko.mafia.data.Game
import com.khodchenko.mafia.data.Player
import com.khodchenko.mafia.databinding.FragmentVotingBinding

class VotingFragment : Fragment() {
    private var _binding: FragmentVotingBinding? = null
    private val binding get() = _binding!!
    private var alivePlayerList: MutableList<Player> = mutableListOf()
    private lateinit var playerAdapter: PlayerAdapter
    private lateinit var candidate : Player
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVotingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()

        return root
    }
    private fun setupRecyclerView() {
        alivePlayerList = Game.getInstance().getAlivePlayers()
        playerAdapter = PlayerAdapter(alivePlayerList) { player ->


        }

        binding.recyclerViewPlayers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = playerAdapter
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}