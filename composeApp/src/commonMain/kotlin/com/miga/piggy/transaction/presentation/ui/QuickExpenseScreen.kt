package com.miga.piggy.transaction.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.miga.piggy.transaction.presentation.viewmodel.AddTransactionViewModel
import com.miga.piggy.utils.datepicker.PlatformDatePicker
import com.miga.piggy.utils.formatters.formatDate
import com.miga.piggy.utils.theme.*
import org.koin.compose.koinInject

class QuickExpenseScreen(
    private val categoryName: String
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: AddTransactionViewModel = koinInject()
        val uiState by viewModel.uiState.collectAsState()

        // Configurações específicas para cada categoria
        val categoryConfig = getCategoryConfig(categoryName)

        LaunchedEffect(Unit) {
            viewModel.setTransactionType(com.miga.piggy.transaction.domain.entity.TransactionType.EXPENSE)
            viewModel.selectCategoryByName(categoryName)
            viewModel.updateDescription("Pagamento de $categoryName")
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = categoryConfig.icon,
                                        contentDescription = categoryName,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        "Adicionar $categoryName",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Card da categoria com gradiente
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(categoryConfig.gradient)
                                .padding(24.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "CATEGORIA",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = categoryName.uppercase(),
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = categoryConfig.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }

                                Card(
                                    modifier = Modifier.size(80.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White.copy(alpha = 0.2f)
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = categoryConfig.icon,
                                            contentDescription = categoryName,
                                            tint = Color.White,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Card de entrada de dados
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Campo de valor
                            OutlinedTextField(
                                value = uiState.amount,
                                onValueChange = viewModel::updateAmount,
                                label = { Text("Valor do $categoryName") },
                                leadingIcon = {
                                    Text(
                                        "R$",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !uiState.isLoading,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(
                                        alpha = 0.5f
                                    )
                                )
                            )

                            // Campo de data
                            var showDatePicker by remember { mutableStateOf(false) }

                            OutlinedTextField(
                                value = formatDate(uiState.selectedDate),
                                onValueChange = { },
                                label = { Text("Data do pagamento") },
                                trailingIcon = {
                                    IconButton(
                                        onClick = { showDatePicker = true }
                                    ) {
                                        Icon(
                                            Icons.Rounded.DateRange,
                                            contentDescription = "Selecionar data",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !uiState.isLoading,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(
                                        alpha = 0.5f
                                    )
                                )
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

                            // Botão salvar com gradiente da categoria
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                onClick = {
                                    if (!uiState.isLoading && uiState.amount.isNotBlank()) {
                                        viewModel.saveTransaction()
                                    }
                                },
                                enabled = !uiState.isLoading && uiState.amount.isNotBlank(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(categoryConfig.gradient),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (uiState.isLoading) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                color = Color.White,
                                                strokeWidth = 2.dp
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Salvando...",
                                                color = Color.White,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    } else {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Save,
                                                contentDescription = "Salvar",
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Salvar $categoryName",
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

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// Configuração para cada categoria
data class CategoryConfig(
    val icon: ImageVector,
    val gradient: Brush,
    val description: String
)

private fun getCategoryConfig(categoryName: String): CategoryConfig {
    return when (categoryName.lowercase()) {
        "água" -> CategoryConfig(
            icon = Icons.Rounded.WaterDrop,
            gradient = PiggyGradients.WaterGradient,
            description = "Conta de água mensal"
        )

        "energia" -> CategoryConfig(
            icon = Icons.Rounded.Lightbulb,
            gradient = PiggyGradients.PowerGradient,
            description = "Conta de energia elétrica"
        )

        "internet" -> CategoryConfig(
            icon = Icons.Rounded.Wifi,
            gradient = PiggyGradients.WifiGradient,
            description = "Plano de internet"
        )

        "telefone" -> CategoryConfig(
            icon = Icons.Rounded.Phone,
            gradient = PiggyGradients.PhoneGradient,
            description = "Conta de telefone/celular"
        )

        else -> CategoryConfig(
            icon = Icons.Rounded.ShoppingCart,
            gradient = PiggyGradients.ExpenseGradient,
            description = "Gasto geral"
        )
    }
}