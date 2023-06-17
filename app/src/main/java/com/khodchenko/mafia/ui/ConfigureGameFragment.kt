package com.khodchenko.mafia.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
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
        val playerName = binding.setPlayerName.text.toString().trim()
        if (playerName.isNotEmpty()) {
            if (numberOfPlayers < 10) {
                nameOfPlayers.add(playerName)
                numberOfPlayers++
                createPlayerItem(playerName)
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

    private fun createPlayerItem(playerName: String) {
        val playerBinding = ItemPlayerBinding.inflate(layoutInflater)
        playerBinding.tvPlayerName.text = playerName

        val itemPlayerView = playerBinding.root
        playerBinding.cardView.setPadding(0, 8, 0, 8)
        binding.layoutPlayerList.addView(itemPlayerView)

        val spinnerOptions = playerBinding.spinnerOptions
        val roles = listOf(Player.Role.CIVIL, Player.Role.MAFIA, Player.Role.DON, Player.Role.SHERIFF)
        val adapter = RoleSpinnerAdapter(requireContext(), roles)
        spinnerOptions.adapter = adapter

        spinnerOptions.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedRole = adapter.getItem(position)
                Toast.makeText(context, "Выбрана роль: $selectedRole", Toast.LENGTH_SHORT).show()
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