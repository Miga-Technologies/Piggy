package com.miga.piggy.home.domain.usecases.category

import com.miga.piggy.home.domain.entity.Category
import com.miga.piggy.home.domain.repository.FinancialRepository

class AddCategoryUseCase(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(category: Category): Result<String> {
        return repository.addCategory(category)
    }
}