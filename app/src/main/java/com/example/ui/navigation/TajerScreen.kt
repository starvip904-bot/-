package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class TajerScreen(val titleAr: String, val icon: ImageVector) {
    DASHBOARD("الرئيسية", Icons.Default.Dashboard),
    PRODUCTS("المنتجات والمخزون", Icons.Default.Inventory2),
    SALES_POS("المبيعات والكاشير", Icons.Default.PointOfSale),
    CUSTOMERS("العملاء والآجل", Icons.Default.People),
    RETURNS("الاستبدال والمرتجع", Icons.Default.AssignmentReturn),
    EXPENSES("المصروفات", Icons.Default.AccountBalanceWallet),
    REPORTS("التقارير", Icons.Default.BarChart),
    SETTINGS("الإعدادات", Icons.Default.Settings)
}
