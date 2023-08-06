package com.khodchenko.mafia.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.khodchenko.mafia.R
import com.khodchenko.mafia.data.Game
import com.khodchenko.mafia.databinding.FragmentWinBinding


class WinFragment : Fragment() {
    private var winner: String? = null

    private var _binding: FragmentWinBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWinBinding.inflate(inflater, container, false)

        if (Game.getInstance().getAllAliveBlackPlayers().size == 0){
            winner = "красных"
        } else winner = "черных"

        binding.tvWinner.text = "Победила команда ${winner}"

        arguments?.let {
            winner = it.getString("parameter_key")
        }

        return _binding?.root ?: inflater.inflate(R.layout.fragment_win, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}