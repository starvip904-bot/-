package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

data class WeeklyBarData(
    val dayName: String,
    val amount: Double
)

@Composable
fun WeeklySalesChart(
    data: List<WeeklyBarData>,
    modifier: Modifier = Modifier
) {
    val maxVal = (data.maxOfOrNull { it.amount } ?: 1.0).coerceAtLeast(100.0)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "مخطط المبيعات الأسبوعية",
                        style = MaterialTheme.typography.titleMedium,
                        color = TajerTextPrimary
                    )
                    Text(
                        text = "حركة المبيعات خلال الـ 7 أيام الماضية بالجنيه المصري",
                        style = MaterialTheme.typography.bodySmall,
                        color = TajerTextMuted
                    )
                }

                val totalWeekly = data.sumOf { it.amount }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "الإجمالي",
                        style = MaterialTheme.typography.labelSmall,
                        color = TajerTextSecondary
                    )
                    Text(
                        text = "${String.format("%,.0f", totalWeekly)} ج.م",
                        style = MaterialTheme.typography.titleMedium,
                        color = EmeraldPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Chart area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val barCount = data.size
                    if (barCount == 0) return@Canvas

                    val spacing = w / (barCount * 2f)
                    val barWidth = (w - (spacing * (barCount + 1))) / barCount

                    // Draw subtle grid lines
                    val gridLines = 4
                    for (i in 0..gridLines) {
                        val y = h - (h * (i.toFloat() / gridLines))
                        drawLine(
                            color = Color(0xFFF0F4F2),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Draw bars
                    data.forEachIndexed { index, item ->
                        val barHeight = ((item.amount / maxVal) * (h - 20.dp.toPx())).toFloat()
                        val x = spacing + index * (barWidth + spacing)
                        val y = h - barHeight

                        val isMax = item.amount == maxVal && maxVal > 0
                        val barColor = if (isMax) WarmGold else EmeraldPrimary

                        // Bar background track
                        drawRoundRect(
                            color = EmeraldLight.copy(alpha = 0.5f),
                            topLeft = Offset(x, 10.dp.toPx()),
                            size = Size(barWidth, h - 10.dp.toPx()),
                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )

                        // Filled bar
                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Day labels row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                data.forEach { item ->
                    Text(
                        text = item.dayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = TajerTextSecondary
                    )
                }
            }
        }
    }
}
