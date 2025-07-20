package com.miga.piggy.utils.theme

import com.miga.piggy.home.domain.entity.Category
import com.miga.piggy.transaction.domain.entity.TransactionType

object DefaultCategories {
    val defaultCategories = listOf(
        Category(
            id = "default_water",
            name = "Água",
            type = TransactionType.EXPENSE,
            color = "#4FC3F7",
            isDefault = true
        ),
        Category(
            id = "default_energy",
            name = "Energia",
            type = TransactionType.EXPENSE,
            color = "#FFB74D",
            isDefault = true
        ),
        Category(
            id = "default_internet",
            name = "Internet",
            type = TransactionType.EXPENSE,
            color = "#81C784",
            isDefault = true
        ),
        Category(
            id = "default_phone",
            name = "Telefone",
            type = TransactionType.EXPENSE,
            color = "#42A5F5",
            isDefault = true
        ),
        Category(
            id = "default_food",
            name = "Alimentação",
            type = TransactionType.EXPENSE,
            color = "#FF8A65",
            isDefault = true
        ),
        Category(
            id = "default_transport",
            name = "Transporte",
            type = TransactionType.EXPENSE,
            color = "#AB47BC",
            isDefault = true
        ),
        Category(
            id = "default_salary",
            name = "Salário",
            type = TransactionType.INCOME,
            color = "#66BB6A",
            isDefault = true
        ),
        Category(
            id = "default_freelance",
            name = "Freelance",
            type = TransactionType.INCOME,
            color = "#26A69A",
            isDefault = true
        )
    )

    fun getCategoryByName(name: String): Category? {
        return defaultCategories.find { it.name == name }
    }
}