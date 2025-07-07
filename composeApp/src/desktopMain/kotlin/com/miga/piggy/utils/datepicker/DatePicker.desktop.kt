package com.miga.piggy.utils.datepicker

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun PlatformDatePicker(
    selectedDate: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val currentDate = remember(selectedDate) {
        if (selectedDate > 0) {
            LocalDate.ofEpochDay(selectedDate / (24 * 60 * 60 * 1000))
        } else {
            LocalDate.now()
        }
    }

    var selectedLocalDate by remember { mutableStateOf(currentDate) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Selecionar Data",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = selectedLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Dia
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Dia", style = MaterialTheme.typography.labelMedium)
                        OutlinedTextField(
                            value = selectedLocalDate.dayOfMonth.toString(),
                            onValueChange = { newDay ->
                                newDay.toIntOrNull()?.let { day ->
                                    if (day in 1..31) {
                                        try {
                                            selectedLocalDate = selectedLocalDate.withDayOfMonth(day)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.width(80.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Mês", style = MaterialTheme.typography.labelMedium)
                        OutlinedTextField(
                            value = selectedLocalDate.monthValue.toString(),
                            onValueChange = { newMonth ->
                                newMonth.toIntOrNull()?.let { month ->
                                    if (month in 1..12) {
                                        try {
                                            selectedLocalDate = selectedLocalDate.withMonth(month)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.width(80.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Ano", style = MaterialTheme.typography.labelMedium)
                        OutlinedTextField(
                            value = selectedLocalDate.year.toString(),
                            onValueChange = { newYear ->
                                newYear.toIntOrNull()?.let { year ->
                                    if (year > 1900 && year < 2100) {
                                        try {
                                            selectedLocalDate = selectedLocalDate.withYear(year)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.width(100.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            val millis = selectedLocalDate
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant()
                                .toEpochMilli()
                            onDateSelected(millis)
                            onDismiss()
                        }
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}