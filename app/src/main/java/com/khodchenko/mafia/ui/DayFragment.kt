package com.khodchenko.mafia.ui


import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        val  dialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Выбор игрока")
            setMessage("Вы хотите выставить игрока ${player.name} на голосование?")
            setPositiveButton("Выставить/Отменить") { _, _ ->
                if (!itemPlayerClicked) {
                    Snackbar.make(binding.root, "${Game.getInstance().getCurrentPlayer().name} выставил игрока ${player.name}", Snackbar.LENGTH_SHORT)
                        .show()
                    player.isOnVote = true
                    VoteHelper.getInstance().addCandidate(player)
                } else {
                    Snackbar.make(binding.root, "Вы отменили выбор игрока ${player.name}", Snackbar.LENGTH_SHORT)
                        .show()
                    player.isOnVote = false
                    VoteHelper.getInstance().removeCandidate(player)
                }
            }
            setNegativeButton("Дать фол") { _, _ ->
                player.penalty ++
                Snackbar.make(binding.root, "Дал фол игроку ${player.name}", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }.create()
        dialog.show()

        itemPlayerClicked = !itemPlayerClicked
        playerAdapter.notifyDataSetChanged()
        playerAdapter.updatePlayers(Game.getInstance().getAllPlayers())
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