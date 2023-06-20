package com.khodchenko.mafia.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.khodchenko.mafia.data.Game
import com.khodchenko.mafia.data.Player
import com.khodchenko.mafia.data.VoteHelper
import com.khodchenko.mafia.databinding.FragmentPlayerListBinding

class DayFragment : Fragment() {

    interface OnPlayerChangeListener {
        fun onPlayerChanged(player: Player)
    }

    private var playerChangeListener: OnPlayerChangeListener? = null
    private var _binding: FragmentPlayerListBinding? = null
    private val binding get() = _binding!!
    private lateinit var playerAdapter: PlayerAdapter
    private var playerList: MutableList<Player> = mutableListOf()
    private var currentPlayer: Player? = null
    private var itemPlayerClicked = false

    fun setOnPlayerChangeListener(listener: OnPlayerChangeListener) {
        playerChangeListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()

        return root
    }

    fun onPlayerChanged(player: Player) {
        currentPlayer = player
        itemPlayerClicked = false
    }

    private fun setupRecyclerView() {
        playerList = Game.getInstance().getAllPlayers()
        playerAdapter = PlayerAdapter(playerList) { player ->
            onPlayerChanged(player)
            Toast.makeText(requireContext(), player.name, Toast.LENGTH_SHORT).show()
            if (!itemPlayerClicked) {
                Snackbar.make(binding.root, "${Game.getInstance().getCurrentPlayer().name} выставил игрока ${player.name}", Snackbar.LENGTH_SHORT)
                    .show()
                player.isOnVote = true
               VoteHelper.getInstance().add(player)
                itemPlayerClicked = true
                playerAdapter.notifyDataSetChanged()
            } else {
                Snackbar.make(binding.root, "Вы отменили выбор игрока ${player.name}", Snackbar.LENGTH_SHORT)
                    .show()
                player.isOnVote = false
                VoteHelper.getInstance().remove(player)
                itemPlayerClicked = false
                playerAdapter.notifyDataSetChanged()
            }

        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = playerAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}