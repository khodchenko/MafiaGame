import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.khodchenko.mafia.databinding.FragmentVotingBinding

class VotingFragment : Fragment() {
    private var _binding: FragmentVotingBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVotingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}