package com.miga.piggy.transaction.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.miga.piggy.utils.theme.*
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

        var filterDialogOpen by remember { mutableStateOf(false) }
        var selectedCategory by remember { mutableStateOf<String?>(null) }

        // Selection mode - multiple selection of transactions
        var selectionMode by remember { mutableStateOf(false) }
        var selectedTransactions by remember { mutableStateOf(setOf<String>()) }

        val availableCategories = remember(uiState.transactions) {
            uiState.transactions.map { it.category }.distinct().sorted()
        }

        LaunchedEffect(authState.user?.id) {
            authState.user?.id?.let { userId ->
                viewModel.loadTransactions(userId, TransactionType.INCOME)
            }
        }

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
                                if (selectionMode) {
                                    Text(
                                        "${selectedTransactions.size} selecionada${if (selectedTransactions.size == 1) "" else "s"}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Text(
                                        "Receitas",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            navigationIcon = {
                                if (selectionMode) {
                                    IconButton(onClick = {
                                        selectionMode = false
                                        selectedTransactions = emptySet()
                                    }) {
                                        Icon(
                                            Icons.Rounded.Close,
                                            "Sair da seleção",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                } else {
                                    IconButton(onClick = { navigator.pop() }) {
                                        Icon(
                                            Icons.AutoMirrored.Rounded.ArrowBack,
                                            "Voltar",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            },
                            actions = {
                                if (selectionMode) {
                                    if (selectedTransactions.isNotEmpty()) {
                                        IconButton(onClick = {
                                            authState.user?.id?.let { userId ->
                                                selectedTransactions.forEach { transactionId ->
                                                    viewModel.deleteTransaction(
                                                        userId,
                                                        transactionId,
                                                        TransactionType.INCOME
                                                    )
                                                }
                                            }
                                            selectionMode = false
                                            selectedTransactions = emptySet()
                                        }) {
                                            Icon(
                                                Icons.Rounded.Delete,
                                                "Deletar selecionadas",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                } else {
                                    if (uiState.transactions.isNotEmpty()) {
                                        IconButton(onClick = { selectionMode = true }) {
                                            Icon(
                                                Icons.Rounded.Checklist,
                                                "Modo seleção",
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                    IconButton(onClick = { filterDialogOpen = true }) {
                                        Icon(
                                            Icons.Rounded.FilterList,
                                            "Filtros",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent
                            )
                        )
                    }
                }
            ) { paddingValues ->
                val filteredTransactions = uiState.transactions.filter { transaction ->
                    selectedCategory == null || transaction.category == selectedCategory
                }
                val filteredTotal = filteredTransactions.sumOf { it.amount }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(PiggyGradients.IncomeGradient)
                                    .padding(20.dp)
                            ) {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Rounded.TrendingUp,
                                            contentDescription = "Receitas",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Total de Receitas",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.White,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "R$ ${formatDouble(filteredTotal)}",
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    if (selectedCategory != null) {
                                        Text(
                                            "Categoria: $selectedCategory",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (uiState.isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    } else if (filteredTransactions.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Rounded.AccountBalanceWallet,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        if (selectedCategory != null)
                                            "Nenhuma receita encontrada\npara esta categoria"
                                        else "Nenhuma receita encontrada",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        items(filteredTransactions) { transaction ->
                            TransactionItemSelectable(
                                transaction = transaction,
                                selectionMode = selectionMode,
                                isSelected = selectedTransactions.contains(transaction.id),
                                onLongPress = {
                                    selectionMode = true
                                    selectedTransactions = selectedTransactions + transaction.id
                                },
                                onClick = {
                                    if (selectionMode) {
                                        selectedTransactions =
                                            if (selectedTransactions.contains(transaction.id)) {
                                                selectedTransactions - transaction.id
                                            } else {
                                                selectedTransactions + transaction.id
                                            }
                                        // Exit selection mode if none left selected
                                        if (selectedTransactions.isEmpty()) {
                                            selectionMode = false
                                        }
                                    }
                                },
                                onDelete = {
                                    authState.user?.id?.let { userId ->
                                        viewModel.deleteTransaction(
                                            userId,
                                            transaction.id,
                                            TransactionType.INCOME
                                        )
                                    }
                                },
                                showDelete = !selectionMode // Only show "delete" for single if not in selection mode
                            )
                        }
                    }
                }
            }

            if (filterDialogOpen) {
                FilterDialog(
                    title = "Filtrar por Categoria",
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
        title = {
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Card(
                                onClick = { tempCategory = null },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (tempCategory == null)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = tempCategory == null,
                                        onClick = { tempCategory = null }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Todas as categorias",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        items(availableCategories) { category ->
                            Card(
                                onClick = { tempCategory = category },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (tempCategory == category)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = tempCategory == category,
                                        onClick = { tempCategory = category }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = getIncomeIcon(category),
                                        contentDescription = category,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = category,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Card(
                onClick = {
                    onCategorySelected(tempCategory)
                    onDismiss()
                },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Aplicar",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Cancelar",
                    fontWeight = FontWeight.Medium
                )
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun TransactionItemSelectable(
    transaction: Transaction,
    selectionMode: Boolean,
    isSelected: Boolean,
    onLongPress: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    showDelete: Boolean = true
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    onClick()
                },
                onLongClick = {
                    onLongPress()
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor =
                if (selectionMode && isSelected)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                else
                    MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone com fundo colorido
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        PiggyColors.Green500.copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selectionMode) {
                    // Show checkbox for selection instead of plain icon
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { _ -> onClick() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                } else {
                    Icon(
                        imageVector = getIncomeIcon(transaction.category),
                        contentDescription = transaction.category,
                        tint = PiggyColors.Green500,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Conteúdo da transação
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${transaction.description} • ${formatDate(transaction.date)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Valor
            Text(
                text = "+R$ ${formatDouble(transaction.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PiggyColors.Green500
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Botão de deletar visível apenas quando NÃO está em modo seleção
            if (showDelete && !selectionMode) {
                IconButton(
                    onClick = { showDeleteDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Deletar transação",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // Dialog de confirmação
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Excluir Transação",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    "Tem certeza que deseja excluir esta transação? Esta ação não pode ser desfeita.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Card(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Excluir",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onError,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(
                        "Cancelar",
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

// Função para mapear categorias de receita para ícones
private fun getIncomeIcon(category: String) = when (category.lowercase()) {
    "salário" -> Icons.Rounded.AttachMoney
    "investimento" -> Icons.Rounded.TrendingUp
    "vendas" -> Icons.Rounded.Sell
    "bonus" -> Icons.Rounded.EmojiEvents
    else -> Icons.Rounded.AccountBalanceWallet
}