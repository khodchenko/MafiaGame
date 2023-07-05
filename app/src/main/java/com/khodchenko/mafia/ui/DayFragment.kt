package com.khodchenko.mafia.ui


import android.app.AlertDialog
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

class DayFragment : Fragment() , PlayerAdapter.PlayerClickListener{

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

    override fun onPlayerClick(player: Player) {
        val dialogBuilder = AlertDialog.Builder(requireContext()).apply {
            setTitle("Выбор игрока")
            setMessage("Вы хотите выставить игрока ${player.name} на голосование?")
            setPositiveButton("Выставить/Отменить") { _, _ ->
                val currentPlayer = Game.getInstance().getCurrentPlayer()

                val message = if (!itemPlayerClicked) {
                    player.isOnVote = true
                    VoteHelper.getInstance().addCandidate(player)
                    itemPlayerClicked = true
                    "Игрок ${currentPlayer.name} выставил игрока ${player.name}"
                } else {
                    player.isOnVote = false
                    VoteHelper.getInstance().removeCandidate(player)
                    itemPlayerClicked = false
                    "Вы отменили выбор игрока ${player.name}"
                }

                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                playerAdapter.notifyDataSetChanged()
                playerAdapter.updatePlayers(Game.getInstance().getAllPlayers())

                val playersOnVote = VoteHelper.getInstance().candidates.keys.joinToString { it.name }
                Toast.makeText(requireContext(), "Игроки на голосовании: $playersOnVote", Toast.LENGTH_SHORT).show()
            }
            setNegativeButton("Дать фол") { _, _ ->
                player.penalty++
                Snackbar.make(binding.root, "Дал фол игроку ${player.name}", Snackbar.LENGTH_SHORT).show()
                playerAdapter.notifyDataSetChanged()
                playerAdapter.updatePlayers(Game.getInstance().getAllPlayers())
            }
        }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    override fun isPlayerSelected(player: Player): Boolean {
        return itemPlayerClicked && player.isOnVote
    }

    fun onPlayerChanged(player: Player) {
        currentPlayer = player
        itemPlayerClicked = false
    }

    private fun setupRecyclerView() {
        playerList = Game.getInstance().getAlivePlayers()
        playerAdapter = PlayerAdapter(playerList, this)

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