package com.miga.piggy.home.domain.entity

import com.miga.piggy.transaction.domain.entity.TransactionType

data class Category(
    val id: String = "",
    val name: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val color: String = "#6200EE",
    val isDefault: Boolean = false
)