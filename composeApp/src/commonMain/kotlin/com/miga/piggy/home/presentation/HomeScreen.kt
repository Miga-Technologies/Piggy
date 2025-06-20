package com.miga.piggy.home.presentation

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.miga.piggy.auth.presentation.ui.AuthScreen
import com.miga.piggy.auth.presentation.viewmodel.AuthViewModel
import org.koin.compose.koinInject
import kotlin.math.pow
import kotlin.math.round

object HomeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: AuthViewModel = koinInject()
        val uiState by viewModel.uiState.collectAsState()

        val currentBalance = 2847.50
        val expensesByCategory = mapOf(
            "Alimentação" to 850.0,
            "Transporte" to 320.0,
            "Lazer" to 180.0,
            "Saúde" to 250.0,
            "Outros" to 120.0
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Olá, ${uiState.user?.displayName?.split(" ")?.first() ?: "Usuário"}!")
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                viewModel.logout()
                                navigator.replaceAll(AuthScreen)
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Rounded.Logout,
                                contentDescription = "Logout"
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Card do Saldo
                    item {
                        BalanceCard(currentBalance)
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
                        MenuGrid()
                    }

                    // Gráfico de Gastos
                    item {
                        Text(
                            text = "Gastos por categoria",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    item {
                        ExpenseChartCard(expensesByCategory)
                    }

                    item {
                        FinancialSummary()
                    }
                }
            }
        )
    }
}

@Composable
private fun BalanceCard(balance: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
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
                Icon(
                    Icons.Rounded.AccountBalanceWallet,
                    contentDescription = "Saldo",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
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
private fun MenuGrid() {
    val menuItems = listOf(
        MenuItem("Adicionar Gasto", Icons.Rounded.Remove),
        MenuItem("Adicionar Receita", Icons.Rounded.Add),
        MenuItem("Ver Gastos", Icons.AutoMirrored.Rounded.List),
        MenuItem("Ver Receitas", Icons.Rounded.Receipt),
        MenuItem("Relatórios", Icons.Rounded.BarChart),
        MenuItem("Categorias", Icons.Rounded.Category)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.height(200.dp)
    ) {
        items(menuItems.size) { item ->
            MenuItemCard(menuItems[item])
        }
    }
}

@Composable
private fun MenuItemCard(item: MenuItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable {
                // TODO: Implementar navegação quando as telas existirem
            },
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
private fun ExpenseChartCard(gastosPorCategoria: Map<String, Double>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (gastosPorCategoria.isNotEmpty()) {
                // Gráfico simples usando Canvas (sem dependências externas)
                SimpleBarChart(gastosPorCategoria)
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
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
private fun SimpleBarChart(data: Map<String, Double>) {
    val maxValue = data.values.maxOrNull() ?: 1.0
    val colors = listOf(
        Color(0xFF6200EE),
        Color(0xFF03DAC6),
        Color(0xFFFF6200),
        Color(0xFF4CAF50),
        Color(0xFFFF5722)
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        data.entries.forEachIndexed { index, (category, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category,
                    modifier = Modifier.width(80.dp),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
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
                                colors[index % colors.size],
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
private fun FinancialSummary() {
    val totalGastos = 1720.0
    val totalReceitas = 4567.50

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
                        text = "R$ ${formatDouble(totalReceitas)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4CAF50)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Gastos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "R$ ${formatDouble(totalGastos)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFFF5722)
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
                    text = "R$ ${formatDouble(totalReceitas - totalGastos)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (totalReceitas - totalGastos >= 0)
                        Color(0xFF4CAF50) else Color(0xFFFF5722)
                )
            }
        }
    }
}

fun formatDouble(value: Double, decimals: Int = 2): String {
    val factor = 10.0.pow(decimals)
    val rounded = round(value * factor) / factor
    return buildString {
        append(rounded.toString())
        val parts = rounded.toString().split(".")
        if (parts.size == 1) {
            append(".")
            repeat(decimals) { append("0") }
        } else if (parts[1].length < decimals) {
            repeat(decimals - parts[1].length) { append("0") }
        }
    }
}

data class MenuItem(
    val title: String,
    val icon: ImageVector
)