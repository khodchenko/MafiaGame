package com.khodchenko.mafia.ui.home


import com.khodchenko.mafia.ui.VotingFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.khodchenko.mafia.data.Game
import com.khodchenko.mafia.data.Player
import com.khodchenko.mafia.databinding.FragmentHomeBinding
import com.khodchenko.mafia.ui.DayFragment
import com.khodchenko.mafia.ui.LastWordFragment
import com.khodchenko.mafia.ui.NightFragment
import com.khodchenko.mafia.ui.TimerFragment
import com.khodchenko.mafia.ui.WinFragment


class HomeFragment : Fragment(),  DayFragment.OnPlayerChangeListener, Game.GameObserver {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var containerTimerFragment: FrameLayout
    private lateinit var containerPlayerListFragment: FrameLayout
    private val timerFragment = TimerFragment()
    private val dayFragment = DayFragment()
    private lateinit var currentFragment: Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        Game.getInstance().startGame()
        Game.getInstance().addObserver(this)
        containerTimerFragment = binding.containerFragmentTimer
        containerPlayerListFragment = binding.containerFragmentPlayer

        currentFragment = NightFragment()

        dayFragment.setOnPlayerChangeListener(this)


        childFragmentManager.beginTransaction()
            .add(containerTimerFragment.id, timerFragment)
            .add(containerPlayerListFragment.id, currentFragment)
            .commit()

        return root
    }

    override fun onStageChanged(newStage: Game.Stage) {

        when (newStage) {
            Game.Stage.NIGHT -> {
                showNightFragment()
            }
            Game.Stage.LAST_WORD -> {
                showLastWordFragment()
            }
            Game.Stage.DAY -> {
                showDayFragment()
            }
            Game.Stage.VOTING -> {
                showVotingFragment()
            }
            Game.Stage.WIN -> {
                showWinFragment()
            }
        }
    }

    private fun showNightFragment() {
        childFragmentManager.beginTransaction()
            .replace(containerPlayerListFragment.id, NightFragment())
            .commit()
    }

    private fun showLastWordFragment() {
        childFragmentManager.beginTransaction()
            .replace(containerPlayerListFragment.id, LastWordFragment())
            .commit()
    }

    private fun showDayFragment() {
        childFragmentManager.beginTransaction()
            .replace(containerPlayerListFragment.id, DayFragment())
            .commit()
    }

    private fun showVotingFragment() {
        childFragmentManager.beginTransaction()
            .replace(containerPlayerListFragment.id, VotingFragment())
            .commit()
    }

    private fun showWinFragment() {
        childFragmentManager.beginTransaction()
            .replace(containerPlayerListFragment.id, WinFragment())
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Game.getInstance().removeObserver(this)
        _binding = null
    }

    override fun onPlayerChanged(player: Player) {
        dayFragment.onPlayerChanged(player)
    }

}