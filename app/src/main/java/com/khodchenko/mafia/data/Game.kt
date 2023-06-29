package com.khodchenko.mafia.data

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

                    // Уведомить наблюдателей о смене игрока
                    notifyPlayerChanged(getCurrentPlayer())
                } else {
                    if (VoteHelper.getInstance().candidates.isEmpty()){
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

                } else if (voteHelper.currentCandidateIndex < voteHelper.candidates.size - 1) {
                   voteHelper.nextCandidate()

                } else {
                    voteHelper.calculateVotes()
                    voteHelper.currentCandidateIndex = 0
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

            }
        }
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
        val redPlayers = getAllRedPlayers()
        val blackPlayers = getAllBlackPlayers()

        if (blackPlayers.size == redPlayers.size) {
            inGame = false
            Stage.WIN
        }
        if (blackPlayers.size == 0) {
            inGame = false
            Stage.WIN
        }
    }

    fun addPlayers(playerNames: ArrayList<String>, playerRoles: MutableList<Player.Role>) {
        for (player in playerNames) {
            playerList.add(Player(player, playerList.size + 1, role = playerRoles[playerList.size]))
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

    private fun clearPlayers() {
        playerList = mutableListOf()
    }

    fun getAllPlayers(): MutableList<Player> {
        return playerList
    }

    fun getCurrentPlayer(): Player {
        return currentPlayer
    }

    fun getAllRedPlayers(): MutableList<Player> {
        return getAlivePlayers().filter {
            it.role in listOf(Player.Role.CIVIL, Player.Role.SHERIFF)
        }.toMutableList()
    }

    fun getAllBlackPlayers(): MutableList<Player> {
        return getAlivePlayers().filter {
            it.role in listOf(Player.Role.MAFIA, Player.Role.DON)
        }.toMutableList()
    }

    private fun getDonOrNull(): Player? {
        return playerList.find { it.role == Player.Role.DON && it.isAlive }
    }

    private fun getSheriffOrNull(): Player? {
        return playerList.find { it.role == Player.Role.SHERIFF && it.isAlive }
    }

    fun getAlivePlayers(): MutableList<Player> {
        return getAllPlayers().filter { it.isAlive } as MutableList<Player>
    }

    fun getDeadPlayers(playerList: MutableList<Player>): MutableList<Player> {
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
}