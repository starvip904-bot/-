package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Sale
import com.example.data.model.StoreProfile
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReceiptDialog(
    sale: Sale,
    storeProfile: StoreProfile,
    onDismiss: () -> Unit,
    onPrintClicked: () -> Unit,
    onShareClicked: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale("ar"))
    val dateString = dateFormat.format(Date(sale.timestamp))

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Row with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "إيصال مبيعات الكتروني",
                        style = MaterialTheme.typography.labelMedium,
                        color = TajerTextMuted
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = TajerTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Store Branding
                Text(
                    text = storeProfile.storeName,
                    style = MaterialTheme.typography.titleLarge,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = storeProfile.branchName,
                    style = MaterialTheme.typography.bodySmall,
                    color = TajerTextSecondary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "هاتف: ${storeProfile.phone} | ضريبة: ${storeProfile.taxNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TajerTextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))
                DashedDivider()
                Spacer(modifier = Modifier.height(10.dp))

                // Invoice metadata
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "رقم الفاتورة: ${sale.invoiceNumber}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TajerTextPrimary
                    )
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.bodySmall,
                        color = TajerTextMuted
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "العميل: ${sale.customerName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TajerTextSecondary
                    )
                    if (!sale.customerPhone.isNullOrBlank()) {
                        Text(
                            text = sale.customerPhone,
                            style = MaterialTheme.typography.bodySmall,
                            color = TajerTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                DashedDivider()
                Spacer(modifier = Modifier.height(10.dp))

                // Items Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TajerSurfaceVariant, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "الصنف والمواصفات",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TajerTextPrimary,
                        modifier = Modifier.weight(2f)
                    )
                    Text(
                        text = "الكمية",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TajerTextPrimary,
                        modifier = Modifier.weight(0.8f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "السعر",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TajerTextPrimary,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Items list
                sale.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(2f)) {
                            Text(
                                text = item.product.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TajerTextPrimary
                            )
                            Text(
                                text = "لون: ${item.variant.color} | مقاس: ${item.variant.size}",
                                style = MaterialTheme.typography.bodySmall,
                                color = EmeraldPrimary
                            )
                        }

                        Text(
                            text = "${item.quantity}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TajerTextPrimary,
                            modifier = Modifier.weight(0.8f),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "${String.format("%,.0f", item.totalPrice)} ج.م",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TajerTextPrimary,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                DashedDivider()
                Spacer(modifier = Modifier.height(10.dp))

                // Totals Breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "المجموع الفرعي:", style = MaterialTheme.typography.bodyMedium, color = TajerTextSecondary)
                    Text(text = "${String.format("%,.2f", sale.subtotal)} ج.م", style = MaterialTheme.typography.bodyMedium, color = TajerTextPrimary)
                }

                if (sale.discount > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "الخصم:", style = MaterialTheme.typography.bodyMedium, color = StatusDanger)
                        Text(text = "- ${String.format("%,.2f", sale.discount)} ج.م", style = MaterialTheme.typography.bodyMedium, color = StatusDanger)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "الإجمالي النهائي:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TajerTextPrimary)
                    Text(
                        text = "${String.format("%,.2f", sale.total)} ج.م",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Payment Details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "طريقة الدفع:", style = MaterialTheme.typography.bodySmall, color = TajerTextSecondary)
                    Surface(
                        color = Color(sale.paymentMethod.badgeColorHex).copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = sale.paymentMethod.titleAr,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(sale.paymentMethod.badgeColorHex),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "المبلغ المدفوع:", style = MaterialTheme.typography.bodySmall, color = TajerTextSecondary)
                    Text(text = "${String.format("%,.2f", sale.paidAmount)} ج.م", style = MaterialTheme.typography.bodySmall, color = TajerTextPrimary)
                }

                if (sale.remainingBalance > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "المتبقي آجل على العميل:", style = MaterialTheme.typography.bodySmall, color = StatusWarning, fontWeight = FontWeight.Bold)
                        Text(text = "${String.format("%,.2f", sale.remainingBalance)} ج.م", style = MaterialTheme.typography.bodySmall, color = StatusWarning, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                DashedDivider()
                Spacer(modifier = Modifier.height(10.dp))

                // Store Notice & Policy
                Text(
                    text = storeProfile.returnPolicyNotice,
                    style = MaterialTheme.typography.bodySmall,
                    color = TajerTextMuted,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = storeProfile.thankYouMessage,
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldSecondary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Simulated Barcode block
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(34.dp)
                        .background(TajerSurfaceVariant, RoundedCornerShape(4.dp))
                        .border(1.dp, TajerBorder, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "||| | || |||| | | ||| | ||| ${sale.invoiceNumber} |||| |||",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TajerTextPrimary,
                        letterSpacing = 2.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onPrintClicked,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "طباعة", style = MaterialTheme.typography.labelMedium)
                    }

                    OutlinedButton(
                        onClick = onShareClicked,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "مشاركة", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun DashedDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(TajerBorder)
    )
}
