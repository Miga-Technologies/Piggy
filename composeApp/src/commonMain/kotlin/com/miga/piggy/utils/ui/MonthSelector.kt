package com.miga.piggy.utils.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Data class representing a month-year selection
 */
data class MonthYear(
    val year: Int,
    val month: Month
) {
    val monthName: String
        get() = when (month) {
            Month.JANUARY -> "Janeiro"
            Month.FEBRUARY -> "Fevereiro"
            Month.MARCH -> "Março"
            Month.APRIL -> "Abril"
            Month.MAY -> "Maio"
            Month.JUNE -> "Junho"
            Month.JULY -> "Julho"
            Month.AUGUST -> "Agosto"
            Month.SEPTEMBER -> "Setembro"
            Month.OCTOBER -> "Outubro"
            Month.NOVEMBER -> "Novembro"
            Month.DECEMBER -> "Dezembro"
        }

    val displayText: String
        get() = "$monthName $year"

    val shortDisplayText: String
        get() = "${monthName.take(3)} $year"

    /**
     * Get the timestamp range for this month
     */
    @OptIn(ExperimentalTime::class)
    fun getMonthRange(): Pair<Long, Long> {
        val timeZone = TimeZone.currentSystemDefault()

        val startOfMonth = LocalDate(year, month, 1)
            .atStartOfDayIn(timeZone)
            .toEpochMilliseconds()

        val nextMonth = if (month == Month.DECEMBER) {
            LocalDate(year + 1, Month.JANUARY, 1)
        } else {
            LocalDate(year, Month(month.number + 1), 1)
        }

        val endOfMonth = nextMonth
            .atStartOfDayIn(timeZone)
            .minus(1, DateTimeUnit.MILLISECOND, timeZone)
            .toEpochMilliseconds()

        return startOfMonth to endOfMonth
    }

    companion object {
        /**
         * Get current month-year
         */
        @OptIn(ExperimentalTime::class)
        fun current(): MonthYear {
            val now = Clock.System.now()
            val timeZone = TimeZone.currentSystemDefault()
            val today = now.toLocalDateTime(timeZone).date
            return MonthYear(today.year, today.month)
        }

        /**
         * Create MonthYear from timestamp
         */
        @OptIn(ExperimentalTime::class)
        fun fromTimestamp(timestampMillis: Long): MonthYear {
            val timeZone = TimeZone.currentSystemDefault()
            val date = Instant.fromEpochMilliseconds(timestampMillis)
                .toLocalDateTime(timeZone)
                .date
            return MonthYear(date.year, date.month)
        }
    }

    operator fun compareTo(other: MonthYear): Int {
        if (year != other.year) {
            return year.compareTo(other.year)
        }
        return month.number.compareTo(other.month.number)
    }
}

/**
 * Month selector component with navigation arrows
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthSelector(
    selectedMonth: MonthYear,
    onMonthSelected: (MonthYear) -> Unit,
    modifier: Modifier = Modifier,
    showYear: Boolean = true
) {
    val currentMonth = MonthYear.current()

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous month button
            IconButton(
                onClick = {
                    val prevMonth = if (selectedMonth.month == Month.JANUARY) {
                        MonthYear(selectedMonth.year - 1, Month.DECEMBER)
                    } else {
                        MonthYear(selectedMonth.year, Month(selectedMonth.month.number - 1))
                    }
                    onMonthSelected(prevMonth)
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Rounded.KeyboardArrowLeft,
                    contentDescription = "Mês anterior",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Current month display
            Text(
                text = if (showYear) selectedMonth.displayText else selectedMonth.monthName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            // Next month button - disabled if current month is selected
            IconButton(
                onClick = {
                    val nextMonth = if (selectedMonth.month == Month.DECEMBER) {
                        MonthYear(selectedMonth.year + 1, Month.JANUARY)
                    } else {
                        MonthYear(selectedMonth.year, Month(selectedMonth.month.number + 1))
                    }
                    onMonthSelected(nextMonth)
                },
                enabled = selectedMonth < currentMonth,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Rounded.KeyboardArrowRight,
                    contentDescription = "Próximo mês",
                    tint = if (selectedMonth < currentMonth) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    },
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Compact month selector with dropdown style
 */
@Composable
fun CompactMonthSelector(
    selectedMonth: MonthYear,
    onMonthSelected: (MonthYear) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Card(
            onClick = { expanded = true },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = selectedMonth.shortDisplayText,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Icon(
                    Icons.Rounded.ArrowDropDown,
                    contentDescription = "Selecionar mês",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Show last 12 months including current and next 6
            val currentMonthYear = MonthYear.current()
            val months = generateMonthOptions(currentMonthYear, pastMonths = 12, futureMonths = 0)

            months.forEach { monthYear ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = monthYear.displayText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (monthYear == selectedMonth) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    },
                    onClick = {
                        onMonthSelected(monthYear)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Horizontal scrolling month selector
 */
@Composable
fun HorizontalMonthSelector(
    selectedMonth: MonthYear,
    onMonthSelected: (MonthYear) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentMonthYear = MonthYear.current()
    val months = remember {
        generateMonthOptions(currentMonthYear, pastMonths = 12, futureMonths = 0)
    }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(months) { monthYear ->
            val isSelected = monthYear == selectedMonth

            Card(
                onClick = { onMonthSelected(monthYear) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                border = if (!isSelected) {
                    CardDefaults.outlinedCardBorder()
                } else null,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = monthYear.shortDisplayText,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) {
                        Color.White
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}

/**
 * Generate a list of month options based on current month
 */
private fun generateMonthOptions(
    currentMonth: MonthYear,
    pastMonths: Int,
    futureMonths: Int
): List<MonthYear> {
    val months = mutableListOf<MonthYear>()

    // Add past months
    var tempMonth = currentMonth
    repeat(pastMonths) {
        tempMonth = if (tempMonth.month == Month.JANUARY) {
            MonthYear(tempMonth.year - 1, Month.DECEMBER)
        } else {
            MonthYear(tempMonth.year, Month(tempMonth.month.number - 1))
        }
        months.add(0, tempMonth)
    }

    // Add current month
    months.add(currentMonth)

    // Add future months
    tempMonth = currentMonth
    repeat(futureMonths) {
        tempMonth = if (tempMonth.month == Month.DECEMBER) {
            MonthYear(tempMonth.year + 1, Month.JANUARY)
        } else {
            MonthYear(tempMonth.year, Month(tempMonth.month.number + 1))
        }
        months.add(tempMonth)
    }

    return months
}