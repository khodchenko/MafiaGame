package com.khodchenko.mafia.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.khodchenko.mafia.data.Game
import com.khodchenko.mafia.databinding.FragmentLastWordBinding


class LastWordFragment : Fragment() {
    //просто удаляй последний идекс из списка kickedPlayers в Game а в TimerFragment проверяй что если в списке есть элементы то текущая фаза LastWord

    private var _binding: FragmentLastWordBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLastWordBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.tvCurrentStage.text = "Прощальная речь игрока:"
        binding.tvPlayerName.text = Game.getInstance().getKickedPlayers().last().name

        //todo добавить список игроков для выбора лучшего хода

        return root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}