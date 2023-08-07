package com.khodchenko.mafia.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.khodchenko.mafia.R
import com.khodchenko.mafia.data.Game
import com.khodchenko.mafia.data.Player
import com.khodchenko.mafia.databinding.FragmentWinBinding


class WinFragment : Fragment(), PlayerAdapter.PlayerClickListener {
    private var winner: String? = ""
    private var itemPlayerClicked = false
    private var _binding: FragmentWinBinding? = null
    private val binding get() = _binding!!
    private lateinit var playerAdapter : PlayerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWinBinding.inflate(inflater, container, false)

        if (Game.getInstance().getAllAliveBlackPlayers().size == 0){
            winner = "красных"
        } else winner = "черных"

        binding.tvWinner.text = "Победила команда ${winner}"

        val playerList = Game.getInstance().getAllPlayers()
        playerAdapter = PlayerAdapter(playerList, this)
        binding.recyclerViewWin.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = playerAdapter
        }
        playerAdapter.updatePlayers(Game.getInstance().getAllPlayers())
        playerAdapter.toggleScoresVisibility()

            return _binding?.root ?: inflater.inflate(R.layout.fragment_win, container, false)
    }

    private fun setupRecyclerView() {


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onPlayerClick(player: Player) {
        TODO("Not yet implemented")
    }

    override fun isPlayerSelected(player: Player): Boolean {
        return itemPlayerClicked && player.isOnVote
    }
}