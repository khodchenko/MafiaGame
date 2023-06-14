package com.khodchenko.mafia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.khodchenko.mafia.data.Player
import com.khodchenko.mafia.databinding.FragmentPlayerListBinding
import com.khodchenko.mafia.databinding.ItemPlayerBinding

public class PlayerListFragment : Fragment() {
    private var _binding: FragmentPlayerListBinding? = null
    private val binding get() = _binding!!

    private val playerList = generateDummyPlayerList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupPlayerList()

        return root
    }

    private fun setupPlayerList() {
        val layoutInflater = LayoutInflater.from(requireContext())
        val parentLayout = binding.layoutPlayerList

        for (player in playerList) {
            val itemBinding = ItemPlayerBinding.inflate(layoutInflater, parentLayout, false)
            itemBinding.tvPlayerName.text = player.name
            itemBinding.tvPenalty.text = player.penalty.toString()
            itemBinding.tvIsVoted.text =  "Not Voted"

            itemBinding.root.setOnLongClickListener {
                // Действия при длительном нажатии на элемент списка
                true
            }

            parentLayout.addView(itemBinding.root)
        }
    }


    private fun generateDummyPlayerList(): List<Player> {
        val playerList = mutableListOf<Player>()
        for (i in 1..10) {
            val player = Player("Player $i", i, Player.Role.CIVIL)
            playerList.add(player)
        }
        return playerList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


