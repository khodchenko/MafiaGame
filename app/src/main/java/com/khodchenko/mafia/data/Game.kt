package com.khodchenko.mafia.data

import android.content.ContentValues.TAG
import android.util.Log

class Game {

    private constructor()

    interface GameObserver {
        fun onStageChanged(newStage: Game.Stage)

        fun onPlayerChanged(player: Player)
    }

    companion object {
        @Volatile
        private var instance: Game? = null

        fun getInstance(): Game {
            return instance ?: synchronized(this) {
                instance ?: Game().also { instance = it }
            }
        }
    }

    private val scores = Scores()
    private var inGame: Boolean = true
    private var playerList: MutableList<Player> = mutableListOf()
    private var day: Int = 0
    private lateinit var currentPlayer: Player
    private var currentStage = Stage.NIGHT
    private var speechPlayerOrder: MutableList<Player> = mutableListOf()
    private val observers: MutableList<GameObserver> = mutableListOf()
    private var kickedPlayers: MutableMap<Player, Stage> = mutableMapOf()
    private lateinit var lastWordFrom: Game.Stage

    enum class Stage {
        NIGHT,
        LAST_WORD,
        DAY,
        VOTING,
        WIN
    }

    fun startGame() {
        currentPlayer = playerList[0]
        speechPlayerOrder = getAlivePlayers()
    }

    fun processNextButtonClick() {
        when (getCurrentStage()) {
            Stage.NIGHT -> {
                if (getKickedPlayers().isNotEmpty()) {
                    setCurrentStage(Stage.LAST_WORD)
                    lastWordFrom = Stage.NIGHT
                } else {
                    setCurrentStage(Stage.DAY)
                }
            }

            Stage.DAY -> {
                val currentPlayer = getCurrentPlayer()
                val lastPlayerOfQueueList = getSpeechPlayerOrder().last()

                if (currentPlayer != lastPlayerOfQueueList) {
                    setCurrentPlayer(nextPlayerSpeech())

                    notifyPlayerChanged(getCurrentPlayer())
                } else {
                    if (VoteHelper.getInstance().candidates.isEmpty()) {
                        setCurrentStage(Stage.NIGHT)
                        nextDay()
                        setSpeechPlayerOrder()
                    } else {
                        setCurrentStage(Stage.VOTING)
                    }
                }
            }

            Stage.VOTING -> {
                val voteHelper = VoteHelper.getInstance()

                if (voteHelper.candidates.size == 1) {
                    makeVote(voteHelper.candidates.keys.first()) //put only one to kick list
                    voteHelper.clearCandidates()
                    setCurrentStage(Stage.LAST_WORD)

                    lastWordFrom = Stage.VOTING

                } else if (voteHelper.currentCandidateIndex < voteHelper.candidates.keys.size - 1) {
                    Log.d(TAG, "processNextButtonClick: next candidate")
                    voteHelper.nextCandidate()
                    setCurrentStage(Stage.VOTING)
                } else {
                    Log.d(TAG, "processNextButtonClick: calculate votes")
                    voteHelper.calculateVotes()
                    if (voteHelper.candidates.size == 1) {
                        voteHelper.candidates.keys.first().let {
                            makeVote(it)
                            voteHelper.clearCandidates()
                            setCurrentStage(Stage.LAST_WORD)
                            lastWordFrom = Stage.VOTING
                        }
                    } else {
                        Log.d(TAG, "processNextButtonClick: equal votes")
                        Log.d(
                            TAG,
                            "candidates: ${VoteHelper.getInstance().candidates.entries.joinToString { (key, value) -> "$key=$value" }}"
                        )
                        voteHelper.currentCandidateIndex = 0
                        setCurrentStage(Stage.VOTING)
                    }

                }
            }

            Stage.LAST_WORD -> {
                if (getKickedPlayers().size > 1) {
                    setCurrentStage(Stage.LAST_WORD)
                    kickPlayer()
                } else if (lastWordFrom == Stage.NIGHT) {
                    kickPlayer()
                    setCurrentStage(Stage.DAY)
                    checkGameEnd()
                } else {
                    //from day
                    kickPlayer()
                    setCurrentStage(Stage.NIGHT)
                    nextDay()
                    setSpeechPlayerOrder()
                    checkGameEnd()
                }

            }

            Stage.WIN -> {
                setCurrentStage(Stage.WIN)
            }

        }
        Log.d(TAG, " \n Stage: ${getCurrentStage()}")
        Log.d(TAG, "All players: ${getAllPlayers().joinToString { it.name }}")
        Log.d(TAG, "alive: ${getAlivePlayers().joinToString { it.name }}")
        Log.d(TAG, "dead: ${getDeadPlayers().joinToString { it.name }}")
        Log.d(
            TAG,
            "candidates: ${VoteHelper.getInstance().candidates.keys.joinToString { it.name }}"
        )
        checkGameEnd()
    }

    fun addObserver(observer: GameObserver) {
        observers.add(observer)
    }

    fun removeObserver(observer: GameObserver) {
        observers.remove(observer)
    }

    private fun notifyStageChanged(newStage: Stage) {
        for (observer in observers) {
            observer.onStageChanged(newStage)
        }
    }

    private fun notifyPlayerChanged(player: Player) {
        for (observer in observers) {
            observer.onPlayerChanged(player)
        }
    }

    fun getCurrentStage(): Stage {
        return currentStage
    }

    fun setCurrentStage(stage: Stage) {
        currentStage = stage
        notifyStageChanged(stage)
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

    fun checkRoleAllPlayers(): MutableMap<Player, Player.Role> {
        val playerRoles: MutableMap<Player, Player.Role> = mutableMapOf()
        for (player in playerList) {
            playerRoles[player] = checkPlayerRole(player)
        }
        return playerRoles
    }

    fun makeShoot(targets: MutableList<Player>) {
        val distinctTargets = targets.distinct()
        val areTargetsEqual = distinctTargets.size == 1
        if (areTargetsEqual) {
            val target = distinctTargets[0]
            if (target.isAlive) {
                kickedPlayers.put(target, Stage.NIGHT)
            }
        } else {

        }
    }

    fun makeVote(player: Player) {
        kickedPlayers.put(player, Stage.VOTING)
    }

    fun kickPlayer() {
        kickedPlayers.keys.last().isAlive = false
        speechPlayerOrder.remove(kickedPlayers.keys.last())
        kickedPlayers.remove(kickedPlayers.keys.last())

    }

    fun getKickedPlayers(): MutableList<Player> {
        return kickedPlayers.keys.toMutableList()
    }

    fun checkGameEnd() {
        val redPlayers = getAllAliveRedPlayers()
        val blackPlayers = getAllAliveBlackPlayers()

        if (blackPlayers.size == redPlayers.size) {
            currentStage = Stage.WIN
            //todo
        }
        if (blackPlayers.size == 0) {
            currentStage = Stage.WIN
            //todo
        }
    }

    fun addPlayer(playerName: String) {
        playerList.add(Player(playerName, playerList.size + 1))
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

    fun getAllPlayers(): MutableList<Player> {
        return playerList
    }

    fun getCurrentPlayer(): Player {
        return currentPlayer
    }

    fun getAllAliveRedPlayers(): MutableList<Player> {
        return getAlivePlayers().filter {
            it.role in listOf(Player.Role.CIVIL, Player.Role.SHERIFF)
        }.toMutableList()
    }

    fun getAllAliveBlackPlayers(): MutableList<Player> {
        return getAlivePlayers().filter {
            it.role in listOf(Player.Role.MAFIA, Player.Role.DON)
        }.toMutableList()
    }

    fun getAlivePlayers(): MutableList<Player> {
        return getAllPlayers().filter { it.isAlive } as MutableList<Player>
    }

    fun getDeadPlayers(): MutableList<Player> {
        return playerList.filter { !it.isAlive } as MutableList<Player>
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

    fun removePlayer() {
        playerList.removeAt(playerList.size - 1)
    }


}