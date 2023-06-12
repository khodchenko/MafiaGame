package com.khodchenko.mafia.data

class Game {
    private  var playerList : MutableList<Player> = mutableListOf()
    private var day : Int = 0
    private val voteHelper : VoteHelper = VoteHelper(playerList)

    fun getAlivePlayers(playerList: MutableList<Player>) : MutableList<Player> {
        return playerList.filter { it.isAlive } as MutableList<Player>
    }

    fun getDeadPlayers(playerList: MutableList<Player>) : MutableList<Player> {
        return playerList.filter { !it.isAlive } as MutableList<Player>
    }
}