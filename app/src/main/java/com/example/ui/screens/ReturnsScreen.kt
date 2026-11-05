package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItem
import com.example.data.model.ReturnItem
import com.example.data.model.ReturnReason
import com.example.data.model.Sale
import com.example.ui.theme.*
import com.example.ui.viewmodel.TajerViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReturnsScreen(
    viewModel: TajerViewModel,
    modifier: Modifier = Modifier
) {
    val sales by viewModel.sales.collectAsState()
    val returns by viewModel.returns.collectAsState()
    val invoiceQuery by viewModel.returnInvoiceQuery.collectAsState()

    var selectedSale by remember { mutableStateOf<Sale?>(null) }
    var selectedItemForReturn by remember { mutableStateOf<CartItem?>(null) }
    var selectedReason by remember { mutableStateOf(ReturnReason.SIZE_UNSUITABLE) }
    var returnNotes by remember { mutableStateOf("") }

    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale("ar"))

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TajerBackground)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "الاستبدال والمرتجعات",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TajerTextPrimary
        )
        Text(
            text = "بحث عن الفاتورة واسترجاع أو استبدال المقاس مع إعادة البضاعة للمخزن",
            style = MaterialTheme.typography.bodySmall,
            color = TajerTextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Invoice search card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ابحث عن الفاتورة الأصلية:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TajerTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = invoiceQuery,
                        onValueChange = { viewModel.setReturnInvoiceQuery(it) },
                        placeholder = { Text("أدخل رقم الفاتورة مثل INV-2026-001...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = {
                            selectedSale = sales.find {
                                it.invoiceNumber.equals(invoiceQuery.trim(), ignoreCase = true) ||
                                        (it.customerPhone?.contains(invoiceQuery.trim()) == true && invoiceQuery.isNotBlank())
                            }
                            if (selectedSale == null) {
                                viewModel.showMessage("لم يتم العثور على فاتورة بهذا الرقم")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("بحث", color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // If a sale is selected/found
        selectedSale?.let { sale ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "الفاتورة: ${sale.invoiceNumber}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                            Text(
                                text = "العميل: ${sale.customerName} • ${dateFormat.format(Date(sale.timestamp))}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TajerTextSecondary
                            )
                        }

                        IconButton(onClick = { selectedSale = null }) {
                            Icon(Icons.Default.Close, contentDescription = "إلغاء")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "اختر الصنف المراد استرجاعه أو استبداله:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TajerTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    sale.items.forEach { item ->
                        val isSelected = selectedItemForReturn == item
                        Surface(
                            onClick = { selectedItemForReturn = item },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) EmeraldLight else TajerSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) EmeraldPrimary else TajerBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = item.product.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "اللون: ${item.variant.color} | المقاس: ${item.variant.size} (الكمية: ${item.quantity})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TajerTextSecondary
                                    )
                                }
                                Text(
                                    text = "${String.format("%,.0f", item.totalPrice)} ج.م",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                            }
                        }
                    }

                    // Return Reason & Action
                    selectedItemForReturn?.let { itemToReturn ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "سبب الاسترجاع أو الاستبدال:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        ReturnReason.values().forEach { r ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                RadioButton(
                                    selected = selectedReason == r,
                                    onClick = { selectedReason = r },
                                    colors = RadioButtonDefaults.colors(selectedColor = EmeraldPrimary)
                                )
                                Text(text = r.titleAr, style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = returnNotes,
                            onValueChange = { returnNotes = it },
                            label = { Text("ملاحظات إضافية عن حالة القطعة") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val returnItem = ReturnItem(
                                    productId = itemToReturn.product.id,
                                    productName = itemToReturn.product.name,
                                    variantId = itemToReturn.variant.id,
                                    color = itemToReturn.variant.color,
                                    size = itemToReturn.variant.size,
                                    quantity = 1,
                                    refundAmount = itemToReturn.unitPrice,
                                    reason = selectedReason
                                )
                                viewModel.processReturn(
                                    originalInvoiceNumber = sale.invoiceNumber,
                                    itemsToReturn = listOf(returnItem),
                                    notes = returnNotes
                                )
                                selectedSale = null
                                selectedItemForReturn = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusWarning),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "تأكيد الاسترجاع وإعادة القطعة للمخزن (${String.format("%,.0f", itemToReturn.unitPrice)} ج.م)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Returns History
        Text(
            text = "سجل عمليات الاسترجاع السابقة:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TajerTextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (returns.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "لا توجد عمليات مرتجعات مسجلة", color = TajerTextMuted)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(returns, key = { it.id }) { ret ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = StatusWarning.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = ret.returnNumber,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = StatusWarning,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "فاتورة: ${ret.originalInvoiceNumber}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TajerTextSecondary
                                    )
                                }

                                Text(
                                    text = "-${String.format("%,.0f", ret.totalRefund)} ج.م",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusDanger
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            ret.items.forEach { itm ->
                                Text(
                                    text = "• ${itm.productName} (${itm.color} - ${itm.size}) — ${itm.reason.titleAr}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TajerTextPrimary
                                )
                            }

                            if (ret.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "ملاحظة: ${ret.notes}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TajerTextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
