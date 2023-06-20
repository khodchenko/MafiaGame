package com.khodchenko.mafia.data

class Player(
    var name: String,
    var number: Int,
    var role: Enum<Role> = Role.CIVIL,
    private var score: Float = 0.0F,
    var penalty: Int = 0,
    var isAlive: Boolean = true,
    var isOnVote : Boolean = false
) {
    enum class Role {
        CIVIL,
        SHERIFF,
        MAFIA,
        DON
    }

    fun isMafia(): Boolean {
        if (role == Role.DON || role == Role.MAFIA) return true
        return false
    }

    fun copy(role: Player.Role) {

    }
}