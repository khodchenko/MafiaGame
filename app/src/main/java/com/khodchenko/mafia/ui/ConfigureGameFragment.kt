package com.khodchenko.mafia.ui

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.khodchenko.mafia.R
import com.khodchenko.mafia.data.Game
import com.khodchenko.mafia.data.Player
import com.khodchenko.mafia.databinding.FragmentConfigureGameBinding
import com.khodchenko.mafia.databinding.ItemPlayerBinding

class ConfigureGameFragment : Fragment() {
    private var _binding: FragmentConfigureGameBinding? = null
    private val binding get() = _binding!!

    private val nameOfPlayers: ArrayList<String> = arrayListOf()
    private val playerRoles: MutableList<Player.Role> = mutableListOf()
    private var numberOfPlayers: Int = 0

    private val roleSmile: Map<Player.Role, String> = hashMapOf(
        Player.Role.CIVIL to "\uD83D\uDE42",
        Player.Role.MAFIA to "\uD83D\uDD2B",
        Player.Role.DON to "\uD83C\uDFA9",
        Player.Role.SHERIFF to "\uD83E\uDD20"
    )

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
        // Добавляем комментарий для обозначения автоматического заполнения списка
        // TODO: Автоматическое заполнение списка игроков (для тестов)

        val randomNames = listOf(
            "John",
            "Emma",
            "Liam",
            "Olivia",
            "Noah",
            "Ava",
            "William",
            "Sophia",
            "James",
            "Isabella"
        )
        val maxPlayers = 10

        for (i in 0 until maxPlayers) {
            val playerName = randomNames.random()
            nameOfPlayers.add(playerName)
            numberOfPlayers++

            val playerBinding = ItemPlayerBinding.inflate(layoutInflater)
            playerBinding.tvPlayerName.text = playerName
            playerBinding.tvPlayerNumber.text = (i + 1).toString()
            val itemPlayerView = playerBinding.root
            binding.layoutPlayerList.addView(itemPlayerView)

            val spinner = playerBinding.spinnerOptions
            setupRoleSpinner(spinner)
        }
    }

    private fun setupListeners() {
        binding.btnAddPlayer.setOnClickListener {
            addPlayer()
        }

        binding.btnRemovePlayer.setOnClickListener {
            removePlayer()
        }

        binding.buttonStart.setOnClickListener {
            if (isPlayersEnough()) {
                Game.getInstance()
                    .addPlayers(playerNames = nameOfPlayers, playerRoles = playerRoles)
                findNavController().navigate(R.id.action_configureGameFragment_to_nav_home)
            } else {
                Toast.makeText(context, "Not enough players", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "setupListeners: ${Game.getInstance().getAllPlayers().toString()}")
            }
        }
    }

    private fun isPlayersEnough(): Boolean {
        return when (nameOfPlayers.size) {
            10 -> playerRoles.contains(Player.Role.MAFIA) && playerRoles.count { it == Player.Role.MAFIA } >= 2 &&
                    playerRoles.contains(Player.Role.DON) &&
                    playerRoles.contains(Player.Role.SHERIFF) &&
                    playerRoles.count { it == Player.Role.CIVIL } >= 6
            9 -> playerRoles.contains(Player.Role.MAFIA) && playerRoles.count { it == Player.Role.MAFIA } >= 2 &&
                    playerRoles.contains(Player.Role.DON) &&
                    playerRoles.contains(Player.Role.SHERIFF) &&
                    playerRoles.count { it == Player.Role.CIVIL } >= 5
            8 -> playerRoles.contains(Player.Role.MAFIA) && playerRoles.count { it == Player.Role.MAFIA } >= 1 &&
                    playerRoles.contains(Player.Role.DON) &&
                    playerRoles.contains(Player.Role.SHERIFF) &&
                    playerRoles.count { it == Player.Role.CIVIL } >= 4
            7 -> playerRoles.contains(Player.Role.MAFIA) && playerRoles.count { it == Player.Role.MAFIA } >= 1 &&
                    playerRoles.contains(Player.Role.DON) &&
                    playerRoles.count { it == Player.Role.CIVIL } >= 3
            else -> false
        }
    }

    private fun addPlayer() {
        val playerName = binding.setPlayerName.text.toString().trim()
        if (playerName.isNotEmpty()) {
            if (numberOfPlayers < 10) {
                nameOfPlayers.add(playerName)
                numberOfPlayers++

                val playerBinding = ItemPlayerBinding.inflate(layoutInflater)
                playerBinding.tvPlayerName.text = playerName
                playerBinding.tvPlayerNumber.text = numberOfPlayers.toString()
                val itemPlayerView = playerBinding.root
                binding.layoutPlayerList.addView(itemPlayerView)

                val spinner = playerBinding.spinnerOptions
                setupRoleSpinner(spinner)

                binding.setPlayerName.text.clear()
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

    private fun setupRoleSpinner(spinner: AdapterView<*>) {
        val roles = arrayOf(
            Player.Role.CIVIL,
            Player.Role.MAFIA,
            Player.Role.DON,
            Player.Role.SHERIFF
        )

        val adapter = RoleSpinnerAdapter(requireContext(), roles)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedRole = roles[position]
                playerRoles.add(position, selectedRole)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Ничего не делаем
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
