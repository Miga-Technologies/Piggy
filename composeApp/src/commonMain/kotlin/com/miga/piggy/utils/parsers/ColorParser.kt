package com.miga.piggy.utils.parsers

import androidx.compose.ui.graphics.Color

/**
 * Utilitários para fazer parse de cores em formato hexadecimal
 * Compatível com Compose Multiplatform
 */
object ColorParser {

    /**
     * Converte uma string hexadecimal em Color do Compose
     *
     * @param hexColor String no formato "#RRGGBB" ou "#AARRGGBB" (com ou sem #)
     * @return Color do Compose ou cor padrão em caso de erro
     *
     * Exemplos:
     * - "#6200EE" -> Color RGB
     * - "#FF6200EE" -> Color ARGB
     * - "6200EE" -> Color RGB (sem #)
     */
    fun parseHexColor(hexColor: String): Color {
        return try {
            val cleanHex = hexColor.removePrefix("#")
            val colorInt = when (cleanHex.length) {
                6 -> {
                    // RGB: #RRGGBB -> FFRRGGBB (adiciona alpha FF)
                    0xFF000000.toULong() or cleanHex.toULong(16)
                }
                8 -> {
                    // ARGB: #AARRGGBB
                    cleanHex.toULong(16)
                }
                else -> {
                    // Formato inválido, retorna cor padrão
                    DEFAULT_COLOR_VALUE
                }
            }
            Color(colorInt.toString().toLong())
        } catch (_: Exception) {
            Color(DEFAULT_COLOR_VALUE) // Cor padrão em caso de erro
        }
    }

    /**
     * Converte um Color do Compose para string hexadecimal
     *
     * @param color Color do Compose
     * @param includeAlpha Se deve incluir o canal alpha (ARGB) ou não (RGB)
     * @return String no formato "#RRGGBB" ou "#AARRGGBB"
     */
    fun colorToHex(color: Color, includeAlpha: Boolean = false): String {
        val argb = color.value.toInt()
        return if (includeAlpha) {
            "#${argb.toUInt().toString(16).uppercase().padStart(8, '0')}" // ARGB
        } else {
            "#${(argb and 0x00FFFFFF).toUInt().toString(16).uppercase().padStart(6, '0')}" // RGB
        }
    }

    /**
     * Valida se uma string é uma cor hexadecimal válida
     *
     * @param hexColor String para validar
     * @return true se for válida, false caso contrário
     */
    fun isValidHexColor(hexColor: String): Boolean {
        val cleanHex = hexColor.removePrefix("#")
        return when (cleanHex.length) {
            6, 8 -> {
                try {
                    cleanHex.toULong(16)
                    true
                } catch (_: Exception) {
                    false
                }
            }
            else -> false
        }
    }

    private const val DEFAULT_COLOR_VALUE = 0xFF6200EE // Cor padrão (roxo Material)
}