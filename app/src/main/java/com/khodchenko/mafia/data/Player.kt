package com.khodchenko.mafia.data

class Player(
    var name: String,
    var number: Int,
    var role: Enum<Role> = Role.CIVIL,
    var score: Float = 0.0F,
    var penalty: Int = 0,
    var isAlive: Boolean = true,
    var isOnVote: Boolean = false
) {
    enum class Role {
        CIVIL,
        SHERIFF,
        MAFIA,
        DON
    }
}