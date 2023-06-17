package com.khodchenko.mafia.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.khodchenko.mafia.data.Game
import com.khodchenko.mafia.data.Player
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
      //  playerAdapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        playerList = Game.getInstance().getAllPlayers()
        playerAdapter = PlayerAdapter(playerList) { player ->
            onPlayerChanged(player)
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