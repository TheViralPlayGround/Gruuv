package com.example.gruuv.helpers

import java.text.SimpleDateFormat
import java.util.*

/**
 * Converts a date string (e.g., "2024-12-06") into a week index.
 */
fun String.toWeekIndex(): Int {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = dateFormat.parse(this) ?: return 0

    val calendar = Calendar.getInstance().apply {
        time = date
    }
    return calendar.get(Calendar.WEEK_OF_YEAR)
}

/**
 * Converts a date string (e.g., "2024-12-06") into a month index.
 */
fun String.toMonthIndex(): Int {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = dateFormat.parse(this) ?: return 0

    val calendar = Calendar.getInstance().apply {
        time = date
    }
    return calendar.get(Calendar.MONTH) // Returns 0 for January, 1 for February, etc.
}

/**
 * Converts a date string (e.g., "2024-12-06") to a day index.
 */
fun String.toDayIndex(baseDate: String): Int {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val parsedDate = dateFormat.parse(this) ?: return 0
    val parsedBaseDate = dateFormat.parse(baseDate) ?: return 0

    val diffInMillis = parsedDate.time - parsedBaseDate.time
    return (diffInMillis / (1000 * 60 * 60 * 24)).toInt() // Convert milliseconds to days
}
