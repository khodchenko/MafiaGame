package com.khodchenko.mafia.data

class VoteHelper(
    var alivePlayers: MutableList<Player>
) {
    var candidates: MutableList<Player> = mutableListOf()
    var candidateWithVotes : MutableMap<Player, MutableList<Player>> = mutableMapOf()

    fun add(player: Player) {
        candidates.add(player)
    }

    fun remove(player: Player) {
        candidates.remove(player)
    }

    fun voteForCandidate(candidate: Player, votes: MutableList<Player>) {
        this.candidateWithVotes.put(candidate, votes)
    }

    fun calculateVotes(candidatesAndVotes: MutableMap<Player, MutableList<Player>> = this.candidateWithVotes): MutableMap<Player, MutableList<Player>> {
        val maxVotes = candidatesAndVotes.values.map { it.size }.maxOrNull()
        return candidatesAndVotes.filterValues { it.size == maxVotes }.toMutableMap()
    }
}
