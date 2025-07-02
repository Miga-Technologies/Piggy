package com.miga.piggy.home.presentation.ui.helper

import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)