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

    enum class Stage {
        NIGHT,
        LAST_WORD,
        DAY,
        VOTING
    }

    fun startGame() {
        currentPlayer = playerList[0]
        speechPlayerOrder = getAlivePlayers()
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

    fun makeVote(player: Player){
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

    private fun checkGameEnd(alivePlayers: MutableList<Player>) {
        val redPlayers = getAllRedPlayers()
        val blackPlayers = getAllBlackPlayers()

        if (blackPlayers.size == redPlayers.size) {
            inGame = false
        }
        if (blackPlayers.size == 0) {
            inGame = false
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

    private fun getDonOrNull(allPlayers: MutableList<Player>): Player? {
        return allPlayers.find { it.role == Player.Role.DON && it.isAlive }
    }

    private fun getSheriffOrNull(allPlayers: MutableList<Player>): Player? {
        return allPlayers.find { it.role == Player.Role.SHERIFF && it.isAlive }
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