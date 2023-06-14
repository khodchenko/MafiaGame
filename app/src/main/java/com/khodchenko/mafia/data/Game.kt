package com.khodchenko.mafia.data

import android.content.ContentValues.TAG
import android.util.Log

class Game: TimerListener {

    private constructor()
    companion object {
        @Volatile
        private var instance: Game? = null

        // Метод для получения экземпляра класса
        fun getInstance(): Game {
            return instance ?: synchronized(this) {
                instance ?: Game().also { instance = it }
            }
        }
    }

    private var inGame: Boolean = true
    private var playerList: MutableList<Player> = mutableListOf()
    private var day: Int = 0
    private val voteHelper: VoteHelper = VoteHelper(playerList)
    private var currentPlayer: Player? = null
    private var currentStage = Stage.NIGHT

    private enum class Stage {
        NIGHT,
        LAST_WORD,
        DAY,
        VOTING
    }

    fun startGame() {
        while (inGame) {
            nightPhase(alivePlayers = playerList)
            dayPhase(alivePlayers = playerList)
            checkGameEnd(alivePlayers = playerList)
            day++
        }
    }

    private fun nightPhase(alivePlayers: MutableList<Player>) {
        currentStage = Stage.NIGHT
        Log.d(TAG, "$currentStage")
        if (day == 0) {
            mafiaMeeting()
            return
        } else {
            currentStage = Stage.LAST_WORD
            Log.d(TAG, "$currentStage")
        }
    }

    private fun dayPhase(alivePlayers: MutableList<Player>) {
        currentStage = Stage.DAY
        Log.d(TAG, "$currentStage")
        for (player in alivePlayers) {
            startSpeech(player, 6000)
        }
        currentStage = Stage.VOTING
        Log.d(TAG, "$currentStage")
        val voteHelper = VoteHelper(alivePlayers = getAlivePlayers(this.playerList))
        val voteResult = voteHelper.calculateVotes()
        kickPlayers(voteResult)
    }

    private fun checkPlayerRole(player: Player): Player.Role {
        return player.role as Player.Role
    }

    private fun mafiaMeeting() {
        Log.d(TAG, "mafiaMeeting: started")
        val timer: Timer = Timer(6000, this)
        timer.start()
        Log.d(TAG, "mafiaMeeting: finished")
    }

    private fun makeShoot(mafiaPlayers: MutableList<Player>, targets: MutableList<Player>) {
        Log.d(TAG, "makeShoot $mafiaPlayers: $targets")
    }


    private fun kickPlayers(voteResult: MutableMap<Player, MutableList<Player>>) {
        for (player in voteResult.keys) {
            player.isAlive = false
            Log.d(TAG, "kickPlayers: player been kicked: $player")
        }

    }

    private fun startSpeech(player: Player, duration: Long) {
        currentPlayer = player
        val timer: Timer = Timer(duration, this)
        timer.start()
        Log.d(TAG, "startSpeech: $player")
    }

    override fun onTimerFinished() {
        Log.d(TAG, "onTimerFinished: $currentPlayer finished!")

    }

    private fun checkGameEnd(alivePlayers: MutableList<Player>) {
        val redPlayers = getAllRedPlayers(alivePlayers)
        val blackPlayers = getAllBlackPlayers(alivePlayers)

        if (blackPlayers.size == redPlayers.size) {
            inGame = false
        }
        if (blackPlayers.size == 0) {
            inGame = false
        }
    }

    fun addPlayers(playerNames: ArrayList<String>) {
        for (player in playerNames) {
            playerList.add(Player(player, playerList.lastIndex))
        }
    }

    private fun shufflePlayerRoles(
        playerList: List<Player>
    ): MutableList<Player> {
        val roles = mutableListOf<Player.Role>().apply {
            repeat(6) { add(Player.Role.CIVIL) }
            add(Player.Role.SHERIFF)
            repeat(2) { add(Player.Role.MAFIA) }
            add(Player.Role.DON)
        }

        val shuffledPlayers: MutableList<Player> = playerList.toMutableList().apply {
            forEachIndexed { index, player ->
                player.role = roles[index]
            }
        }
        return shuffledPlayers
    }

    private fun giveRoleToPlayer(player: Player, role: Player.Role): Player {
        player.role = role
        return player
    }

    private fun clearPlayers() {
        playerList = mutableListOf()
    }

    fun getAllPlayers(): MutableList<Player> {
        return playerList
    }

    private fun getAllRedPlayers(alivePlayers: MutableList<Player>): MutableList<Player> {
        return alivePlayers.filter {
            it.role in listOf(
                Player.Role.CIVIL,
                Player.Role.SHERIFF
            )
        } as MutableList<Player>
    }

    private fun getAllBlackPlayers(alivePlayers: MutableList<Player>): MutableList<Player> {
        return alivePlayers.filter {
            it.role in listOf(
                Player.Role.MAFIA,
                Player.Role.DON
            )
        } as MutableList<Player>
    }

    private fun getDonOrNull(allPlayers: MutableList<Player>): Player? {
        return allPlayers.find { it.role == Player.Role.DON && it.isAlive }
    }

    private fun getSheriffOrNull(allPlayers: MutableList<Player>): Player? {
        return allPlayers.find { it.role == Player.Role.SHERIFF && it.isAlive }
    }

    private fun getAlivePlayers(playerList: MutableList<Player>): MutableList<Player> {
        return playerList.filter { it.isAlive } as MutableList<Player>
    }

    private fun getDeadPlayers(playerList: MutableList<Player>): MutableList<Player> {
        return playerList.filter { !it.isAlive } as MutableList<Player>
    }


}