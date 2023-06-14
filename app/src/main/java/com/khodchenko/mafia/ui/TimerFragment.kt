import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.khodchenko.mafia.R
import com.khodchenko.mafia.databinding.FragmentTimerBinding

private const val TOTAL_TIME = 60000L
private const val INTERVAL = 1000L
class TimerFragment : Fragment() {
    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    private lateinit var countDownTimer: CountDownTimer
    private var isTimerRunning = false
    private var remainingTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        val root: View = binding.root

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

        return root
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(remainingTime, INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                updateTimerText()
                updateProgressBar()
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
      Snackbar.make(requireContext(),requireView(), "Время вышло!", Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

    }
}
