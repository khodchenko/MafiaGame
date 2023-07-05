package com.khodchenko.mafia.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.khodchenko.mafia.R
import com.khodchenko.mafia.data.Player
import com.khodchenko.mafia.databinding.ItemPlayerBinding

class PlayerAdapter(
    private var players: MutableList<Player>,
    private val playerClickListener: PlayerClickListener
) : RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {

    private var isRoleVisible: Boolean = true
    private val selectedPlayers: MutableSet<Player> = mutableSetOf()
    private val roleSmile: Map<Player.Role, String> = hashMapOf(
        Player.Role.CIVIL to "\uD83D\uDE42",
        Player.Role.MAFIA to "\uD83D\uDD2B",
        Player.Role.DON to "\uD83C\uDFA9",
        Player.Role.SHERIFF to "\uD83E\uDD20"
    )
    interface PlayerClickListener {
        fun onPlayerClick(player: Player)
        fun isPlayerSelected(player: Player): Boolean
    }
    inner class ViewHolder(private val binding: ItemPlayerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(player: Player) {

            binding.tvPlayerNumber.text = player.number.toString()
            binding.tvPlayerName.text = player.name
            binding.tvRole.text = roleSmile[player.role]
            binding.tvPenalty.text = player.penalty.toString()
            if (player.isOnVote) {
                binding.tvIsVoted.visibility = View.VISIBLE
            } else {
                binding.tvIsVoted.visibility = View.INVISIBLE
            }
            if (playerClickListener.isPlayerSelected(player)) {
                binding.root.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.orange))
            } else {
                binding.root.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white))
            }
            if (isRoleVisible) {
                binding.tvRole.visibility = View.VISIBLE
            } else {
                binding.tvRole.visibility = View.INVISIBLE
            }
            itemView.setOnClickListener {
                playerClickListener.onPlayerClick(player)
            }
        }

    }
    fun updatePlayers(players: MutableList<Player>) {
        this.players = players
        notifyDataSetChanged()
    }
    fun toggleRoleVisibility() {
        isRoleVisible = !isRoleVisible
        notifyDataSetChanged()
    }

    fun addSelectedPlayer(player: Player) {
        selectedPlayers.add(player)
    }

    fun removeSelectedPlayer(player: Player) {
        selectedPlayers.remove(player)
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

    fun isSelected(player: Player): Boolean {
        return selectedPlayers.contains(player)
    }
}
