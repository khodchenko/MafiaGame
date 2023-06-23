package com.khodchenko.mafia.data

class VoteHelper {
    private constructor()

    companion object {
        @Volatile
        private var instance: VoteHelper? = null

        fun getInstance(): VoteHelper {
            return instance ?: synchronized(this) {
                instance ?: VoteHelper().also { instance = it }
            }
        }
    }

    var candidates: MutableMap<Player, MutableList<Player>> = mutableMapOf()
    var currentCandidateIndex: Int = 0

    fun addCandidate(player: Player) {
        candidates.put(player, mutableListOf())
    }

    fun removeCandidate(player: Player) {
        candidates.remove(player)
    }

    fun addVoteForCandidate(voter: Player) {
        val lastCandidate = candidates.keys.lastOrNull()
        lastCandidate?.let { candidate ->
            candidates[candidate]?.add(voter)
        }
    }

    fun removeVoteForCandidate(voter: Player) {
        val lastCandidate = candidates.keys.lastOrNull()
        lastCandidate?.let { candidate ->
            candidates[candidate]?.remove(voter)
        }
    }

    fun nextCandidate() {
        if (currentCandidateIndex != candidates.size - 1) {
            currentCandidateIndex + 1
        }
    }

    fun clearCandidates() {
        candidates = mutableMapOf()
        currentCandidateIndex = 0
    }

    fun voteForCandidate(candidate: Player, votes: MutableList<Player>) {
        this.candidates.put(candidate, votes)
    }

    fun calculateVotes(candidatesAndVotes: MutableMap<Player, MutableList<Player>> = this.candidates): MutableMap<Player, MutableList<Player>> {
        val maxVotes = candidatesAndVotes.values.map { it.size }.maxOrNull()
        return candidatesAndVotes.filterValues { it.size == maxVotes }.toMutableMap()
    }


}
