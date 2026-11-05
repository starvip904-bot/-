package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.TajerBottomNavBar
import com.example.ui.components.TajerSidebar
import com.example.ui.navigation.TajerScreen
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.TajerViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TajerTheme {
                TajerApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TajerApp(
    viewModel: TajerViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val products by viewModel.products.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    val lowStockCount = products.count { it.isLowStock }
    val outstandingDebtsCount = customers.count { it.outstandingBalance > 0 }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Handle back button
    BackHandler(enabled = currentScreen != TajerScreen.DASHBOARD) {
        val handled = viewModel.handleBack()
        if (!handled) {
            viewModel.navigateTo(TajerScreen.DASHBOARD)
        }
    }

    // React to snackbar messages
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 760.dp

        if (isWideScreen) {
            // Desktop / Tablet layout: Sidebar on the right (RTL start) and content pane on the left
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Persistent Sidebar
                TajerSidebar(
                    currentScreen = currentScreen,
                    onNavigate = { viewModel.navigateTo(it) },
                    lowStockCount = lowStockCount,
                    outstandingDebtsCount = outstandingDebtsCount
                )

                // Main Content View
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    containerColor = TajerBackground,
                    modifier = Modifier.weight(1f)
                ) { innerPadding ->
                    ScreenContent(
                        currentScreen = currentScreen,
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        } else {
            // Mobile layout: Drawer + TopAppBar + BottomNavBar
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = Color.White,
                        modifier = Modifier.width(300.dp)
                    ) {
                        TajerSidebar(
                            currentScreen = currentScreen,
                            onNavigate = {
                                viewModel.navigateTo(it)
                                scope.launch { drawerState.close() }
                            },
                            lowStockCount = lowStockCount,
                            outstandingDebtsCount = outstandingDebtsCount,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(EmeraldPrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.tajer_logo),
                                            contentDescription = "تاجِر",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = currentScreen.titleAr,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TajerTextPrimary
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "القائمة",
                                        tint = EmeraldPrimary
                                    )
                                }
                            },
                            actions = {
                                if (currentScreen != TajerScreen.SALES_POS) {
                                    IconButton(onClick = { viewModel.navigateTo(TajerScreen.SALES_POS) }) {
                                        Icon(
                                            imageVector = Icons.Default.AddShoppingCart,
                                            contentDescription = "كاشير",
                                            tint = EmeraldPrimary
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.White
                            )
                        )
                    },
                    bottomBar = {
                        TajerBottomNavBar(
                            currentScreen = currentScreen,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    containerColor = TajerBackground
                ) { innerPadding ->
                    ScreenContent(
                        currentScreen = currentScreen,
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenContent(
    currentScreen: TajerScreen,
    viewModel: TajerViewModel,
    modifier: Modifier = Modifier
) {
    when (currentScreen) {
        TajerScreen.DASHBOARD -> DashboardScreen(
            viewModel = viewModel,
            onNavigate = { viewModel.navigateTo(it) },
            modifier = modifier
        )
        TajerScreen.PRODUCTS -> ProductsScreen(
            viewModel = viewModel,
            modifier = modifier
        )
        TajerScreen.SALES_POS -> PosScreen(
            viewModel = viewModel,
            modifier = modifier
        )
        TajerScreen.CUSTOMERS -> CustomersScreen(
            viewModel = viewModel,
            modifier = modifier
        )
        TajerScreen.RETURNS -> ReturnsScreen(
            viewModel = viewModel,
            modifier = modifier
        )
        TajerScreen.EXPENSES -> ExpensesScreen(
            viewModel = viewModel,
            modifier = modifier
        )
        TajerScreen.REPORTS -> ReportsScreen(
            viewModel = viewModel,
            modifier = modifier
        )
        TajerScreen.SETTINGS -> SettingsScreen(
            viewModel = viewModel,
            modifier = modifier
        )
    }
}
