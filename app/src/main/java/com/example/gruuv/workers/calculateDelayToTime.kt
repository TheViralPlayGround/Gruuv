package com.example.gruuv.workers

import java.util.Calendar

fun calculateDelayToTime(hour: Int, minute: Int): Long {
    val currentTime = Calendar.getInstance()
    val targetTime = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        if (before(currentTime)) {
            add(Calendar.DAY_OF_YEAR, 1) // Schedule for the next day
        }
    }
    return targetTime.timeInMillis - currentTime.timeInMillis
}
