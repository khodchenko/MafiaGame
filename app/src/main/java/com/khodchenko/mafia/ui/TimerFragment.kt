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
import com.khodchenko.mafia.ui.home.HomeFragment

private const val TOTAL_TIME = 60000L
private const val HALF_TIME = 60000L
private const val INTERVAL = 1000L

class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

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
        binding.tvCurrentPlayer.text = Game.getInstance().getCurrentPlayer().name
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
                    playSound(R.raw.timer_sound_10sec_r)
                } else if (secondsRemaining == 5L) {

                }
            }

            override fun onFinish() {
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
        var lastWordFromVoting = false

        when (game.getCurrentStage()) {
            Game.Stage.NIGHT -> {
                if (game.getKickedPlayers()?.isNotEmpty() == true) {
                    game.setCurrentStage(Game.Stage.LAST_WORD)
                } else {
                    game.setCurrentStage(Game.Stage.DAY)
                }
            }

            Game.Stage.DAY -> {
                val currentPlayer = game.getCurrentPlayer()
                val lastPlayerOfQueueList = game.getSpeechPlayerOrder().last()

                if (currentPlayer != lastPlayerOfQueueList) {
                    game.setCurrentPlayer(game.nextPlayerSpeech())
                    notifyPlayerChange(game.getCurrentPlayer())
                } else {
                    game.setCurrentStage(Game.Stage.VOTING)
                }
            }

            Game.Stage.VOTING -> {
                val voteHelper: VoteHelper = VoteHelper()
                if (voteHelper.candidates.isNotEmpty()) {
                    lastWordFromVoting = true
                    game.setCurrentStage(Game.Stage.LAST_WORD)
                } else {
                    game.setCurrentStage(Game.Stage.NIGHT)
                }
            }

            Game.Stage.LAST_WORD -> {
                if (lastWordFromVoting) {
                    game.setCurrentStage(Game.Stage.NIGHT)
                    lastWordFromVoting = false
                    game.nextDay()
                    game.setSpeechPlayerOrder()
                } else {
                    game.setCurrentStage(Game.Stage.DAY)
                }
            }
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

    companion object {

    }
}
