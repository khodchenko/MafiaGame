package com.khodchenko.mafia.data

data class Player(
    private var name: String,
    private var number: Int,
    private var role: Enum<Role> = Role.CIVIL,
    private var score: Float = 0.0F,
    private var penalty: Int = 0,
    var isAlive: Boolean = true
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
}