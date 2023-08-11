package com.khodchenko.mafia.ui

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.khodchenko.mafia.R
import com.khodchenko.mafia.data.Game
import com.khodchenko.mafia.data.Player
import com.khodchenko.mafia.data.Scores
import com.khodchenko.mafia.databinding.FragmentWinBinding


class WinFragment : Fragment(), PlayerAdapter.PlayerClickListener {
    private var winner: String? = ""
    private val scores = Scores()
    private var itemPlayerClicked = false
    private var _binding: FragmentWinBinding? = null
    private val binding get() = _binding!!
    private lateinit var playerAdapter: PlayerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWinBinding.inflate(inflater, container, false)

        if (Game.getInstance().getAllAliveBlackPlayers().size == 0) {
            winner = "красных"
            Game.getInstance().getAllPlayers()
                .filter { it.role == Player.Role.CIVIL || it.role == Player.Role.SHERIFF }
                .forEach { it.score = it.score + scores.WIN_SCORE }
            Game.getInstance().getAllPlayers()
                .filter { it.role == Player.Role.CIVIL || it.role == Player.Role.SHERIFF }.forEach {
                    Log.d(
                        ContentValues.TAG,
                        "Scores: ${it.number} ${it.name} ${it.role}+ ${scores.WIN_SCORE}s"
                    )
                }
        } else {
            winner = "черных"
            Game.getInstance().getAllPlayers()
                .filter { it.role == Player.Role.MAFIA || it.role == Player.Role.DON }
                .forEach { it.score = it.score + scores.WIN_SCORE }
            Game.getInstance().getAllPlayers()
                .filter { it.role == Player.Role.MAFIA || it.role == Player.Role.DON }.forEach {
                Log.d(
                    ContentValues.TAG,
                    "Scores: ${it.number} ${it.name} ${it.role}+ ${scores.WIN_SCORE}s"
                )
            }
        }

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