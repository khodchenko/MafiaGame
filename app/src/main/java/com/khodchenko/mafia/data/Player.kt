package com.khodchenko.mafia.data

import android.os.Parcelable

class Player(
    var name: String,
    var number: Int,
    var role: Enum<Role> = Role.CIVIL,
    var score: Double = 0.0,
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