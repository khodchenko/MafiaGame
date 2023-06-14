package com.khodchenko.mafia.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.khodchenko.mafia.data.Game
import com.khodchenko.mafia.data.Player
import com.khodchenko.mafia.data.VoteHelper
import com.khodchenko.mafia.databinding.FragmentPlayerListBinding
import com.khodchenko.mafia.databinding.ItemPlayerBinding

public class PlayerListFragment : Fragment() {

    private var _binding: FragmentPlayerListBinding? = null
    private val binding get() = _binding!!
    private var playerList : MutableList<Player> = mutableListOf()


    val roleSmile: Map<Player.Role, String> = hashMapOf(
        Player.Role.CIVIL to "\uD83D\uDE42",
        Player.Role.MAFIA to "\uD83D\uDD2B",
        Player.Role.DON to "\uD83C\uDFA9",
        Player.Role.SHERIFF to "\uD83E\uDD20"
    )
    @SuppressLint("SuspiciousIndentation")
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
        playerList = Game.getInstance().getAllPlayers()
        for (player in playerList) {
            val itemBinding = ItemPlayerBinding.inflate(layoutInflater, parentLayout, false)
            itemBinding.tvPlayerNumber.text = player.number.toString()
            itemBinding.tvPlayerName.text = player.name
            itemBinding.tvRole.text = roleSmile.get(player.role)
            itemBinding.tvPenalty.text = player.penalty.toString()


            itemBinding.root.setOnLongClickListener {

                true
            }

            parentLayout.addView(itemBinding.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


