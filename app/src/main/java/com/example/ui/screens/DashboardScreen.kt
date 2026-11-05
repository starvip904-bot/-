package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.data.model.Sale
import com.example.ui.components.*
import com.example.ui.navigation.TajerScreen
import com.example.ui.theme.*
import com.example.ui.viewmodel.TajerViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: TajerViewModel,
    onNavigate: (TajerScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val sales by viewModel.sales.collectAsState()
    val products by viewModel.products.collectAsState()
    val customers by viewModel.customers.collectAsState()

    // Calculations
    val now = System.currentTimeMillis()
    val oneDay = 86400000L

    // Sales today (within today's calendar date)
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    val startOfToday = calendar.timeInMillis

    val todaySales = sales.filter { it.timestamp >= startOfToday }
    val totalSalesTodayAmount = todaySales.sumOf { it.total }

    val totalOutstandingDebts = customers.sumOf { it.outstandingBalance }
    val lowStockCount = products.count { it.isLowStock }
    val totalProductsCount = products.size

    // Weekly sales breakdown for the chart (last 7 days)
    val dayFormat = SimpleDateFormat("EEEE", Locale("ar"))
    val weeklyData = remember(sales) {
        val daysList = mutableListOf<WeeklyBarData>()
        val cal = Calendar.getInstance()
        for (i in 6 downTo 0) {
            cal.timeInMillis = now - (i * oneDay)
            val dayStart = cal.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }.timeInMillis
            val dayEnd = dayStart + oneDay
            val dayTotal = sales.filter { it.timestamp in dayStart until dayEnd }.sumOf { it.total }
            daysList.add(WeeklyBarData(dayFormat.format(Date(dayStart)), dayTotal))
        }
        daysList
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TajerBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Banner / Store Title
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "مرحباً بك في لوحة تحكم تاجِر 👋",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "نظام إدارة المبيعات ونقاط البيع لمتاجر الملابس والأحذية في مصر",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    FilledTonalButton(
                        onClick = { onNavigate(TajerScreen.SALES_POS) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = WarmGold,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "فاتورة جديدة",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Top 4 Metrics Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatsCard(
                        title = "مبيعات اليوم",
                        value = "${String.format("%,.0f", totalSalesTodayAmount)} ج.م",
                        subtitle = "${todaySales.size} عمليات بيع اليوم",
                        icon = Icons.Default.MonetizationOn,
                        iconBgColor = EmeraldLight,
                        iconTintColor = EmeraldPrimary,
                        badgeText = if (todaySales.isNotEmpty()) "نشط الآن" else "لا توجد",
                        badgeColor = EmeraldPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    StatsCard(
                        title = "ديون العملاء (الآجل)",
                        value = "${String.format("%,.0f", totalOutstandingDebts)} ج.م",
                        subtitle = "مستحقة لدى ${customers.count { it.outstandingBalance > 0 }} عميل",
                        icon = Icons.Default.AccountBalanceWallet,
                        iconBgColor = WarmGoldLight,
                        iconTintColor = WarmGoldDark,
                        badgeText = "تحصيل",
                        badgeColor = WarmGoldDark,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatsCard(
                        title = "إجمالي المنتجات",
                        value = "$totalProductsCount صنف",
                        subtitle = "${products.sumOf { it.totalStock }} قطعة بالمخزن",
                        icon = Icons.Default.Inventory2,
                        iconBgColor = Color(0xFFE8F1FC),
                        iconTintColor = StatusInfo,
                        modifier = Modifier.weight(1f)
                    )

                    StatsCard(
                        title = "نواقص المخزون",
                        value = "$lowStockCount منتجات",
                        subtitle = "بحاجة لإعادة الطلب والتوريد",
                        icon = Icons.Default.WarningAmber,
                        iconBgColor = Color(0xFFFDECEB),
                        iconTintColor = StatusDanger,
                        badgeText = if (lowStockCount > 0) "تنبيه" else "آمن",
                        badgeColor = if (lowStockCount > 0) StatusDanger else StatusSuccess,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Weekly Sales Chart
        item {
            WeeklySalesChart(
                data = weeklyData,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Quick Action Buttons Row
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "إجراءات سريعة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TajerTextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionButton(
                            title = "نقطة البيع (POS)",
                            icon = Icons.Default.PointOfSale,
                            color = EmeraldPrimary,
                            onClick = { onNavigate(TajerScreen.SALES_POS) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            title = "إضافة منتج",
                            icon = Icons.Default.AddBox,
                            color = EmeraldSecondary,
                            onClick = { onNavigate(TajerScreen.PRODUCTS) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            title = "العملاء والديون",
                            icon = Icons.Default.PersonAdd,
                            color = WarmGoldDark,
                            onClick = { onNavigate(TajerScreen.CUSTOMERS) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            title = "تسجيل مصروف",
                            icon = Icons.Default.ReceiptLong,
                            color = StatusDanger,
                            onClick = { onNavigate(TajerScreen.EXPENSES) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Recent Sales Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "أحدث عمليات البيع والفواتير",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TajerTextPrimary
                )
                TextButton(onClick = { onNavigate(TajerScreen.SALES_POS) }) {
                    Text(text = "فتح شاشة الكاشير", color = EmeraldPrimary)
                }
            }
        }

        // Recent Sales Items
        val recentSales = sales.take(5)
        if (recentSales.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "لا توجد مبيعات مسجلة بعد", color = TajerTextMuted)
                    }
                }
            }
        } else {
            items(recentSales) { sale ->
                RecentSaleCard(
                    sale = sale,
                    onClick = { viewModel.showReceiptForSale(sale) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TajerTextPrimary,
                fontWeight = FontWeight.SemiBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun RecentSaleCard(
    sale: Sale,
    onClick: () -> Unit
) {
    val timeFormat = SimpleDateFormat("hh:mm a", Locale("ar"))
    val timeString = timeFormat.format(Date(sale.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(sale.paymentMethod.badgeColorHex).copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = Color(sale.paymentMethod.badgeColorHex),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = sale.invoiceNumber,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TajerTextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(sale.paymentMethod.badgeColorHex).copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = sale.paymentMethod.titleAr,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(sale.paymentMethod.badgeColorHex),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${sale.customerName} • ${sale.items.size} أصناف • $timeString",
                        style = MaterialTheme.typography.bodySmall,
                        color = TajerTextSecondary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${String.format("%,.0f", sale.total)} ج.م",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )
                if (sale.remainingBalance > 0) {
                    Text(
                        text = "آجل: ${String.format("%,.0f", sale.remainingBalance)} ج.م",
                        style = MaterialTheme.typography.labelSmall,
                        color = StatusWarning,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
