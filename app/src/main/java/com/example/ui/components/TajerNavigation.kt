package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.navigation.TajerScreen
import com.example.ui.theme.*

@Composable
fun TajerSidebar(
    currentScreen: TajerScreen,
    onNavigate: (TajerScreen) -> Unit,
    lowStockCount: Int,
    outstandingDebtsCount: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .width(260.dp)
            .fillMaxHeight(),
        color = Color.White,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp, horizontal = 14.dp)
        ) {
            // Brand Logo & Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.tajer_logo),
                        contentDescription = "تاجِر",
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "تاجِر",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "TAJER",
                            style = MaterialTheme.typography.labelSmall,
                            color = WarmGoldDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "إدارة محلك أسهل.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TajerTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = TajerBorder)
            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Items
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TajerScreen.values().forEach { screen ->
                    val isSelected = currentScreen == screen
                    val bg = if (isSelected) EmeraldPrimary else Color.Transparent
                    val contentColor = if (isSelected) Color.White else TajerTextPrimary
                    val iconColor = if (isSelected) Color.White else EmeraldSecondary

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(bg)
                            .clickable { onNavigate(screen) }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.titleAr,
                            tint = iconColor,
                            modifier = Modifier.size(22.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = screen.titleAr,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = contentColor,
                            modifier = Modifier.weight(1f)
                        )

                        // Badges for alerts
                        if (screen == TajerScreen.PRODUCTS && lowStockCount > 0) {
                            Surface(
                                color = if (isSelected) WarmGold else StatusWarning.copy(alpha = 0.2f),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "$lowStockCount",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) Color.White else StatusWarning,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else if (screen == TajerScreen.CUSTOMERS && outstandingDebtsCount > 0) {
                            Surface(
                                color = if (isSelected) WarmGold else StatusInfo.copy(alpha = 0.2f),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "$outstandingDebtsCount",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) Color.White else StatusInfo,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Cashier / Store Info Pill
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = TajerSurfaceVariant,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(WarmGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "فرع مصر الجديدة",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = TajerTextPrimary
                        )
                        Text(
                            text = "نظام سحابي متصل",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldPrimary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TajerBottomNavBar(
    currentScreen: TajerScreen,
    onNavigate: (TajerScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    // Primary mobile bottom items: Dashboard, Products, POS, Customers, More
    val mainTabs = listOf(
        TajerScreen.DASHBOARD,
        TajerScreen.PRODUCTS,
        TajerScreen.SALES_POS,
        TajerScreen.CUSTOMERS,
        TajerScreen.EXPENSES
    )

    NavigationBar(
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        mainTabs.forEach { screen ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.titleAr,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = screen.titleAr,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = EmeraldPrimary,
                    selectedTextColor = EmeraldPrimary,
                    indicatorColor = EmeraldLight,
                    unselectedIconColor = TajerTextSecondary,
                    unselectedTextColor = TajerTextSecondary
                )
            )
        }
    }
}
