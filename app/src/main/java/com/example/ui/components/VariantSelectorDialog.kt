package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Product
import com.example.data.model.ProductVariant
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VariantSelectorDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirmAddToCart: (ProductVariant, Int) -> Unit
) {
    var selectedColor by remember { mutableStateOf(product.colors.firstOrNull() ?: "") }
    var selectedSize by remember { mutableStateOf(product.sizes.firstOrNull() ?: "") }
    var quantity by remember { mutableIntStateOf(1) }

    // Find matching variant
    val matchingVariant = product.variants.find {
        it.color == selectedColor && it.size == selectedSize
    }
    val inStock = matchingVariant?.quantity ?: 0

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TajerTextPrimary
                        )
                        Text(
                            text = "${String.format("%,.0f", product.sellingPrice)} ج.م  |  ${product.category.titleAr}",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إلغاء", tint = TajerTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Colors selection
                Text(
                    text = "اختر اللون:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TajerTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    product.colors.forEach { color ->
                        val isSelected = color == selectedColor
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) EmeraldPrimary else TajerSurfaceVariant)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) EmeraldPrimary else TajerBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedColor = color }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = color,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TajerTextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sizes selection
                Text(
                    text = "اختر المقاس:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TajerTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    product.sizes.forEach { size ->
                        val isSelected = size == selectedSize
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) WarmGold else TajerSurfaceVariant)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) WarmGoldDark else TajerBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedSize = size }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = size,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TajerTextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // In-Stock availability status
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (inStock > 0) EmeraldLight else StatusDanger.copy(alpha = 0.1f),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الكمية المتاحة في المخزن:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TajerTextPrimary
                    )
                    Text(
                        text = if (inStock > 0) "$inStock قطعة متوفرة" else "نفدت الكمية!",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (inStock > 0) EmeraldPrimary else StatusDanger
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quantity selector
                if (inStock > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "الكمية المطلوبة:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = TajerTextPrimary
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledIconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = TajerSurfaceVariant),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "تقليل", tint = TajerTextPrimary)
                            }

                            Text(
                                text = "$quantity",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TajerTextPrimary,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            FilledIconButton(
                                onClick = { if (quantity < inStock) quantity++ },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = EmeraldPrimary),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "زيادة", tint = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (matchingVariant != null) {
                                onConfirmAddToCart(matchingVariant, quantity)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "إضافة إلى الفاتورة (${String.format("%,.0f", product.sellingPrice * quantity)} ج.م)",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onDismiss,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "هذا المقاس أو اللون غير متوفر", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}
