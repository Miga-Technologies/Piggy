package com.miga.piggy.transaction.presentation.ui

import com.miga.piggy.ThemeManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.miga.piggy.auth.presentation.viewmodel.AuthViewModel
import com.miga.piggy.transaction.domain.entity.Transaction
import com.miga.piggy.transaction.domain.entity.TransactionType
import com.miga.piggy.transaction.presentation.viewmodel.TransactionListViewModel
import com.miga.piggy.utils.formatters.formatDouble
import com.miga.piggy.utils.formatters.formatDate
import org.koin.compose.koinInject

object ViewIncomeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val authViewModel: AuthViewModel = koinInject()
        val viewModel: TransactionListViewModel = koinInject()
        val authState by authViewModel.uiState.collectAsState()
        val uiState by viewModel.uiState.collectAsState()

        // Filter state
        var filterDialogOpen by remember { mutableStateOf(false) }
        var selectedCategory by remember { mutableStateOf<String?>(null) }

        // Available categories from transactions
        val availableCategories = remember(uiState.transactions) {
            uiState.transactions.map { it.category }.distinct().sorted()
        }

        LaunchedEffect(authState.user?.id) {
            authState.user?.id?.let { userId ->
                viewModel.loadTransactions(userId, TransactionType.INCOME)
            }
        }

        val isDark = ThemeManager.getCurrentTheme()
        val incomeContainerColor = if (isDark) Color(0xFF25372B) else Color(0xFFE8F5E8)
        val incomeAmountColor = if (isDark) Color(0xFF4CFF6D) else Color(0xFF4CAF50)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Receitas") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Voltar")
                        }
                    },
                    actions = {
                        IconButton(onClick = { filterDialogOpen = true }) {
                            Icon(Icons.Rounded.FilterList, "Filtros")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { paddingValues ->
            // Apply filters
            val filteredTransactions = uiState.transactions.filter { transaction ->
                val categoryMatch =
                    selectedCategory == null || transaction.category == selectedCategory
                categoryMatch
            }
            val filteredTotal = filteredTransactions.sumOf { it.amount }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = incomeContainerColor
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Total de Receitas",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "R$ ${formatDouble(filteredTotal)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = incomeAmountColor
                        )
                        if (selectedCategory != null) {
                            Text(
                                "Filtrado por: $selectedCategory",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (filteredTransactions.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (selectedCategory != null) "Nenhuma receita encontrada para esta categoria"
                            else "Nenhuma receita encontrada",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredTransactions) { transaction ->
                            IncomeTransactionCard(transaction, amountColor = incomeAmountColor)
                        }
                    }
                }
            }

            // Filter Dialog
            if (filterDialogOpen) {
                FilterDialog(
                    title = "Filtrar Receitas",
                    availableCategories = availableCategories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    onDismiss = { filterDialogOpen = false }
                )
            }
        }
    }
}

@Composable
private fun FilterDialog(
    title: String,
    availableCategories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    var tempCategory by remember { mutableStateOf(selectedCategory) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                Text(
                    "Categoria:",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = tempCategory == null,
                                onClick = { tempCategory = null }
                            )
                            Text(
                                text = "Todas as categorias",
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    items(availableCategories) { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = tempCategory == category,
                                onClick = { tempCategory = category }
                            )
                            Text(
                                text = category,
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onCategorySelected(tempCategory)
                    onDismiss()
                }
            ) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun IncomeTransactionCard(transaction: Transaction, amountColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.category,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    if (transaction.description.isNotEmpty()) {
                        Text(
                            text = transaction.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = formatDate(transaction.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "R$ ${formatDouble(transaction.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )
            }
        }
    }
}