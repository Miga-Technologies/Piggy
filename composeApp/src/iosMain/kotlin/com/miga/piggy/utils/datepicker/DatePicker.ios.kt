package com.miga.piggy.utils.datepicker

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import network.chaintech.kmp_date_time_picker.ui.datepicker.WheelDatePickerView
import network.chaintech.kmp_date_time_picker.utils.DateTimePickerView
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
actual fun PlatformDatePicker(
    selectedDate: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val startDate = LocalDate(
        currentDateTime.year,
        currentDateTime.month.number,
        currentDateTime.day
    )
    
    WheelDatePickerView(
        showDatePicker = true,
        title = "Selecionar Data",
        doneLabel = "Confirmar",
        startDate = startDate,
        showShortMonths = true,
        minDate = LocalDate(2020, 1, 1),
        maxDate = LocalDate(2030, 12, 31),
        height = 400.dp,
        dateTimePickerView = DateTimePickerView.DIALOG_VIEW,
        onDoneClick = { selectedLocalDate ->
            val timestamp = selectedLocalDate.toEpochDays() * 24 * 60 * 60 * 1000L
            onDateSelected(timestamp)
        },
        onDismiss = onDismiss
    )
}