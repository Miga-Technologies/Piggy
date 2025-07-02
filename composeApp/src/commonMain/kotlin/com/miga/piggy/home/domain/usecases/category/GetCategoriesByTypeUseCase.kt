package com.miga.piggy.home.domain.usecases.category

import com.miga.piggy.home.domain.entity.Category
import com.miga.piggy.transaction.domain.entity.TransactionType
import com.miga.piggy.home.domain.repository.FinancialRepository

class GetCategoriesByTypeUseCase(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(type: TransactionType): List<Category> {
        return repository.getCategories().filter { it.type == type }
    }
}