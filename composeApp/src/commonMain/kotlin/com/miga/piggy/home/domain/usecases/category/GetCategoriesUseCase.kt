package com.miga.piggy.home.domain.usecases.category

import com.miga.piggy.home.domain.entity.Category
import com.miga.piggy.home.domain.repository.FinancialRepository

class GetCategoriesUseCase(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(): List<Category> {
        return repository.getCategories()
    }
}