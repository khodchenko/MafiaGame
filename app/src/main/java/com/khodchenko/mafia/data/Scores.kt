package com.khodchenko.mafia.data

class Scores {

    val WIN_SCORE : Double = 1.0
    val DISQUALIFICATION : Double = -0.5
    val BEST_MOVE_1_OF_3 : Double = 0.1
    val BEST_MOVE_2_OF_3 : Double = 0.25
    val BEST_MOVE_3_OF_3 : Double = 0.4

    val FIND_SHERIFF_AT_1_NIGHT : Double = 0.1
    val FIND_SHERIFF_AT_2_NIGHT : Double = 0.05
    val KILL_SHERIFF_AT_1 : Double = 0.15
    val BLACK_CHECK_AT_1 : Double = 0.15
    val BLACK_CHECK_AT_2 : Double = 0.05
    val CIVIL_VOTE_AT_CIVIL : Double = -0.15
    val CIVIL_VOTE_AT_MAFIA : Double = 0.2
    val MAFIA_VOTE_AT_CIVIL : Double = 0.2
    val MAFIA_VOTE_AT_SHERIFF : Double = 0.15
    val MAFIA_VOTE_AT_MAFIA : Double = -0.1
    val SHERIFF_VOTE_AT_CIVIL : Double = -0.2
    val SHERIFF_VOTE_AT_SHERIFF : Double = -0.2
    val SHERIFF_VOTE_AT_MAFIA : Double = 0.2
}