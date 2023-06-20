package com.khodchenko.mafia.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.khodchenko.mafia.data.Player
import com.khodchenko.mafia.databinding.ItemPlayerBinding

class PlayerAdapter(
    private val players: List<Player>,
    private val playerClickListener: (Player) -> Unit
) : RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {

    private val roleSmile: Map<Player.Role, String> = hashMapOf(
        Player.Role.CIVIL to "\uD83D\uDE42",
        Player.Role.MAFIA to "\uD83D\uDD2B",
        Player.Role.DON to "\uD83C\uDFA9",
        Player.Role.SHERIFF to "\uD83E\uDD20"
    )

    inner class ViewHolder(private val binding: ItemPlayerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(player: Player) {
            binding.tvPlayerNumber.text = player.number.toString()
            binding.tvPlayerName.text = player.name
            binding.tvRole.text = roleSmile[player.role]
            binding.tvPenalty.text = player.penalty.toString()
            if (player.isOnVote){
                binding.tvIsVoted.visibility = View.VISIBLE
            } else {
                binding.tvIsVoted.visibility = View.INVISIBLE
            }
            itemView.setOnClickListener {
                playerClickListener(player)
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
