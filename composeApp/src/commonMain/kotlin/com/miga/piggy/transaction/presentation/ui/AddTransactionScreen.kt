package com.miga.piggy.transaction.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.miga.piggy.transaction.domain.entity.TransactionType
import com.miga.piggy.transaction.presentation.viewmodel.AddTransactionViewModel
import com.miga.piggy.utils.datepicker.PlatformDatePicker
import com.miga.piggy.utils.formatters.formatDate
import com.miga.piggy.utils.theme.PiggyGradients
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
class AddTransactionScreen(
    private val initialType: TransactionType = TransactionType.EXPENSE,
    private val preselectedCategory: String? = null
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: AddTransactionViewModel = koinInject()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.setTransactionType(initialType)
            preselectedCategory?.let { viewModel.selectCategoryByName(it) }
        }

        LaunchedEffect(uiState.success) {
            if (uiState.success) {
                navigator.pop()
            }
        }

        // Background com gradiente sutil
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        )
                    )
                )
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        TopAppBar(
                            title = {
                                Text(
                                    if (uiState.transactionType == TransactionType.EXPENSE)
                                        "Adicionar Gasto"
                                    else
                                        "Adicionar Receita",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { navigator.pop() }) {
                                    Icon(
                                        Icons.AutoMirrored.Rounded.ArrowBack,
                                        contentDescription = "Voltar",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent
                            )
                        )
                    }
                }
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Tipo de transação",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    TransactionType.entries.forEach { type ->
                                        Row(
                                            modifier = Modifier
                                                .selectable(
                                                    selected = (type == uiState.transactionType),
                                                    onClick = { viewModel.setTransactionType(type) },
                                                    role = Role.RadioButton
                                                )
                                                .weight(1f)
                                                .padding(horizontal = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = (type == uiState.transactionType),
                                                onClick = { viewModel.setTransactionType(type) }
                                            )
                                            Text(
                                                text = if (type == TransactionType.EXPENSE) "Gasto" else "Receita",
                                                modifier = Modifier.padding(start = 8.dp),
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = uiState.amount,
                            onValueChange = viewModel::updateAmount,
                            label = { Text("Valor") },
                            leadingIcon = { Text("R$") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isLoading,
                            isError = uiState.error?.contains("valor", ignoreCase = true) == true,
                            supportingText = if (uiState.error?.contains(
                                    "valor",
                                    ignoreCase = true
                                ) == true
                            ) {
                                { Text("Digite um valor para continuar") }
                            } else null
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = uiState.description,
                            onValueChange = viewModel::updateDescription,
                            label = { Text("Descrição (opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isLoading,
                            maxLines = 3
                        )
                    }

                    item {
                        Column {
                            ExposedDropdownMenuBox(
                                modifier = Modifier.fillMaxWidth(),
                                expanded = uiState.isCategoryDropdownExpanded,
                                onExpandedChange = { viewModel.setIsCategoryDropdownExpanded(it) }
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                                    readOnly = true,
                                    value = uiState.selectedCategory?.name
                                        ?: "Selecione uma categoria",
                                    onValueChange = { },
                                    label = { Text("Categoria") },
                                    isError = uiState.error?.contains(
                                        "categoria",
                                        ignoreCase = true
                                    ) == true,
                                    supportingText = if (uiState.error?.contains(
                                            "categoria",
                                            ignoreCase = true
                                        ) == true
                                    ) {
                                        { Text("Selecione uma categoria para continuar") }
                                    } else null,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = uiState.isCategoryDropdownExpanded
                                        )
                                    },
                                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                                )

                                this@ExposedDropdownMenuBox.ExposedDropdownMenu(
                                    expanded = uiState.isCategoryDropdownExpanded,
                                    onDismissRequest = {
                                        viewModel.setIsCategoryDropdownExpanded(
                                            false
                                        )
                                    }
                                ) {
                                    uiState.categories.forEach { category ->
                                        DropdownMenuItem(
                                            onClick = {
                                                viewModel.selectCategory(category)
                                                viewModel.setIsCategoryDropdownExpanded(false)
                                            },
                                            text = { Text(category.name) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        var showDatePicker by remember { mutableStateOf(false) }

                        OutlinedTextField(
                            value = formatDate(uiState.selectedDate),
                            onValueChange = { },
                            label = { Text("Data") },
                            trailingIcon = {
                                IconButton(
                                    onClick = { showDatePicker = true }
                                ) {
                                    Icon(
                                        Icons.Rounded.DateRange,
                                        contentDescription = "Selecionar data"
                                    )
                                }
                            },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isLoading
                        )

                        if (showDatePicker) {
                            PlatformDatePicker(
                                selectedDate = uiState.selectedDate,
                                onDateSelected = { selectedDate ->
                                    viewModel.updateDate(selectedDate)
                                    showDatePicker = false
                                },
                                onDismiss = { showDatePicker = false }
                            )
                        }
                    }

                    item {
                        // Botão de cadastrar com gradiente
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            onClick = {
                                if (!uiState.isLoading) {
                                    when {
                                        uiState.amount.isBlank() -> {
                                            viewModel.showAmountError()
                                        }

                                        uiState.selectedCategory == null -> {
                                            viewModel.showCategoryError()
                                        }

                                        else -> {
                                            viewModel.saveTransaction()
                                        }
                                    }
                                }
                            },
                            enabled = !uiState.isLoading,
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = if (uiState.transactionType == TransactionType.EXPENSE)
                                            PiggyGradients.ExpenseGradient
                                        else
                                            PiggyGradients.IncomeGradient
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (uiState.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = if (uiState.transactionType == TransactionType.EXPENSE)
                                                Icons.Rounded.Remove
                                            else
                                                Icons.Rounded.Add,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (uiState.transactionType == TransactionType.EXPENSE)
                                                "Salvar Gasto"
                                            else
                                                "Salvar Receita",
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        uiState.error?.let { error ->
            LaunchedEffect(error) {
                kotlinx.coroutines.delay(3000)
                viewModel.clearError()
            }
        }
    }
}

object AddExpenseScreen : Screen {
    @Composable
    override fun Content() {
        AddTransactionScreen(TransactionType.EXPENSE).Content()
    }
}

object AddIncomeScreen : Screen {
    @Composable
    override fun Content() {
        AddTransactionScreen(TransactionType.INCOME).Content()
    }
}