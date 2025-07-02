package com.miga.piggy.utils.formatters

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun formatDate(timestampMillis: Long): String {
    val timeZone = TimeZone.currentSystemDefault()
    val localDate = Instant.fromEpochMilliseconds(timestampMillis)
        .toLocalDateTime(timeZone)
        .date

    val day = localDate.day.toString().padStart(2, '0')
    val month = localDate.month.number.toString().padStart(2, '0')
    val year = localDate.year.toString()

    return "$day/$month/$year"
}