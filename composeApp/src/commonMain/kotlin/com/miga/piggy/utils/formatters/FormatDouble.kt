package com.miga.piggy.utils.formatters

import kotlin.math.pow
import kotlin.math.round


fun formatDouble(value: Double, decimals: Int = 2): String {
    val factor = 10.0.pow(decimals)
    val rounded = round(value * factor) / factor
    return buildString {
        append(rounded.toString())
        val parts = rounded.toString().split(".")
        if (parts.size == 1) {
            append(".")
            repeat(decimals) { append("0") }
        } else if (parts[1].length < decimals) {
            repeat(decimals - parts[1].length) { append("0") }
        }
    }
}