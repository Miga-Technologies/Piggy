package com.miga.piggy.transaction.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miga.piggy.auth.presentation.viewmodel.AuthViewModel
import com.miga.piggy.home.domain.entity.Category
import com.miga.piggy.transaction.domain.entity.Transaction
import com.miga.piggy.transaction.domain.entity.TransactionType
import com.miga.piggy.home.domain.usecases.category.GetCategoriesByTypeUseCase
import com.miga.piggy.transaction.domain.usecases.AddTransactionUseCase
import com.miga.piggy.transaction.presentation.state.AddTransactionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddTransactionViewModel(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getCategoriesByTypeUseCase: GetCategoriesByTypeUseCase,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val categories = getCategoriesByTypeUseCase(_uiState.value.transactionType)
                _uiState.value = _uiState.value.copy(
                    categories = categories,
                    selectedCategory = categories.firstOrNull()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Erro ao carregar categorias: ${e.message}"
                )
            }
        }
    }

    fun setTransactionType(type: TransactionType) {
        _uiState.value = _uiState.value.copy(transactionType = type)
        loadCategories()
    }

    fun updateAmount(amount: String) {
        val filteredAmount = amount.filter { it.isDigit() || it == '.' || it == ',' }
            .replace(',', '.')
        _uiState.value = _uiState.value.copy(amount = filteredAmount)
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun selectCategory(category: Category) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun updateDate(date: Long) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }

    fun saveTransaction() {
        val userId = authViewModel.uiState.value.user?.id ?: return
        val state = _uiState.value

        // Validações
        if (state.amount.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Digite o valor da transação")
            return
        }

        if (state.description.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Digite uma descrição")
            return
        }

        if (state.selectedCategory == null) {
            _uiState.value = _uiState.value.copy(error = "Selecione uma categoria")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val amount = state.amount.toDoubleOrNull()
                if (amount == null || amount <= 0) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Digite um valor válido (maior que zero)"
                    )
                    return@launch
                }

                val transaction = Transaction(
                    userId = userId,
                    type = state.transactionType,
                    amount = amount,
                    category = state.selectedCategory.name,
                    description = state.description,
                    date = state.selectedDate
                )

                addTransactionUseCase(transaction).fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            success = true
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Erro ao salvar: ${error.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erro inesperado: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}