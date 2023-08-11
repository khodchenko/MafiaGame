package com.khodchenko.mafia.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.khodchenko.mafia.R
import com.khodchenko.mafia.data.Game
import com.khodchenko.mafia.data.Player
import com.khodchenko.mafia.data.Scores

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
    private lateinit var killedPlayer:Player
    private val scores: Scores = Scores()
    private lateinit var spinnerDigit1: Spinner
    private lateinit var spinnerDigit2: Spinner
    private lateinit var spinnerDigit3: Spinner

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = createDialogView()

        val number = arguments?.getInt(ARG_NUMBER, 0)

        // Инициализация спиннеров перед использованием setupSpinners
        spinnerDigit1 = dialogView.findViewById(R.id.spinnerDigit1)
        spinnerDigit2 = dialogView.findViewById(R.id.spinnerDigit2)
        spinnerDigit3 = dialogView.findViewById(R.id.spinnerDigit3)

        killedPlayer = Game.getInstance().getAllPlayers().find { it.number == number }!!

        setupSpinners()

        return buildAlertDialog(dialogView, number)
    }

    private fun createDialogView(): View {
        val inflater = requireActivity().layoutInflater
        return inflater.inflate(R.layout.dialog_input, null)
    }

    private fun setupSpinners() {
        val digitAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        )
        digitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerDigit1.adapter = digitAdapter
        spinnerDigit2.adapter = digitAdapter
        spinnerDigit3.adapter = digitAdapter
    }

    private fun buildAlertDialog(dialogView: View, number: Int?): AlertDialog {
        spinnerDigit1 = dialogView.findViewById(R.id.spinnerDigit1)
        spinnerDigit2 = dialogView.findViewById(R.id.spinnerDigit2)
        spinnerDigit3 = dialogView.findViewById(R.id.spinnerDigit3)

        return AlertDialog.Builder(requireContext())
            .setTitle("Лучший ход ${killedPlayer.name}")
            .setView(dialogView)
            .setPositiveButton("Ок") { dialog, _ ->
                val digit1 = spinnerDigit1.selectedItem.toString()
                val digit2 = spinnerDigit2.selectedItem.toString()
                val digit3 = spinnerDigit3.selectedItem.toString()

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
