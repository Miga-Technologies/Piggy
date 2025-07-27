package com.miga.piggy.category.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.miga.piggy.category.presentation.viewmodel.CategoryViewModel
import com.miga.piggy.home.domain.entity.Category
import com.miga.piggy.transaction.domain.entity.TransactionType
import com.miga.piggy.utils.parsers.ColorParser
import com.miga.piggy.utils.theme.*
import org.koin.compose.koinInject

object CategoryScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: CategoryViewModel = koinInject()
        val uiState by viewModel.uiState.collectAsState()
        val formState by viewModel.formState.collectAsState()

        var isSelectionMode by remember { mutableStateOf(false) }
        var selectedCategories by remember { mutableStateOf(setOf<String>()) }

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
                                        Icons.Rounded.Category,
                                        contentDescription = "Categorias",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = if (isSelectionMode) {
                                            "${selectedCategories.size} selecionada${if (selectedCategories.size != 1) "s" else ""}"
                                        } else {
                                            "Categorias"
                                        },
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
                            actions = {
                                if (isSelectionMode) {
                                    // Botão de selecionar todos
                                    val customCategories =
                                        uiState.categories.filter { !it.isDefault }
                                    val allSelected =
                                        customCategories.all { selectedCategories.contains(it.id) }

                                    IconButton(
                                        onClick = {
                                            selectedCategories = if (allSelected) {
                                                emptySet()
                                            } else {
                                                customCategories.map { it.id }.toSet()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            if (allSelected) Icons.Rounded.Deselect else Icons.Rounded.SelectAll,
                                            contentDescription = if (allSelected) "Desselecionar todos" else "Selecionar todos",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    // Botão de deletar selecionados
                                    if (selectedCategories.isNotEmpty()) {
                                        IconButton(
                                            onClick = {
                                                selectedCategories.forEach { categoryId ->
                                                    val category =
                                                        uiState.categories.find { it.id == categoryId }
                                                    category?.let { viewModel.deleteCategory(it) }
                                                }
                                                selectedCategories = emptySet()
                                                isSelectionMode = false
                                            }
                                        ) {
                                            Icon(
                                                Icons.Rounded.Delete,
                                                contentDescription = "Deletar selecionados",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }

                                    // Botão de cancelar seleção
                                    IconButton(
                                        onClick = {
                                            isSelectionMode = false
                                            selectedCategories = emptySet()
                                        }
                                    ) {
                                        Icon(
                                            Icons.Rounded.Close,
                                            contentDescription = "Cancelar seleção",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                } else {
                                    // Botão para entrar no modo seleção
                                    val customCategories =
                                        uiState.categories.filter { !it.isDefault }
                                    if (customCategories.isNotEmpty()) {
                                        IconButton(
                                            onClick = { isSelectionMode = true }
                                        ) {
                                            Icon(
                                                Icons.Rounded.Checklist,
                                                contentDescription = "Modo seleção",
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent
                            )
                        )
                    }
                },
                floatingActionButton = {
                    Card(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        onClick = { viewModel.showAddDialog() },
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.secondary
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.Add,
                                contentDescription = "Adicionar categoria",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            ) { paddingValues ->
                if (uiState.isLoading && uiState.categories.isEmpty()) {
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
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Seção de categorias padrão - sempre mostrar
                        item {
                            Text(
                                text = "Categorias Padrão",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        val defaultCategories = uiState.categories.filter { it.isDefault }
                        if (defaultCategories.isNotEmpty()) {
                            val rows = defaultCategories.chunked(3)
                            items(rows) { rowCategories ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Use weight for proper grid layout; add empty spacers for missing items
                                    for (i in 0 until 3) {
                                        if (i < rowCategories.size) {
                                            DefaultCategoryItem(
                                                category = rowCategories[i],
                                                modifier = Modifier.weight(1f)
                                            )
                                        } else {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }

                        // Seção de categorias personalizadas
                        val customCategories = uiState.categories.filter { !it.isDefault }
                        item {
                            Text(
                                text = "Suas Categorias",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        if (customCategories.isNotEmpty()) {
                            val rows = customCategories.chunked(3)
                            items(rows) { rowCategories ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowCategories.forEach { category ->
                                        CustomCategoryItem(
                                            category = category,
                                            onEdit = { viewModel.showEditDialog(category) },
                                            onDelete = { viewModel.deleteCategory(category) },
                                            modifier = Modifier.weight(1f),
                                            isSelected = selectedCategories.contains(category.id),
                                            onSelect = {
                                                if (isSelectionMode) {
                                                    if (selectedCategories.contains(category.id)) {
                                                        selectedCategories =
                                                            selectedCategories.minus(category.id)
                                                    } else {
                                                        selectedCategories =
                                                            selectedCategories.plus(category.id)
                                                    }
                                                }
                                            },
                                            isSelectionMode = isSelectionMode,
                                            onEnterSelectionMode = {
                                                isSelectionMode = true
                                                selectedCategories = setOf(category.id)
                                            }
                                        )
                                    }
                                    // Fill empty spaces
                                    repeat(3 - rowCategories.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        } else {
                            // Placeholder quando não há categorias personalizadas
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface.copy(
                                            alpha = 0.5f
                                        )
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            Icons.Rounded.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(32.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.6f
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Nenhuma categoria personalizada",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.8f
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "Toque no + para criar sua primeira categoria",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.6f
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (uiState.showAddDialog) {
                ModernCategoryDialog(
                    title = if (uiState.editingCategory != null) "Editar Categoria" else "Nova Categoria",
                    formState = formState,
                    onNameChange = viewModel::updateName,
                    onColorChange = viewModel::updateColor,
                    onTypeChange = viewModel::updateType,
                    onSave = viewModel::saveCategory,
                    onDismiss = viewModel::hideDialog,
                    isLoading = uiState.isLoading
                )
            }

            uiState.error?.let { error ->
                LaunchedEffect(error) {
                    kotlinx.coroutines.delay(3000)
                    viewModel.clearError()
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.Error,
                            contentDescription = "Erro",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DefaultCategoryItem(
    category: Category,
    modifier: Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Padrão",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CustomCategoryItem(
    category: Category,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier,
    isSelected: Boolean,
    onSelect: () -> Unit,
    isSelectionMode: Boolean,
    onEnterSelectionMode: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    val color = try {
        ColorParser.parseHexColor(category.color)
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }
    Box(
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .aspectRatio(1f)
                .padding(4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                color,
                                color.copy(alpha = 0.7f)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .combinedClickable(
                            onClick = {
                                if (isSelectionMode) {
                                    onSelect()
                                } else {
                                    showMenu = true
                                }
                            },
                            onLongClick = {
                                if (!isSelectionMode) {
                                    onEnterSelectionMode()
                                } else {
                                    onSelect()
                                }
                            }
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Personalizada",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (isSelected) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(24.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    Icons.Rounded.Check,
                    contentDescription = "Selecionado",
                    tint = Color.White,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                )
            }
        }

        // Menu dropdown
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Editar")
                    }
                },
                onClick = {
                    showMenu = false
                    onEdit()
                }
            )
            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.Delete,
                            contentDescription = "Deletar",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Deletar")
                    }
                },
                onClick = {
                    showMenu = false
                    onDelete()
                }
            )
        }
    }
}

@Composable
private fun ModernCategoryDialog(
    title: String,
    formState: com.miga.piggy.category.presentation.viewmodel.CategoryFormState,
    onNameChange: (String) -> Unit,
    onColorChange: (String) -> Unit,
    onTypeChange: (TransactionType) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean
) {
    var localName by remember { mutableStateOf(formState.name) }

    LaunchedEffect(formState.name) {
        if (localName != formState.name && formState.name.isEmpty()) {
            localName = formState.name
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                OutlinedTextField(
                    value = localName,
                    onValueChange = { newValue ->
                        localName = newValue
                        onNameChange(newValue)
                    },
                    label = { Text("Nome da categoria") },
                    leadingIcon = {
                        Icon(
                            Icons.Rounded.Label,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    isError = formState.nameError != null,
                    supportingText = formState.nameError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.Category,
                            contentDescription = "Tipo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tipo de categoria",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TransactionType.entries.forEach { type ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                onClick = {
                                    if (!isLoading) {
                                        onTypeChange(type)
                                    }
                                },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (formState.type == type)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = when (type) {
                                            TransactionType.EXPENSE -> "Gasto"
                                            TransactionType.INCOME -> "Receita"
                                        },
                                        color = if (formState.type == type)
                                            Color.White
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.Palette,
                            contentDescription = "Cor",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Escolha uma cor",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    ModernColorPicker(
                        selectedColor = formState.color,
                        onColorSelected = onColorChange,
                        enabled = !isLoading
                    )
                }
            }
        },
        confirmButton = {
            Card(
                onClick = onSave,
                enabled = !isLoading && localName.isNotBlank(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Salvar",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
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
private fun ModernColorPicker(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    enabled: Boolean = true
) {
    val colors = listOf(
        "#6200EE", "#03DAC6", "#FF6200", "#4CAF50", "#FF5722",
        "#9C27B0", "#2196F3", "#FF9800", "#607D8B", "#E91E63",
        "#795548", "#009688", "#8BC34A", "#CDDC39", "#FFC107"
    )

    LazyColumn(
        modifier = Modifier.height(140.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val rows = colors.chunked(5)
        items(rows) { rowColors ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowColors.forEach { color ->
                    Card(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .let {
                                if (enabled) {
                                    it.selectable(
                                        selected = color == selectedColor,
                                        onClick = { onColorSelected(color) }
                                    )
                                } else it
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = ColorParser.parseHexColor(color)
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (color == selectedColor) 6.dp else 2.dp
                        )
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (color == selectedColor) {
                                Icon(
                                    Icons.Rounded.Check,
                                    contentDescription = "Selecionado",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}