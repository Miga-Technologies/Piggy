package com.miga.piggy.utils.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Card com gradiente para mostrar valores de receita/gasto
 */
@Composable
fun GradientValueCard(
    title: String,
    value: String,
    gradient: Brush,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = value,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Card de cartão de crédito com gradiente
 */
@Composable
fun CreditCardCard(
    holderName: String,
    cardNumber: String,
    balance: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp) // Reduzir altura de 200dp para 180dp
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF667eea),
                            Color(0xFF764ba2),
                            Color(0xFF667eea)
                        ),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
                    )
                )
        ) {
            // Background pattern overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            radius = 500f,
                            center = androidx.compose.ui.geometry.Offset(300f, -100f)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp) // Reduzir padding de 24dp para 16dp
            ) {
                // Header row with chip and contactless
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Chip do cartão
                    Card(
                        modifier = Modifier.size(40.dp, 30.dp), // Reduzir tamanho do chip
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFD700).copy(alpha = 0.9f)
                        ),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFFFD700),
                                            Color(0xFFFFA500)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            // Linhas do chip
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                repeat(3) {
                                    Box(
                                        modifier = Modifier
                                            .width(20.dp) // Reduzir tamanho das linhas
                                            .height(1.dp)
                                            .background(
                                                Color(0xFF8B6914),
                                                RoundedCornerShape(1.dp)
                                            )
                                    )
                                    if (it < 2) Spacer(modifier = Modifier.height(2.dp))
                                }
                            }
                        }
                    }

                    // Símbolo contactless
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Wifi,
                            contentDescription = "Contactless",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier
                                .size(20.dp) // Reduzir tamanho do ícone
                                .rotate(90f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Piggy",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp)) // Reduzir espaçamento

                // Número do cartão
                Text(
                    text = cardNumber,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 18.sp, // Reduzir fonte
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp
                    ),
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.weight(1f))

                // Row inferior com nome e validade/bandeira
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(
                        modifier = Modifier.weight(1f) // Adicionar peso para controlar largura
                    ) {
                        Text(
                            text = "PORTADOR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = holderName.uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 12.sp, // Reduzir fonte
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(6.dp)) // Reduzir espaçamento

                        Text(
                            text = "SALDO DISPONÍVEL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = balance,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 16.sp, // Reduzir fonte
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp)) // Adicionar espaço entre colunas

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "VÁLIDO ATÉ",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "12/28",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 12.sp, // Reduzir fonte
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color.White,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(6.dp)) // Reduzir espaçamento

                        // Logo da "bandeira" do cartão
                        Card(
                            modifier = Modifier.size(45.dp, 25.dp), // Reduzir tamanho do logo
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.95f)
                            ),
                            shape = RoundedCornerShape(4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "PIGGY",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 7.sp, // Reduzir fonte
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = Color(0xFF667eea)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Botão de categoria com gradiente
 */
@Composable
fun CategoryButton(
    title: String,
    icon: ImageVector,
    gradient: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .size(80.dp)
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Item de transação com ícone colorido
 */
@Composable
fun TransactionItem(
    title: String,
    subtitle: String,
    amount: String,
    icon: ImageVector,
    iconBackgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone com fundo colorido
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        iconBackgroundColor,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Título e subtítulo
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Valor
            Text(
                text = amount,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (amount.startsWith("-")) PiggyColors.Red500 else PiggyColors.Green500
            )
        }
    }
}