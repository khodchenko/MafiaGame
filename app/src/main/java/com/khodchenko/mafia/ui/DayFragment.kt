package com.khodchenko.mafia.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.khodchenko.mafia.data.Game
import com.khodchenko.mafia.data.Player
import com.khodchenko.mafia.databinding.FragmentPlayerListBinding
import com.khodchenko.mafia.databinding.ItemPlayerBinding

class DayFragment : Fragment() {

    interface OnPlayerChangeListener {
        fun onPlayerChanged(player: Player)
    }

    private var playerChangeListener: OnPlayerChangeListener? = null
    private var _binding: FragmentPlayerListBinding? = null
    private val binding get() = _binding!!
    private lateinit var playerAdapter: PlayerAdapter
    private var playerList: MutableList<Player> = mutableListOf()
    private var currentPlayer: Player? = null

    val roleSmile: Map<Player.Role, String> = hashMapOf(
        Player.Role.CIVIL to "\uD83D\uDE42",
        Player.Role.MAFIA to "\uD83D\uDD2B",
        Player.Role.DON to "\uD83C\uDFA9",
        Player.Role.SHERIFF to "\uD83E\uDD20"
    )

    fun setOnPlayerChangeListener(listener: OnPlayerChangeListener) {
        playerChangeListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()

        return root
    }

    fun onPlayerChanged(player: Player) {
        currentPlayer = player
      //  playerAdapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        playerList = Game.getInstance().getAllPlayers()
        playerAdapter = PlayerAdapter(playerList)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = playerAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class PlayerAdapter(private val players: List<Player>) :
        RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {

        inner class ViewHolder(private val binding: ItemPlayerBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(player: Player) {
                binding.tvPlayerNumber.text = player.number.toString()
                binding.tvPlayerName.text = player.name
                binding.tvRole.text = roleSmile[player.role]
                binding.tvPenalty.text = player.penalty.toString()

                itemView.setOnClickListener {
                    playerChangeListener?.onPlayerChanged(player)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemPlayerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val player = players[position]
            holder.bind(player)
        }

        override fun getItemCount(): Int {
            return players.size
        }
    }
}