package com.khodchenko.mafia.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.khodchenko.mafia.data.Game
import com.khodchenko.mafia.data.Player
import com.khodchenko.mafia.data.Scores
import com.khodchenko.mafia.databinding.DialogInputBinding

class InputDialogFragment : DialogFragment() {
    companion object {
        private const val ARG_NUMBER = "arg_number"

        fun newInstance(number: Int): InputDialogFragment {
            val fragment = InputDialogFragment()
            val args = Bundle()
            args.putInt(ARG_NUMBER, number)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var binding: DialogInputBinding
    private lateinit var killedPlayer: Player
    private val scores: Scores = Scores()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogInputBinding.inflate(LayoutInflater.from(requireContext()))

        val number = arguments?.getInt(ARG_NUMBER, 0)
        killedPlayer = Game.getInstance().getAllPlayers().find { it.number == number }!!

        setupSpinners()

        return buildAlertDialog(number)
    }

    private fun setupSpinners() {
        val digitAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        )
        digitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerDigit1.adapter = digitAdapter
        binding.spinnerDigit2.adapter = digitAdapter
        binding.spinnerDigit3.adapter = digitAdapter
    }

    private fun buildAlertDialog(number: Int?): AlertDialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Лучший ход ${killedPlayer.name}")
            .setView(binding.root)
            .setPositiveButton("Ок") { dialog, _ ->
                val digit1 = binding.spinnerDigit1.selectedItem.toString()
                val digit2 = binding.spinnerDigit2.selectedItem.toString()
                val digit3 = binding.spinnerDigit3.selectedItem.toString()

                val selectedNumbers = listOf(digit1, digit2, digit3)
                val mafiaAndDonCount = getMafiaAndDonCount(selectedNumbers)

                when (mafiaAndDonCount) {
                    1 -> killedPlayer.score += scores.BEST_MOVE_1_OF_3
                    2 -> killedPlayer.score += scores.BEST_MOVE_2_OF_3
                    3 -> killedPlayer.score += scores.BEST_MOVE_3_OF_3
                }

                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    private fun getMafiaAndDonCount(selectedNumbers: List<String>): Int {
        val mafiaAndDonNumbers = mutableListOf<Int>()
        val mafiaAndDonRoles = setOf(Player.Role.MAFIA, Player.Role.DON)
        val allPlayers = Game.getInstance().getAllPlayers()

        for (number in selectedNumbers) {
            val player = allPlayers.find { it.number.toString() == number }
            if (player != null && player.role in mafiaAndDonRoles) {
                mafiaAndDonNumbers.add(player.number)
            }
        }

        return mafiaAndDonNumbers.size
    }
}
