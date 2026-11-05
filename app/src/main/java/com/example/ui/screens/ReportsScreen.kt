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
import com.example.data.model.PaymentMethod
import com.example.ui.theme.*
import com.example.ui.viewmodel.TajerViewModel

@Composable
fun ReportsScreen(
    viewModel: TajerViewModel,
    modifier: Modifier = Modifier
) {
    val sales by viewModel.sales.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val products by viewModel.products.collectAsState()

    // Calculations
    val totalRevenue = sales.sumOf { it.total }

    // Cost of goods sold
    val cogs = sales.sumOf { s ->
        s.items.sumOf { item -> item.product.purchasePrice * item.quantity }
    }

    val grossProfit = totalRevenue - cogs
    val totalExpenses = expenses.sumOf { it.amount }
    val netProfit = grossProfit - totalExpenses

    // Payment methods distribution
    val paymentBreakdown = remember(sales) {
        PaymentMethod.values().map { method ->
            val methodSales = sales.filter { it.paymentMethod == method }
            val totalAmt = methodSales.sumOf { it.total }
            Triple(method, methodSales.size, totalAmt)
        }.filter { it.third > 0 }
    }

    // Top Selling Products
    val topSelling = remember(sales) {
        val countMap = mutableMapOf<String, Pair<String, Int>>() // id to (name, quantity)
        sales.forEach { s ->
            s.items.forEach { item ->
                val prev = countMap[item.product.id]
                val currentQty = (prev?.second ?: 0) + item.quantity
                countMap[item.product.id] = Pair(item.product.name, currentQty)
            }
        }
        countMap.values.sortedByDescending { it.second }.take(5)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TajerBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "التقارير والأرباح الصافية",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TajerTextPrimary
                )
                Text(
                    text = "تحليل مالي شامل: الإيرادات، تكلفة البضاعة المباعة، المصروفات، وصافي الربح",
                    style = MaterialTheme.typography.bodySmall,
                    color = TajerTextSecondary
                )
            }
        }

        // Net Profit Highlight Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (netProfit >= 0) EmeraldPrimary else StatusDanger
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "صافي الأرباح التقديرية للمتجر",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${String.format("%,.0f", netProfit)} ج.م",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "= (المبيعات - تكلفة شراء البضاعة) - المصروفات الإدارية والتشغيلية",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }
        }

        // Financial Breakdown Grid
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "جدول قائمة الدخل المبسطة:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TajerTextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    FinancialRow(
                        title = "1. إجمالي المبيعات (الإيرادات)",
                        value = "${String.format("%,.0f", totalRevenue)} ج.م",
                        valueColor = EmeraldPrimary,
                        isBold = true
                    )
                    HorizontalDivider(color = TajerBorder, modifier = Modifier.padding(vertical = 8.dp))

                    FinancialRow(
                        title = "2. تكلفة البضاعة المباعة (سعر الشراء)",
                        value = "- ${String.format("%,.0f", cogs)} ج.م",
                        valueColor = StatusDanger
                    )
                    HorizontalDivider(color = TajerBorder, modifier = Modifier.padding(vertical = 8.dp))

                    FinancialRow(
                        title = "3. مجمل الربح التجاري (Gross Profit)",
                        value = "${String.format("%,.0f", grossProfit)} ج.م",
                        valueColor = WarmGoldDark,
                        isBold = true
                    )
                    HorizontalDivider(color = TajerBorder, modifier = Modifier.padding(vertical = 8.dp))

                    FinancialRow(
                        title = "4. إجمالي المصروفات والتشغيل",
                        value = "- ${String.format("%,.0f", totalExpenses)} ج.م",
                        valueColor = StatusDanger
                    )
                    HorizontalDivider(color = TajerBorder, modifier = Modifier.padding(vertical = 8.dp))

                    FinancialRow(
                        title = "5. صافي الربح النهائي (Net Profit)",
                        value = "${String.format("%,.0f", netProfit)} ج.م",
                        valueColor = if (netProfit >= 0) EmeraldPrimary else StatusDanger,
                        isBold = true
                    )
                }
            }
        }

        // Payment Methods Distribution
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "توزيع طرق الدفع (المبيعات):",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TajerTextPrimary
                    )
                    Text(
                        text = "مقارنة حصص كاش، فودافون كاش، إنستاباي، والفيزا",
                        style = MaterialTheme.typography.bodySmall,
                        color = TajerTextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    paymentBreakdown.forEach { (method, count, total) ->
                        val percent = if (totalRevenue > 0) (total / totalRevenue) * 100 else 0.0
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${method.titleAr} ($count فواتير)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TajerTextPrimary
                                )
                                Text(
                                    text = "${String.format("%,.0f", total)} ج.م (${String.format("%.1f", percent)}%)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(method.badgeColorHex)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (percent / 100f).toFloat() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = Color(method.badgeColorHex),
                                trackColor = TajerSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Top Selling Items
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "أكثر المنتجات مبيعاً وطلباً:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TajerTextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (topSelling.isEmpty()) {
                        Text(text = "لا توجد مبيعات كافية", color = TajerTextMuted)
                    } else {
                        topSelling.forEachIndexed { index, (name, qty) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (index == 0) WarmGoldLight else TajerSurfaceVariant
                                    ) {
                                        Text(
                                            text = "#${index + 1}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (index == 0) WarmGoldDark else TajerTextSecondary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = TajerTextPrimary
                                    )
                                }

                                Surface(
                                    color = EmeraldLight,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "$qty قطعة مباعة",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EmeraldPrimary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun FinancialRow(
    title: String,
    value: String,
    valueColor: Color,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = if (isBold) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = TajerTextPrimary
        )
        Text(
            text = value,
            style = if (isBold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
            color = valueColor
        )
    }
}
