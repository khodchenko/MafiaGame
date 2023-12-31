package com.khodchenko.mafia.ui

import android.app.AlertDialog
import android.content.Context
import android.media.AudioDeviceCallback
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.khodchenko.mafia.R
import com.khodchenko.mafia.data.Game
import com.khodchenko.mafia.data.Player
import com.khodchenko.mafia.data.VoteHelper
import com.khodchenko.mafia.databinding.FragmentTimerBinding
import com.khodchenko.mafia.ui.dialog.InputDialogFragment
import com.khodchenko.mafia.ui.home.HomeFragment

private const val TOTAL_TIME = 60000L
private const val HALF_TIME = 30000L
private const val INTERVAL = 1000L

class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
    private var isInputDialogShown = false
    private lateinit var countDownTimer: CountDownTimer
    private var isTimerRunning = false
    private var remainingTime: Long = 0
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        updateHeaderText()

        binding.imNext.setOnClickListener {
            nextButtonClick()
        }

        binding.ivButtonShowRoles.setOnClickListener {

        }

        with(binding) {
            progressBar.max = TOTAL_TIME.toInt()
            updateTimerText()
            imPlay.setOnClickListener {
                if (!isTimerRunning) {
                    if (remainingTime == 0L) {
                        remainingTime = TOTAL_TIME
                        updateTimerText()
                        progressBar.progress = 0
                    }
                    startTimer()
                } else {
                    pauseTimer()
                }
                togglePlayPauseButtons()
            }

            imStop.setOnClickListener {
                stopTimer()
                togglePlayPauseButtons()
            }
        }
        initMediaPlayer()
        return root
    }

    private fun notifyPlayerChange(player: Player) {
        val homeFragment = requireParentFragment() as? HomeFragment
        homeFragment?.onPlayerChanged(player)
    }

    private fun updateHeaderText() {
        binding.tvCurrentPlayer.text = when (Game.getInstance().getCurrentStage()) {
            Game.Stage.DAY -> {
                val currentPlayer = Game.getInstance().getCurrentPlayer()
                "${currentPlayer.number}: ${currentPlayer.name}"
            }

            Game.Stage.VOTING -> {
                val candidates = VoteHelper.getInstance().candidates
                val currentCandidateIndex = VoteHelper.getInstance().currentCandidateIndex
                val currentCandidate = candidates.keys.toList()[currentCandidateIndex]
                "${currentCandidate.number}: ${currentCandidate.name}"
            }

            else -> ""
        }

        binding.tvSpeechHeader.text =
            "${Game.getInstance().getCurrentStage()} № ${Game.getInstance().getCurrentDay()}"
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(remainingTime, INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                updateTimerText()
                updateProgressBar()
                remainingTime = millisUntilFinished
                updateTimerText()
                updateProgressBar()

                val secondsRemaining = millisUntilFinished / 1000
                if (secondsRemaining == 10L) {
                    //todo play sound
                    //playSound(R.raw.timer_sound_10sec_r)
                } else if (secondsRemaining == 5L) {
                    //todo play sound
                    //playSound(R.raw.timer_sound_10sec_r)
                }
            }

            override fun onFinish() {
                playSound(R.raw.timer_sound_0sec)
                onTimerFinished()
                stopTimer()
                togglePlayPauseButtons()
            }
        }

        countDownTimer.start()
        isTimerRunning = true
    }

    private fun pauseTimer() {
        countDownTimer.cancel()
        isTimerRunning = false
    }

    private fun stopTimer() {
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
        remainingTime = 0
        updateTimerText()
        binding.progressBar.progress = 0
        isTimerRunning = false
    }

    private fun updateTimerText() {
        val minutes = remainingTime / 1000 / 60
        val seconds = (remainingTime / 1000) % 60
        val timeText = String.format("%02d:%02d", minutes, seconds)
        binding.tvTimer.text = timeText
    }


    private fun updateProgressBar() {
        val progress = (TOTAL_TIME - remainingTime).toInt()
        binding.progressBar.progress = progress
    }

    private fun togglePlayPauseButtons() {
        with(binding) {
            if (isTimerRunning) {
                imPlay.setImageResource(R.drawable.ic_pause)
            } else {
                imPlay.setImageResource(R.drawable.ic_play)
            }
        }
    }

    private fun onTimerFinished() {
        Snackbar.make(requireContext(), requireView(), "Время вышло!", Snackbar.LENGTH_SHORT).show()
    }

    private fun nextButtonClick() {
        val game = Game.getInstance()
        val voteHelper = VoteHelper.getInstance()

        if (voteHelper.voteStage == 2) {
            val dialog = AlertDialog.Builder(requireContext()).apply {
                setTitle("Выгнать всех кандидатов?")
                setPositiveButton("Выгнать") { dialog, which ->
                    //todo выгнать всех кандидатов в списке
                    dialog.cancel()
                }
                setNegativeButton("Оставить") { dialog, which ->
                    //todo это тут не должно быть
                    game.setCurrentStage(Game.Stage.NIGHT)
                    voteHelper.clearCandidates()
                    game.nextDay()
                    game.setSpeechPlayerOrder()
                    dialog.cancel()
                }
            }
            dialog.show()
            //BEST MOVE
        } else if (game.getCurrentDay() == 1 && game.getAlivePlayers().size == 9 && !isInputDialogShown) {
            val currentPlayerNumber = game.getAllPlayers().find { !it.isAlive }?.number
            val inputDialogFragment =
                currentPlayerNumber?.let { InputDialogFragment.newInstance(it) }
            inputDialogFragment?.show(parentFragmentManager, "input_dialog_fragment")
            isInputDialogShown = true
        } else {
            game.processNextButtonClick()
        }
        updateHeaderText()
        updateTimerText()
        togglePlayPauseButtons()
    }

    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.timer_sound_10sec_r)
    }

    private fun playSound(soundResId: Int) {
        mediaPlayer?.apply {
            stop()
            reset()
            setDataSource(
                requireContext(),
                Uri.parse("android.resource://${requireContext().packageName}/$soundResId")
            )
            prepare()
            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
