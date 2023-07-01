package com.khodchenko.mafia.ui

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.khodchenko.mafia.data.Game
import com.khodchenko.mafia.data.Player
import com.khodchenko.mafia.data.VoteHelper
import com.khodchenko.mafia.databinding.FragmentVotingBinding

class VotingFragment : Fragment(), PlayerAdapter.PlayerClickListener {
    private var _binding: FragmentVotingBinding? = null
    private val binding get() = _binding!!
    private lateinit var playerAdapter: PlayerAdapter
    private lateinit var candidate: Player
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

    override fun onPlayerClick(player: Player) {
        if (isPlayerSelected(player)) {
            VoteHelper.getInstance().removeVoteForCandidate(player)
            //Toast.makeText(requireContext(), "Убрал голос", Toast.LENGTH_SHORT).show()
            playerAdapter.removeSelectedPlayer(player)
            Log.d(TAG, "onPlayerClick: " + player.name + " total votes:" + VoteHelper.getInstance().candidates.keys.joinToString { it.name })
        } else {
            VoteHelper.getInstance().addVoteForCandidate(player)
            //Toast.makeText(requireContext(), "Поставил голос", Toast.LENGTH_SHORT).show()
            playerAdapter.addSelectedPlayer(player)
        }

        playerAdapter.notifyDataSetChanged()
    }

    private fun updatePlayerList() {
        playerAdapter.updatePlayers(Game.getInstance().getAlivePlayers())
    }

    override fun isPlayerSelected(player: Player): Boolean {
        return playerAdapter.isSelected(player)
    }

    private fun setupRecyclerView() {
        playerAdapter = PlayerAdapter(Game.getInstance().getAlivePlayers(), this)

        binding.recyclerViewPlayers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = playerAdapter
        }

        updatePlayerList()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}