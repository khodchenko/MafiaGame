package com.khodchenko.mafia.data


import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow


interface TimerListener {
    fun onTimerFinished()
}

class Timer(private val duration: Long, private val listener: TimerListener) {
    private val remainingTimeFlow = MutableStateFlow(duration)
    private var timerJob: Job? = null


    fun start() {
        if (timerJob == null) {
            timerJob = GlobalScope.launch {
                remainingTimeFlow.collect { remainingTime ->
                    if (remainingTime <= 0) {
                        pause()
                        listener.onTimerFinished()
                    } else {
                        delay(1000)
                        remainingTimeFlow.value = remainingTime - 1000
                    }
                }
            }
        }
    }

    fun pause() {
        timerJob?.cancel()
        timerJob = null
    }

    fun restart() {
        pause()
        remainingTimeFlow.value = duration
    }

    val remainingTime: Long
        get() = remainingTimeFlow.value
}