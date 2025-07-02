package com.miga.piggy.home.data.model

import com.miga.piggy.home.domain.entity.Category
import com.miga.piggy.transaction.domain.entity.TransactionType
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: String = "",
    val name: String = "",
    val type: String = "EXPENSE",
    val color: String = "#6200EE",
    val isDefault: Boolean = false
) {
    fun toDomain(): Category = Category(
        id = id,
        name = name,
        type = TransactionType.valueOf(type),
        color = color,
        isDefault = isDefault
    )
}

fun Category.toDto(): CategoryDto = CategoryDto(
    id = id,
    name = name,
    type = type.name,
    color = color,
    isDefault = isDefault
)
