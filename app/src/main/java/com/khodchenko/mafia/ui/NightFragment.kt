package com.khodchenko.mafia.ui

import android.R
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
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

        val blackPlayers = Game.getInstance().getAllAliveBlackPlayers()

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
        var spinnners = mutableListOf<Spinner>(
            binding.spinnerTarget1,
            binding.spinnerTarget2,
            binding.spinnerTarget3
        )

        val spinnerTarget1 = binding.spinnerTarget1
        val spinnerTarget2 = binding.spinnerTarget2
        val spinnerTarget3 = binding.spinnerTarget3

        // Populate spinners with player names
        val players = Game.getInstance().getAllPlayers()
        val playerList = players.map { player ->
            "${player.number}: ${player.name}"
            player
        }
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            playerList.map { "${it.number}: ${it.name}" })
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinnerTarget1.adapter = adapter
        spinnerTarget2.adapter = adapter
        spinnerTarget3.adapter = adapter

        binding.buttonShoot.setOnClickListener {
            var targets: MutableList<Player> = mutableListOf()

            for (spinner in spinnners) {
                if (spinner.visibility == View.VISIBLE) {
                    targets.add(spinner.selectedItemPosition.takeIf { it != AdapterView.INVALID_POSITION }
                        ?.let { playerList[it] }!!)
                }
            }

            if (targets.isNotEmpty()) {
                Game.getInstance().makeShoot(targets)
                targets = mutableListOf()
                Toast.makeText(requireContext(), "Выстрел совершен", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "onViewCreated: ${targets.map { it.name }}")
            } else {
                Toast.makeText(requireContext(), "Выстрел не совершен", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "onViewCreated: ${targets.map { it.name }}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
