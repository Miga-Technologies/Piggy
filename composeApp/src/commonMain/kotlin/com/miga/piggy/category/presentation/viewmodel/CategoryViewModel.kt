package com.miga.piggy.category.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miga.piggy.home.domain.entity.Category
import com.miga.piggy.home.domain.usecase.AddCategoryUseCase
import com.miga.piggy.home.domain.usecase.DeleteCategoryUseCase
import com.miga.piggy.home.domain.usecase.GetCategoriesUseCase
import com.miga.piggy.home.domain.usecase.UpdateCategoryUseCase
import com.miga.piggy.transaction.domain.entity.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CategoryUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val editingCategory: Category? = null
)

data class CategoryFormState(
    val name: String = "",
    val color: String = "#6200EE",
    val nameError: String? = null
)

class CategoryViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(CategoryFormState())
    val formState: StateFlow<CategoryFormState> = _formState.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val categories = getCategoriesUseCase()
                _uiState.value = _uiState.value.copy(
                    categories = categories,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erro ao carregar categorias"
                )
            }
        }
    }

    fun showAddDialog(initialType: TransactionType = TransactionType.EXPENSE) {
        _uiState.value = _uiState.value.copy(showAddDialog = true)
        _formState.value = CategoryFormState()
    }

    fun showEditDialog(category: Category) {
        _uiState.value = _uiState.value.copy(
            showAddDialog = true,
            editingCategory = category
        )
        _formState.value = CategoryFormState(
            name = category.name,
            color = category.color
        )
    }

    fun hideDialog() {
        _uiState.value = _uiState.value.copy(
            showAddDialog = false,
            editingCategory = null
        )
        _formState.value = CategoryFormState()
    }

    fun updateName(name: String) {
        _formState.value = _formState.value.copy(name = name, nameError = null)
    }

    fun updateColor(color: String) {
        _formState.value = _formState.value.copy(color = color)
    }

    fun saveCategory() {
        val form = _formState.value
        if (form.name.isBlank()) {
            _formState.value = form.copy(nameError = "Nome é obrigatório")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val editingCategory = _uiState.value.editingCategory
                if (editingCategory != null) {
                    val updatedCategory = editingCategory.copy(
                        name = form.name,
                        color = form.color
                    )
                    updateCategoryUseCase(updatedCategory)
                } else {
                    val newCategory = Category(
                        id = "",
                        name = form.name,
                        type = TransactionType.EXPENSE,
                        color = form.color,
                        isDefault = false
                    )
                    addCategoryUseCase(newCategory)
                }

                hideDialog()
                loadCategories()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erro ao salvar categoria"
                )
            }
        }
    }

    fun deleteCategory(category: Category) {
        if (category.isDefault) {
            _uiState.value = _uiState.value.copy(error = "Não é possível deletar categorias padrão")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                deleteCategoryUseCase(category.id)
                loadCategories()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erro ao deletar categoria"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}