package com.khodchenko.mafia.ui

import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.khodchenko.mafia.data.Game
import com.khodchenko.mafia.data.Player
import com.khodchenko.mafia.databinding.FragmentNightBinding


class NightFragment : Fragment() {

    private var _binding: FragmentNightBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNightBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val blackPlayers = Game.getInstance().getAllBlackPlayers()
        if (blackPlayers.isNotEmpty()) {
            val tvPlayerRole1 = binding.tvPlayerRole1
            if (blackPlayers[0].isAlive) {
                tvPlayerRole1.text = blackPlayers[0].name
            }

            val tvPlayerRole2 = binding.tvPlayerRole2
            if (blackPlayers.size > 1 && blackPlayers[1].isAlive) {
                tvPlayerRole2.text = blackPlayers[1].name
            }

            val tvPlayerRole3 = binding.tvPlayerRole3
            if (blackPlayers.size > 2 && blackPlayers[2].isAlive) {
                tvPlayerRole3.text = blackPlayers[2].name
            }
        }

        val spinnerTarget1 = binding.spinnerTarget1
        val spinnerTarget2 = binding.spinnerTarget2
        val spinnerTarget3 = binding.spinnerTarget3
        // Populate spinners with player names
        val playerList = getPlayerList()
        val playerNames = playerList.map { it.name }
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, playerNames)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinnerTarget1.adapter = adapter
        spinnerTarget2.adapter = adapter
        spinnerTarget3.adapter = adapter

        binding.buttonShoot.setOnClickListener {
            val targets : MutableList<Player> = mutableListOf()
            targets.add(spinnerTarget1.selectedItem as Player)
            targets.add(spinnerTarget2.selectedItem as Player)
            targets.add(spinnerTarget3.selectedItem as Player)
            Game.getInstance().makeShoot(targets)
        }
    }

    fun getPlayerList()  : MutableList<Player> {
       return Game.getInstance().getAllPlayers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}