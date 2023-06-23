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
            } else {
                tvPlayerRole1.text = "Мертв"
                binding.spinnerTarget1.visibility = View.GONE
                binding.spinnerTarget1.isEnabled = false
            }

            val tvPlayerRole2 = binding.tvPlayerRole2
            if (blackPlayers.size > 1 && blackPlayers[1].isAlive) {
                tvPlayerRole2.text = blackPlayers[1].name
            } else {
                tvPlayerRole2.text = "Мертв"
                binding.spinnerTarget2.visibility = View.GONE
                binding.spinnerTarget2.isEnabled = false
            }

            val tvPlayerRole3 = binding.tvPlayerRole3
            if (blackPlayers.size > 2 && blackPlayers[2].isAlive) {
                tvPlayerRole3.text = blackPlayers[2].name
            } else {
                tvPlayerRole3.text = "Мертв"
                binding.spinnerTarget3.visibility = View.GONE
                binding.spinnerTarget3.isEnabled = false
            }
        }

        val spinnerTarget1 = binding.spinnerTarget1
        val spinnerTarget2 = binding.spinnerTarget2
        val spinnerTarget3 = binding.spinnerTarget3

        // Remove dead black players from the player list
        val aliveBlackPlayers = blackPlayers.filter { it.isAlive }

        // Populate spinners with player names
        val playerNames = aliveBlackPlayers.map { it.name }
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, playerNames)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinnerTarget1.adapter = adapter
        spinnerTarget2.adapter = adapter
        spinnerTarget3.adapter = adapter

        binding.buttonShoot.setOnClickListener {
            val targets: MutableList<Player> = mutableListOf()

            val selectedTarget1 = spinnerTarget1.selectedItem as? Player
            val selectedTarget2 = spinnerTarget2.selectedItem as? Player
            val selectedTarget3 = spinnerTarget3.selectedItem as? Player

            if (selectedTarget1 != null) {
                targets.add(selectedTarget1)
            }

            if (selectedTarget2 != null) {
                targets.add(selectedTarget2)
            }

            if (selectedTarget3 != null) {
                targets.add(selectedTarget3)
            }

            if (targets.size >= 1) {
                Game.getInstance().makeShoot(targets)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
