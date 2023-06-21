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

    var alivePlayers: MutableList<Player> = Game.getInstance().getAlivePlayers()
    var candidates:  MutableMap<Player, MutableList<Player>> = mutableMapOf()

    fun addCandidate(player: Player) {
        candidates.put(player, mutableListOf())
    }

    fun removeCandidate(player: Player) {
        candidates.remove(player)
    }
   fun addVoteForCandidate( voter: Player) {
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

    fun removeAllCandidates(){
        candidates = mutableMapOf()
    }
    fun voteForCandidate(candidate: Player, votes: MutableList<Player>) {
        this.candidates.put(candidate, votes)
    }

    fun calculateVotes(candidatesAndVotes: MutableMap<Player, MutableList<Player>> = this.candidates): MutableMap<Player, MutableList<Player>> {
        val maxVotes = candidatesAndVotes.values.map { it.size }.maxOrNull()
        return candidatesAndVotes.filterValues { it.size == maxVotes }.toMutableMap()
    }


}
