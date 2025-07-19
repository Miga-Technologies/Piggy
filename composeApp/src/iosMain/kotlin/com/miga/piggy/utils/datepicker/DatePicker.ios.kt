package com.miga.piggy.utils.datepicker

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import network.chaintech.kmp_date_time_picker.ui.datepicker.WheelDatePickerView
import network.chaintech.kmp_date_time_picker.utils.DateTimePickerView
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
actual fun PlatformDatePicker(
    selectedDate: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    // Converter a data selecionada de UTC para LocalDate
    val selectedLocalDate = Instant.fromEpochMilliseconds(selectedDate)
        .toLocalDateTime(TimeZone.UTC)
        .date
    
    WheelDatePickerView(
        modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
        showDatePicker = true,
        title = "Selecionar Data",
        doneLabel = "Confirmar",
        titleStyle = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        ),
        doneLabelStyle = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF007AFF)
        ),
        startDate = selectedLocalDate,
        showShortMonths = true,
        minDate = LocalDate(2020, 1, 1),
        maxDate = LocalDate(2030, 12, 31),
        height = 520.dp,
        rowCount = 5,
        hideHeader = false,
        dateTimePickerView = DateTimePickerView.DIALOG_VIEW,
        onDoneClick = { localDate ->
            val utcDateTime =
                LocalDateTime(localDate.year, localDate.month, localDate.day, 12, 0)
            val timestamp = utcDateTime.toInstant(TimeZone.UTC).toEpochMilliseconds()
            onDateSelected(timestamp)
        },
        onDismiss = onDismiss
    )
}