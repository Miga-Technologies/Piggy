package com.miga.piggy.reports.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PictureAsPdf
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
import com.miga.piggy.reports.presentation.viewmodel.ReportsViewModel
import com.miga.piggy.transaction.domain.entity.TransactionType
import com.miga.piggy.utils.formatters.formatDouble
import com.miga.piggy.utils.permission.checkPermission
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

object ReportsScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val authViewModel: AuthViewModel = koinInject()
        val reportsViewModel: ReportsViewModel = koinInject()
        val authState by authViewModel.uiState.collectAsState()
        val reportsState by reportsViewModel.uiState.collectAsState()

        val snackBarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val factory = rememberPermissionsControllerFactory()
        val controller = remember(factory) {
            factory.createPermissionsController()
        }

        BindEffect(controller)

        LaunchedEffect(authState.user?.id) {
            authState.user?.id?.let { userId ->
                reportsViewModel.loadReports(userId)
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackBarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text("Relatórios") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Voltar")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    val hasPermission = checkPermission(
                                        permission = Permission.WRITE_STORAGE,
                                        controller = controller,
                                        snackBarHostState = snackBarHostState
                                    )

                                    if (hasPermission) {
                                        authState.user?.id?.let {
                                            reportsViewModel.exportToPdf()
                                        }
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Rounded.PictureAsPdf, "Exportar PDF")
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
            if (reportsState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Monthly Summary
                    item {
                        MonthlySummaryCard(
                            totalIncome = reportsState.monthlyIncome,
                            totalExpenses = reportsState.monthlyExpenses,
                            balance = reportsState.monthlyBalance
                        )
                    }

                    // Expenses by Category
                    if (reportsState.expensesByCategory.isNotEmpty()) {
                        item {
                            Text(
                                text = "Gastos por Categoria",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        item {
                            ExpensesByCategoryCard(reportsState.expensesByCategory)
                        }
                    }

                    // Income by Category
                    if (reportsState.incomeByCategory.isNotEmpty()) {
                        item {
                            Text(
                                text = "Receitas por Categoria",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        item {
                            IncomesByCategoryCard(reportsState.incomeByCategory)
                        }
                    }

                    // Recent Transactions
                    if (reportsState.recentTransactions.isNotEmpty()) {
                        item {
                            Text(
                                text = "Transações Recentes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(reportsState.recentTransactions.take(10)) { transaction ->
                            TransactionSummaryCard(transaction)
                        }
                    }
                }
            }

            // Show success message when PDF is exported
            if (reportsState.pdfExported) {
                LaunchedEffect(reportsState.pdfExported) {
                    // Show snackbar or notification
                    kotlinx.coroutines.delay(2000)
                    reportsViewModel.clearPdfExported()
                }
            }

            // Show error if any
            reportsState.error?.let { error ->
                LaunchedEffect(error) {
                    // Show error snackbar
                    kotlinx.coroutines.delay(3000)
                    reportsViewModel.clearError()
                }
            }
        }
    }
}

@Composable
private fun MonthlySummaryCard(
    totalIncome: Double,
    totalExpenses: Double,
    balance: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Resumo do Mês",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    title = "Receitas",
                    amount = totalIncome,
                    color = Color(0xFF4CAF50)
                )

                SummaryItem(
                    title = "Gastos",
                    amount = totalExpenses,
                    color = Color(0xFFD32F2F)
                )

                SummaryItem(
                    title = "Saldo",
                    amount = balance,
                    color = if (balance >= 0) Color(0xFF4CAF50) else Color(0xFFD32F2F)
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    title: String,
    amount: Double,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "R$ ${formatDouble(amount)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun ExpensesByCategoryCard(expensesByCategory: Map<String, Double>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            expensesByCategory.entries.sortedByDescending { it.value }
                .forEach { (category, amount) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "R$ ${formatDouble(amount)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFD32F2F)
                        )
                    }
                }
        }
    }
}

@Composable
private fun IncomesByCategoryCard(incomeByCategory: Map<String, Double>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            incomeByCategory.entries.sortedByDescending { it.value }.forEach { (category, amount) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "R$ ${formatDouble(amount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionSummaryCard(transaction: com.miga.piggy.transaction.domain.entity.Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                if (transaction.description.isNotEmpty()) {
                    Text(
                        text = transaction.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "R$ ${formatDouble(transaction.amount)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (transaction.type == TransactionType.INCOME)
                    Color(0xFF4CAF50) else Color(0xFFD32F2F)
            )
        }
    }
}