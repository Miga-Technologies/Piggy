package com.miga.piggy.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.miga.piggy.transaction.presentation.ui.QuickExpenseScreen
import com.miga.piggy.transaction.presentation.ui.ViewExpensesScreen
import com.miga.piggy.transaction.presentation.ui.ViewIncomeScreen
import com.miga.piggy.utils.formatters.formatDouble
import com.miga.piggy.utils.parsers.ColorParser
import com.miga.piggy.utils.theme.*
import com.miga.piggy.ThemeManager
import com.miga.piggy.transaction.domain.entity.TransactionType
import dev.materii.pullrefresh.PullRefreshIndicator
import dev.materii.pullrefresh.PullRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState
import org.koin.compose.koinInject
import com.miga.piggy.utils.formatters.formatDate
import com.miga.piggy.utils.ui.ProfileImagePicker
import com.miga.piggy.utils.ImagePickerWithPermissions
import com.miga.piggy.utils.ui.ImageSelectionDialog

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
        val currentTheme = ThemeManager.getCurrentTheme()
        var showImagePicker by remember { mutableStateOf(false) }

        LaunchedEffect(authUiState.user) {
            if (authUiState.user == null) {
                navigator.replaceAll(AuthScreen)
            }
        }

        val pullRefreshState = rememberPullRefreshState(
            refreshing = isRefreshing,
            onRefresh = { homeViewModel.refresh() }
        )

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
            PullRefreshLayout(
                state = pullRefreshState,
                indicator = {
                    PullRefreshIndicator(
                        state = pullRefreshState,
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                }
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
                            Column(
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Olá,",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = authUiState.user?.displayName?.split(" ")
                                                ?.first() ?: "Usuário",
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        ProfileImagePicker(
                                            name = authUiState.user?.displayName ?: "Usuário",
                                            imageUrl = authUiState.user?.photoUrl,
                                            onImageClick = { showImagePicker = true }
                                        )

                                        IconButton(
                                            onClick = { showMenuDialog = true }
                                        ) {
                                            Icon(
                                                Icons.Rounded.MoreVert,
                                                contentDescription = "Menu",
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    GradientValueCard(
                                        title = "Receitas",
                                        value = "R$ ${formatDouble(homeUiState.monthlyIncome)}",
                                        gradient = PiggyGradients.IncomeGradient,
                                        icon = Icons.AutoMirrored.Rounded.TrendingUp,
                                        modifier = Modifier.weight(1f)
                                    )

                                    GradientValueCard(
                                        title = "Gastos",
                                        value = "R$ ${formatDouble(homeUiState.monthlyExpenses)}",
                                        gradient = PiggyGradients.ExpenseGradient,
                                        icon = Icons.AutoMirrored.Rounded.TrendingDown,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    },
                    content = { paddingValues ->
                        if (homeUiState.isLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                item {
                                    CreditCardCard(
                                        holderName = authUiState.user?.displayName ?: "Usuário",
                                        cardNumber = "•••• •••• •••• 1234",
                                        balance = "R$ ${formatDouble(homeUiState.balance)}",
                                        modifier = Modifier.clickable {
                                            navigator.push(
                                                EditBalanceScreen
                                            )
                                        }
                                    )
                                }

                                // Botões principais de adicionar
                                item {
                                    Text(
                                        text = "Adicionar Transação",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }

                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Botão Adicionar Gasto
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(100.dp)
                                                .clip(RoundedCornerShape(20.dp)),
                                            onClick = { navigator.push(AddExpenseScreen) },
                                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(PiggyGradients.ExpenseGradient),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Rounded.Remove,
                                                        contentDescription = "Adicionar Gasto",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(28.dp)
                                                    )
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text(
                                                        text = "Adicionar",
                                                        color = Color.White,
                                                        style = MaterialTheme.typography.titleSmall,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                    Text(
                                                        text = "Gasto",
                                                        color = Color.White,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }
                                        }

                                        // Botão Adicionar Receita
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(100.dp)
                                                .clip(RoundedCornerShape(20.dp)),
                                            onClick = { navigator.push(AddIncomeScreen) },
                                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(PiggyGradients.IncomeGradient),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Rounded.Add,
                                                        contentDescription = "Adicionar Receita",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(28.dp)
                                                    )
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text(
                                                        text = "Adicionar",
                                                        color = Color.White,
                                                        style = MaterialTheme.typography.titleSmall,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                    Text(
                                                        text = "Receita",
                                                        color = Color.White,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Botão Adicionar Categoria
                                item {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp)
                                            .clip(RoundedCornerShape(16.dp)),
                                        onClick = { navigator.push(CategoryScreen) },
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                        ),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Category,
                                                contentDescription = "Adicionar Categoria",
                                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = "Adicionar Categoria",
                                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }

                                // Seção de Ações Rápidas
                                item {
                                    Text(
                                        text = "Ações Rápidas",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }

                                item {
                                    QuickActionsGrid(navigator)
                                }

                                // Lista de transações mais recentes
                                if (homeUiState.recentTransactions.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = "Transações Recentes",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }

                                    item {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            homeUiState.recentTransactions.take(5)
                                                .forEach { transaction ->
                                                    TransactionItem(
                                                        title = transaction.category,
                                                        subtitle = "${transaction.description} • ${
                                                            formatDate(
                                                                transaction.date
                                                            )
                                                        }",
                                                        amount = if (transaction.type == TransactionType.INCOME)
                                                            "+R$ ${formatDouble(transaction.amount)}"
                                                        else
                                                            "-R$ ${formatDouble(transaction.amount)}",
                                                        icon = getTransactionIcon(
                                                            transaction.category,
                                                            transaction.type
                                                        ),
                                                        iconBackgroundColor = if (transaction.type == TransactionType.INCOME)
                                                            PiggyColors.Green500 else PiggyColors.Red500
                                                    )
                                                }
                                        }
                                    }
                                } else {
                                    // Mostrar placeholder quando não há transações
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
                                                    .padding(24.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Icon(
                                                    Icons.Rounded.Receipt,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(48.dp),
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                        alpha = 0.6f
                                                    )
                                                )
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Text(
                                                    text = "Nenhuma transação ainda",
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    text = "Comece adicionando seus gastos e receitas",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                        alpha = 0.7f
                                                    ),
                                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }

                                if (homeUiState.expensesByCategory.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = "Gastos por Categoria",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }

                                    item {
                                        ExpenseChartCard(homeUiState.expensesByCategory)
                                    }
                                }
                            }
                        }

                        homeUiState.error?.let { error ->
                            LaunchedEffect(error) {
                                kotlinx.coroutines.delay(3000)
                                homeViewModel.clearError()
                            }
                        }
                    }
                )
            }
        }

        if (showMenuDialog) {
            AlertDialog(
                onDismissRequest = { showMenuDialog = false },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.Settings,
                            contentDescription = "Configurações",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Configurações",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Card para informações do usuário
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ProfileImagePicker(
                                    name = authUiState.user?.displayName ?: "Usuário",
                                    imageUrl = authUiState.user?.photoUrl,
                                    onImageClick = { showImagePicker = true },
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = authUiState.user?.displayName ?: "Usuário",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = authUiState.user?.email ?: "email@exemplo.com",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Card para o toggle de tema
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        if (currentTheme) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                                        contentDescription = "Tema",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            "Tema escuro",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            if (currentTheme) "Ativo" else "Inativo",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Switch(
                                    checked = currentTheme,
                                    onCheckedChange = { ThemeManager.setDarkTheme(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            }
                        }

                        // Card para informações do app
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Rounded.Info,
                                    contentDescription = "Informações",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Piggy - Sua carteira inteligente",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Versão 1.0.0",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Card(
                        onClick = {
                            authViewModel.logout()
                            navigator.replaceAll(AuthScreen)
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.AutoMirrored.Rounded.Logout,
                                contentDescription = "Logout",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Sair",
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showMenuDialog = false }
                    ) {
                        Text(
                            "Fechar",
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.surface
            )
        }

        if (showImagePicker) {
            ImagePickerWithPermissions(
                onImageSelected = { imageData ->
                    imageData?.let { data ->
                        authViewModel.updateProfileImage(data)
                    }
                    showImagePicker = false
                },
                onPermissionDenied = {
                    // TODO: Mostrar mensagem de erro sobre permissão
                    showImagePicker = false
                }
            ) { pickFromGallery, pickFromCamera ->
                ImageSelectionDialog(
                    onDismissRequest = { showImagePicker = false },
                    onGalleryClick = {
                        pickFromGallery()
                    },
                    onCameraClick = {
                        pickFromCamera()
                    }
                )
            }
        }

        LaunchedEffect(Unit) {
            homeViewModel.refresh()
        }
    }
}

@Composable
private fun QuickActionsGrid(navigator: Navigator) {
    // Usar as categorias padrão dos utilitários
    val quickActions = listOf(
        Triple("Água", PiggyGradients.WaterGradient, "Água"),
        Triple("Energia", PiggyGradients.PowerGradient, "Energia"),
        Triple("Internet", PiggyGradients.WifiGradient, "Internet"),
        Triple("Telefone", PiggyGradients.PhoneGradient, "Telefone")
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        quickActions.forEach { (title, gradient, categoryName) ->
            CategoryButton(
                title = title,
                icon = when (title) {
                    "Água" -> Icons.Rounded.WaterDrop
                    "Energia" -> Icons.Rounded.Lightbulb
                    "Internet" -> Icons.Rounded.Wifi
                    "Telefone" -> Icons.Rounded.Phone
                    else -> Icons.Rounded.ShoppingCart
                },
                gradient = gradient,
                onClick = {
                    navigator.push(
                        QuickExpenseScreen(
                            categoryName = categoryName
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .height(80.dp)
                .clip(RoundedCornerShape(16.dp)),
            onClick = { navigator.push(ViewExpensesScreen) },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.List,
                    contentDescription = "Ver Gastos",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ver Gastos",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .height(80.dp)
                .clip(RoundedCornerShape(16.dp)),
            onClick = { navigator.push(ViewIncomeScreen) },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Receipt,
                    contentDescription = "Ver Receitas",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ver Receitas",
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .height(80.dp)
                .clip(RoundedCornerShape(16.dp)),
            onClick = { navigator.push(ReportsScreen) },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.BarChart,
                    contentDescription = "Relatórios",
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Relatórios",
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ExpenseChartCard(expensesByCategory: Map<String, Double>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            if (expensesByCategory.isNotEmpty()) {
                SimpleBarChart(expensesByCategory)
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
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
        PiggyColors.Purple500, PiggyColors.Pink500, PiggyColors.Blue500,
        PiggyColors.Green500, PiggyColors.Orange500, PiggyColors.Red500
    )

    val itemHeight = 32.dp
    val spacing = 12.dp
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
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth((value / maxValue).toFloat())
                            .background(
                                categoryColorMap[categoryName]
                                    ?: fallbackColors[index % fallbackColors.size],
                                RoundedCornerShape(12.dp)
                            )
                    )
                }

                Text(
                    text = "R$ ${formatDouble(value)}",
                    modifier = Modifier.width(80.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// Função para mapear transações para ícones
private fun getTransactionIcon(category: String, type: TransactionType) =
    if (type == TransactionType.INCOME) getIncomeIcon(category) else getExpenseIcon(category)

private fun getExpenseIcon(category: String) = when (category.lowercase()) {
    "água" -> Icons.Rounded.WaterDrop
    "energia" -> Icons.Rounded.Lightbulb
    "internet" -> Icons.Rounded.Wifi
    "telefone" -> Icons.Rounded.Phone
    "alimentação" -> Icons.Rounded.Restaurant
    "transporte" -> Icons.Rounded.DirectionsCar
    else -> Icons.Rounded.ShoppingCart
}

private fun getIncomeIcon(category: String) = when (category.lowercase()) {
    "salário" -> Icons.Rounded.AttachMoney
    "freelance" -> Icons.Rounded.Work
    "investimento" -> Icons.Rounded.TrendingUp
    "vendas" -> Icons.Rounded.Sell
    "bonus" -> Icons.Rounded.EmojiEvents
    else -> Icons.Rounded.AccountBalanceWallet
}