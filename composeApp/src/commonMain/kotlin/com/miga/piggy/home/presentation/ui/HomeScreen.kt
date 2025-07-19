package com.miga.piggy.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.miga.piggy.auth.presentation.ui.AuthScreen
import com.miga.piggy.auth.presentation.viewmodel.AuthViewModel
import com.miga.piggy.balance.presentation.ui.EditBalanceScreen
import com.miga.piggy.category.presentation.ui.CategoryScreen
import com.miga.piggy.home.domain.entity.Category
import com.miga.piggy.home.presentation.ui.helper.MenuItem
import com.miga.piggy.home.presentation.viewmodel.HomeViewModel
import com.miga.piggy.reports.presentation.ui.ReportsScreen
import com.miga.piggy.transaction.presentation.ui.AddExpenseScreen
import com.miga.piggy.transaction.presentation.ui.AddIncomeScreen
import com.miga.piggy.transaction.presentation.ui.ViewExpensesScreen
import com.miga.piggy.transaction.presentation.ui.ViewIncomeScreen
import com.miga.piggy.utils.formatters.formatDouble
import com.miga.piggy.utils.parsers.ColorParser
import com.miga.piggy.ThemeManager
import dev.materii.pullrefresh.PullRefreshIndicator
import dev.materii.pullrefresh.PullRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState
import org.koin.compose.koinInject

object HomeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val authViewModel: AuthViewModel = koinInject()
        val homeViewModel: HomeViewModel = koinInject()
        val authUiState by authViewModel.uiState.collectAsState()
        val homeUiState by homeViewModel.uiState.collectAsState()

        var isRefreshing by remember { mutableStateOf(false) }
        var showMenuDialog by remember { mutableStateOf(false) }
        val isDarkTheme by ThemeManager.isDarkTheme
        val currentTheme = ThemeManager.getCurrentTheme()

        LaunchedEffect(authUiState.user) {
            if (authUiState.user == null) {
                navigator.replaceAll(AuthScreen)
            }
        }

        val pullRefreshState = rememberPullRefreshState(
            refreshing = isRefreshing,
            onRefresh = { homeViewModel.refresh() }
        )

        PullRefreshLayout(
            state = pullRefreshState,
            indicator = {
                PullRefreshIndicator(
                    state = pullRefreshState,
                    backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                    contentColor = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                "Olá, ${
                                    authUiState.user?.displayName?.split(" ")?.first() ?: "Usuário"
                                }!"
                            )
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    showMenuDialog = true
                                }
                            ) {
                                Icon(
                                    Icons.Rounded.MoreVert,
                                    contentDescription = "Menu"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                },
                content = { paddingValues ->
                    if (homeUiState.isLoading) {
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
                            // Card do Saldo
                            item {
                                BalanceCard(
                                    balance = homeUiState.balance,
                                    onBalanceClick = {
                                        navigator.push(EditBalanceScreen)
                                    }
                                )
                            }

                            // Menu de Ações
                            item {
                                Text(
                                    text = "O que você quer fazer?",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            item {
                                MenuGrid(navigator)
                            }

                            // Gráfico de Gastos
                            if (homeUiState.expensesByCategory.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Gastos por categoria",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }

                                item {
                                    ExpenseChartCard(homeUiState.expensesByCategory)
                                }
                            }

                            item {
                                FinancialSummary(
                                    totalIncome = homeUiState.monthlyIncome,
                                    totalExpenses = homeUiState.monthlyExpenses
                                )
                            }
                        }
                    }

                    // Mostrar erro se houver
                    homeUiState.error?.let { error ->
                        LaunchedEffect(error) {
                            // Aqui você pode mostrar um SnackBar
                            kotlinx.coroutines.delay(3000)
                            homeViewModel.clearError()
                        }
                    }
                }
            )
        }

        if (showMenuDialog) {
            AlertDialog(
                onDismissRequest = { showMenuDialog = false },
                title = { Text("Menu") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Theme toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Tema escuro")
                            Switch(
                                checked = currentTheme,
                                onCheckedChange = { ThemeManager.setDarkTheme(it) }
                            )
                        }

                        HorizontalDivider()

                        // App version
                        Column {
                            Text(
                                text = "Versão do App",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "1.0.0",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            authViewModel.logout()
                            navigator.replaceAll(AuthScreen)
                        }
                    ) {
                        Text("Logout")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showMenuDialog = false }
                    ) {
                        Text("Fechar")
                    }
                }
            )
        }

        LaunchedEffect(Unit) {
            homeViewModel.refresh()
        }
    }
}

@Composable
private fun BalanceCard(
    balance: Double,
    onBalanceClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onBalanceClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saldo atual",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.AccountBalanceWallet,
                        contentDescription = "Saldo",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Toque para editar",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            Text(
                text = "R$ ${formatDouble(balance)}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun MenuGrid(navigator: Navigator) {
    val menuItems = listOf(
        MenuItem("Adicionar Gasto", Icons.Rounded.Remove) {
            navigator.push(AddExpenseScreen)
        },
        MenuItem("Adicionar Receita", Icons.Rounded.Add) {
            navigator.push(AddIncomeScreen)
        },
        MenuItem("Ver Gastos", Icons.AutoMirrored.Rounded.List) {
            navigator.push(ViewExpensesScreen)
        },
        MenuItem("Ver Receitas", Icons.Rounded.Receipt) {
            navigator.push(ViewIncomeScreen)
        },
        MenuItem("Relatórios", Icons.Rounded.BarChart) {
            navigator.push(ReportsScreen)
        },
        MenuItem("Categorias", Icons.Rounded.Category) {
            navigator.push(CategoryScreen)
        }
    )

    val rows = menuItems.chunked(2)

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { item ->
                    Box(modifier = Modifier.weight(1f)) {
                        MenuItemCard(item)
                    }
                }
                if (rowItems.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun MenuItemCard(item: MenuItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable { item.onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ExpenseChartCard(expensesByCategory: Map<String, Double>) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (expensesByCategory.isNotEmpty()) {
                SimpleBarChart(expensesByCategory)
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Nenhum gasto registrado ainda",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SimpleBarChart(
    data: Map<String, Double>,
    categories: List<Category> = emptyList()
) {
    val maxValue = data.values.maxOrNull() ?: 1.0

    val categoryColorMap = categories.associate { category ->
        category.name to ColorParser.parseHexColor(category.color)
    }

    val fallbackColors = listOf(
        Color(0xFF6200EE), Color(0xFF03DAC6), Color(0xFFFF6200),
        Color(0xFF4CAF50), Color(0xFFFF5722), Color(0xFF9C27B0),
        Color(0xFF2196F3), Color(0xFFFF9800), Color(0xFF607D8B),
        Color(0xFFE91E63)
    )

    val itemHeight = 28.dp
    val spacing = 8.dp
    val totalHeight = (itemHeight * data.size) + (spacing * (data.size - 1).coerceAtLeast(0))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(totalHeight),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        data.entries.forEachIndexed { index, (categoryName, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = categoryName,
                    modifier = Modifier.width(80.dp),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(20.dp)
                        .background(
                            Color.Gray.copy(alpha = 0.2f),
                            RoundedCornerShape(10.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth((value / maxValue).toFloat())
                            .background(
                                categoryColorMap[categoryName]
                                    ?: fallbackColors[index % fallbackColors.size],
                                RoundedCornerShape(10.dp)
                            )
                    )
                }

                Text(
                    text = "R$ ${formatDouble(value)}",
                    modifier = Modifier.width(60.dp),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun FinancialSummary(
    totalIncome: Double,
    totalExpenses: Double
) {
    val isDark = ThemeManager.getCurrentTheme()
    // Theme-aware colors for income and expense
    val incomeColor = if (isDark) Color(0xFF7CF49A) else Color(0xFF388E3C)
    val expenseColor = if (isDark) Color(0xFFFFB4A9) else Color(0xFFD84315)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Resumo do mês",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Receitas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "R$ ${formatDouble(totalIncome)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = incomeColor
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Gastos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "R$ ${formatDouble(totalExpenses)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = expenseColor
                    )
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Saldo do mês",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "R$ ${formatDouble(totalIncome - totalExpenses)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (totalIncome - totalExpenses >= 0) incomeColor else expenseColor
                )
            }
        }
    }
}