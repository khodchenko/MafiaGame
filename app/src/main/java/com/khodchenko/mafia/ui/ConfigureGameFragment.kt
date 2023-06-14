package com.khodchenko.mafia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.khodchenko.mafia.R
import com.khodchenko.mafia.data.Game
import com.khodchenko.mafia.databinding.FragmentConfigureGameBinding
import com.khodchenko.mafia.databinding.ItemPlayerBinding


class ConfigureGameFragment : Fragment() {
    private var _binding: FragmentConfigureGameBinding? = null
    private val binding get() = _binding!!

    private val nameOfPlayers: ArrayList<String> = arrayListOf()
    private var numberOfPlayers: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfigureGameBinding.inflate(inflater, container, false)
        val root = binding.root

        setupViews()
        setupListeners()

        return root
    }

    private fun setupViews() {
        binding.layoutPlayerList.removeAllViews()
    }

    private fun setupListeners() {
        binding.btnAddPlayer.setOnClickListener {
            addPlayer()
        }

        binding.btnRemovePlayer.setOnClickListener {
            removePlayer()
        }

        binding.buttonStart.setOnClickListener {
            if (nameOfPlayers.size > 0) {

                Game.getInstance().addPlayers(playerNames = nameOfPlayers)
                findNavController().navigate(R.id.action_configureGameFragment_to_nav_home)
            }

        }
    }

    private fun addPlayer() {
        val playerName = binding.etPlayerName.text.toString().trim()
        if (playerName.isNotEmpty()) {
            if (numberOfPlayers < 10) {
                nameOfPlayers.add(playerName)
                numberOfPlayers++
                createPlayerItem(playerName)
                binding.etPlayerName.text.clear()
            } else {
                Toast.makeText(context, "Maximum number of players reached", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun removePlayer() {
        if (numberOfPlayers > 0) {
            binding.layoutPlayerList.removeViewAt(numberOfPlayers - 1)
            nameOfPlayers.removeAt(numberOfPlayers - 1)
            numberOfPlayers--
        }
    }

    private fun createPlayerItem(playerName: String) {
        val playerBinding = ItemPlayerBinding.inflate(layoutInflater)
        playerBinding.tvPlayerName.text = "$numberOfPlayers. $playerName"
        val itemPlayerView = playerBinding.root
        binding.layoutPlayerList.addView(itemPlayerView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}