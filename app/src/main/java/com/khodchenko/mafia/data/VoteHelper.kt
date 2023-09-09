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
    var voteStage: Int = 0

    fun addCandidate(player: Player) {
        candidates.put(player, mutableListOf())
    }

    fun removeCandidate(player: Player) {
        candidates.remove(player)
    }

    fun addVoteForCandidate(voter: Player) {
        val lastCandidate = candidates.keys.elementAtOrNull(currentCandidateIndex)
        lastCandidate?.let { candidate ->
            candidates.computeIfAbsent(candidate) { mutableListOf() }.add(voter)
        }
    }

    fun removeVoteForCandidate(voter: Player) {
        val lastCandidate = candidates.keys.elementAtOrNull(currentCandidateIndex)
        lastCandidate?.let { candidate ->
            candidates[candidate]?.remove(voter)
        }
    }

    fun nextCandidate() {
        if (currentCandidateIndex != candidates.size - 1) {
            currentCandidateIndex ++
        }
    }

    fun clearCandidates() {
        candidates = mutableMapOf()
        currentCandidateIndex = 0
        voteStage = 0
    }

    fun calculateVotes(): MutableMap<Player, MutableList<Player>> {
        val groupedCandidates = candidates.entries.groupBy({ it.value.size }) { it.key to it.value }
        val maxVotesGroup = groupedCandidates.entries.maxByOrNull { it.key }?.value
        val filteredCandidates = maxVotesGroup?.associate { it.first to it.second }?.toMutableMap()
        val keysToClear = candidates.keys - filteredCandidates?.keys.orEmpty()

        keysToClear.forEach { key ->
            candidates[key]?.clear()
        }

        candidates = filteredCandidates ?: mutableMapOf()
        return candidates
    }

}
