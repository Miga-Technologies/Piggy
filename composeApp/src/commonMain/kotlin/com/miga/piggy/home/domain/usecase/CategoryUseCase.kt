package com.miga.piggy.home.domain.usecase

import com.miga.piggy.home.domain.entity.Category
import com.miga.piggy.home.domain.repository.FinancialRepository

class GetCategoriesUseCase(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(): List<Category> {
        return repository.getCategories()
    }
}

class AddCategoryUseCase(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(category: Category): Result<String> {
        return repository.addCategory(category)
    }
}

class UpdateCategoryUseCase(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(category: Category): Result<Unit> {
        return repository.updateCategory(category)
    }
}

class DeleteCategoryUseCase(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(categoryId: String): Result<Unit> {
        return repository.deleteCategory(categoryId)
    }
}