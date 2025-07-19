package com.miga.piggy.transaction.presentation.ui

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
class AddTransactionScreen(
    private val initialType: TransactionType = TransactionType.EXPENSE
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: AddTransactionViewModel = koinInject()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.setTransactionType(initialType)
        }

        LaunchedEffect(uiState.success) {
            if (uiState.success) {
                navigator.pop()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (uiState.transactionType == TransactionType.EXPENSE)
                                "Adicionar Gasto"
                            else
                                "Adicionar Receita"
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Voltar"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
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
                        enabled = !uiState.isLoading
                    )
                }

                item {
                    OutlinedTextField(
                        value = uiState.description,
                        onValueChange = viewModel::updateDescription,
                        label = { Text("Descrição") },
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
                                value = uiState.selectedCategory?.name ?: "Selecione uma categoria",
                                onValueChange = { },
                                label = { Text("Categoria") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = uiState.isCategoryDropdownExpanded
                                    )
                                },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                            )

                            this@ExposedDropdownMenuBox.ExposedDropdownMenu(
                                expanded = uiState.isCategoryDropdownExpanded,
                                onDismissRequest = { viewModel.setIsCategoryDropdownExpanded(false) }
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
                                Icon(Icons.Rounded.DateRange, contentDescription = "Selecionar data")
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
                    Button(
                        onClick = viewModel::saveTransaction,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            if (uiState.transactionType == TransactionType.EXPENSE)
                                "Salvar Gasto"
                            else
                                "Salvar Receita"
                        )
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