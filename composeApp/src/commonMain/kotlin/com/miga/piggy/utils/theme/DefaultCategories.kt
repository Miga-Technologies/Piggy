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
            id = "default_health",
            name = "Saúde",
            type = TransactionType.EXPENSE,
            color = "#EF5350",
            isDefault = true
        ),
        Category(
            id = "default_education",
            name = "Educação",
            type = TransactionType.EXPENSE,
            color = "#5C6BC0",
            isDefault = true
        ),

        // Categorias de Receitas
        Category(
            id = "default_salary",
            name = "Salário",
            type = TransactionType.INCOME,
            color = "#66BB6A",
            isDefault = true
        ),
        Category(
            id = "default_bonus",
            name = "Bônus",
            type = TransactionType.INCOME,
            color = "#29B6F6",
            isDefault = true
        ),
        Category(
            id = "default_investment",
            name = "Investimentos",
            type = TransactionType.INCOME,
            color = "#26A69A",
            isDefault = true
        ),
        Category(
            id = "default_freelance",
            name = "Freelance",
            type = TransactionType.INCOME,
            color = "#AB47BC",
            isDefault = true
        ),
        Category(
            id = "default_sales",
            name = "Vendas",
            type = TransactionType.INCOME,
            color = "#FFA726",
            isDefault = true
        ),
        Category(
            id = "default_gift",
            name = "Presente",
            type = TransactionType.INCOME,
            color = "#EC407A",
            isDefault = true
        )
    )

    fun getCategoryByName(name: String): Category? {
        return defaultCategories.find { it.name == name }
    }
}