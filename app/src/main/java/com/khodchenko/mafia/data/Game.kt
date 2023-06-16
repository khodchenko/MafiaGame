package com.khodchenko.mafia.data

import android.content.ContentValues.TAG
import android.util.Log

class Game : TimerListener {

    private constructor()

    companion object {
        @Volatile
        private var instance: Game? = null

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
    private lateinit var currentPlayer: Player
    private var currentStage = Stage.NIGHT
    private var speechPlayerOrder: MutableList<Player> = mutableListOf()

    enum class Stage {
        NIGHT,
        LAST_WORD,
        DAY,
        VOTING
    }

    fun startGame() {
        currentPlayer = playerList[0]
        speechPlayerOrder = getAlivePlayers(getAllPlayers())
    }

    fun nextPlayerSpeech(): Player {
        return speechPlayerOrder[(speechPlayerOrder.indexOf(currentPlayer) + 1)]
    }

    fun setSpeechPlayerOrder() {
        val previousFirstPlayer = speechPlayerOrder[0]
        speechPlayerOrder.removeAt(0)
        speechPlayerOrder.add(previousFirstPlayer)
        currentPlayer = speechPlayerOrder[0]
    }

    fun getSpeechPlayerOrder(): MutableList<Player> {
        return speechPlayerOrder
    }

    private fun checkPlayerRole(player: Player): Player.Role {
        return player.role as Player.Role
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

    fun getCurrentPlayer(): Player {
        return currentPlayer
    }

    fun getAllRedPlayers(alivePlayers: MutableList<Player>): MutableList<Player> {
        return alivePlayers.filter {
            it.role in listOf(
                Player.Role.CIVIL,
                Player.Role.SHERIFF
            )
        } as MutableList<Player>
    }

    fun getAllBlackPlayers(alivePlayers: MutableList<Player>): MutableList<Player> {
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

    fun getAlivePlayers(playerList: MutableList<Player>): MutableList<Player> {
        return playerList.filter { it.isAlive } as MutableList<Player>
    }

    fun getDeadPlayers(playerList: MutableList<Player>): MutableList<Player> {
        return playerList.filter { !it.isAlive } as MutableList<Player>
    }

    fun getCurrentStage(): Stage {
        return currentStage
    }

    fun setCurrentStage(stage: Stage) {
        currentStage = stage
    }

    fun setCurrentPlayer(toPlayer: Player) {
        currentPlayer = toPlayer
    }

    fun getCurrentDay(): Int {
        return day
    }

    fun nextDay() {
        day++
    }
}