package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItem
import com.example.data.model.PaymentMethod
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.ui.components.ReceiptDialog
import com.example.ui.components.VariantSelectorDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.TajerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosScreen(
    viewModel: TajerViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val discountAmount by viewModel.discountAmount.collectAsState()
    val selectedPaymentMethod by viewModel.selectedPaymentMethod.collectAsState()
    val selectedCustomerId by viewModel.selectedCustomerId.collectAsState()
    val customPaidAmount by viewModel.customPaidAmount.collectAsState()

    val searchQuery by viewModel.posSearchQuery.collectAsState()
    val selectedCategory by viewModel.posSelectedCategory.collectAsState()

    val productForVariantSelector by viewModel.posProductForVariantSelection.collectAsState()
    val activeReceiptSale by viewModel.activeReceiptSale.collectAsState()
    val storeProfile by viewModel.storeProfile.collectAsState()

    var showCartBottomSheet by remember { mutableStateOf(false) }

    // Computations
    val subtotal = cartItems.sumOf { it.totalPrice }
    val total = (subtotal - discountAmount).coerceAtLeast(0.0)
    val paid = when (selectedPaymentMethod) {
        PaymentMethod.DEBT -> customPaidAmount ?: 0.0
        else -> customPaidAmount ?: total
    }
    val remaining = (total - paid).coerceAtLeast(0.0)

    val filteredProducts = remember(products, searchQuery, selectedCategory) {
        products.filter { p ->
            val matchesQuery = searchQuery.isBlank() ||
                    p.name.contains(searchQuery, ignoreCase = true) ||
                    p.sku.contains(searchQuery, ignoreCase = true) ||
                    p.barcode.contains(searchQuery, ignoreCase = true)
            val matchesCat = selectedCategory == null || p.category == selectedCategory
            matchesQuery && matchesCat
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 840.dp

        Scaffold(
            containerColor = TajerBackground,
            bottomBar = {
                if (!isWideScreen && cartItems.isNotEmpty()) {
                    // Floating cart bottom bar on mobile
                    Surface(
                        color = Color.White,
                        tonalElevation = 8.dp,
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.navigationBars)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${cartItems.sumOf { it.quantity }} قطعة في الفاتورة",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TajerTextMuted
                                )
                                Text(
                                    text = "${String.format("%,.0f", total)} ج.م",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                            }

                            Button(
                                onClick = { showCartBottomSheet = true },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.ShoppingCartCheckout, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "عرض الفاتورة والدفع", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            if (isWideScreen) {
                // Wide Screen Split View: Catalog (left/start) & Live Cart Drawer (right/end)
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Products Catalog (70% width)
                    Box(modifier = Modifier.weight(1.3f)) {
                        ProductCatalogSection(
                            products = filteredProducts,
                            searchQuery = searchQuery,
                            onSearchChange = { viewModel.setPosSearchQuery(it) },
                            selectedCategory = selectedCategory,
                            onCategoryChange = { viewModel.setPosCategory(it) },
                            onProductClick = { viewModel.openVariantSelector(it) }
                        )
                    }

                    // Cart Panel (30% width)
                    Card(
                        modifier = Modifier
                            .weight(0.9f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        CartContent(
                            cartItems = cartItems,
                            subtotal = subtotal,
                            discount = discountAmount,
                            total = total,
                            paid = paid,
                            remaining = remaining,
                            selectedMethod = selectedPaymentMethod,
                            onMethodSelect = { viewModel.setPaymentMethod(it) },
                            customers = customers,
                            selectedCustomerId = selectedCustomerId,
                            onCustomerSelect = { viewModel.selectCustomer(it) },
                            onDiscountChange = { viewModel.setDiscount(it) },
                            onPaidAmountChange = { viewModel.setCustomPaidAmount(it) },
                            onUpdateQuantity = { idx, q -> viewModel.updateCartItemQuantity(idx, q) },
                            onRemoveItem = { idx -> viewModel.removeCartItem(idx) },
                            onClearCart = { viewModel.clearCart() },
                            onCheckout = { viewModel.completeCheckout() }
                        )
                    }
                }
            } else {
                // Compact Screen Layout: Catalog on full view + modal bottom sheet for Cart
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    ProductCatalogSection(
                        products = filteredProducts,
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.setPosSearchQuery(it) },
                        selectedCategory = selectedCategory,
                        onCategoryChange = { viewModel.setPosCategory(it) },
                        onProductClick = { viewModel.openVariantSelector(it) }
                    )
                }

                // Mobile Modal Bottom Sheet for Cart
                if (showCartBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showCartBottomSheet = false },
                        containerColor = Color.White,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 640.dp)
                        ) {
                            CartContent(
                                cartItems = cartItems,
                                subtotal = subtotal,
                                discount = discountAmount,
                                total = total,
                                paid = paid,
                                remaining = remaining,
                                selectedMethod = selectedPaymentMethod,
                                onMethodSelect = { viewModel.setPaymentMethod(it) },
                                customers = customers,
                                selectedCustomerId = selectedCustomerId,
                                onCustomerSelect = { viewModel.selectCustomer(it) },
                                onDiscountChange = { viewModel.setDiscount(it) },
                                onPaidAmountChange = { viewModel.setCustomPaidAmount(it) },
                                onUpdateQuantity = { idx, q -> viewModel.updateCartItemQuantity(idx, q) },
                                onRemoveItem = { idx -> viewModel.removeCartItem(idx) },
                                onClearCart = { viewModel.clearCart() },
                                onCheckout = {
                                    showCartBottomSheet = false
                                    viewModel.completeCheckout()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Variant selector modal (choosing Color & Size)
    productForVariantSelector?.let { prod ->
        VariantSelectorDialog(
            product = prod,
            onDismiss = { viewModel.closeVariantSelector() },
            onConfirmAddToCart = { variant, qty ->
                viewModel.addToCart(prod, variant, qty)
            }
        )
    }

    // Receipt Modal on checkout completion
    activeReceiptSale?.let { sale ->
        ReceiptDialog(
            sale = sale,
            storeProfile = storeProfile,
            onDismiss = { viewModel.closeReceiptDialog() },
            onPrintClicked = {
                viewModel.showMessage("تم إرسال الفاتورة ${sale.invoiceNumber} لطابعة الإيصالات البلوتوث/USB")
            },
            onShareClicked = {
                viewModel.showMessage("تم نسخ رابط الفاتورة الإلكترونية للمشاركة عبر واتساب")
            }
        )
    }
}

@Composable
fun ProductCatalogSection(
    products: List<Product>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedCategory: ProductCategory?,
    onCategoryChange: (ProductCategory?) -> Unit,
    onProductClick: (Product) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar with Barcode Scanner Icon
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("ابحث عن صنف بالاسم، كود المقاس، أو امسح الباركود...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = EmeraldPrimary)
            },
            trailingIcon = {
                IconButton(onClick = { /* simulated scanner */ }) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = "مسح باركود", tint = EmeraldSecondary)
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Categories Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { onCategoryChange(null) },
                    label = { Text("جميع الأقسام") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }

            items(ProductCategory.values()) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { onCategoryChange(if (selectedCategory == cat) null else cat) },
                    label = { Text(cat.titleAr) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Products Grid
        if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "لا توجد منتجات متطابقة مع البحث", color = TajerTextMuted)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(products, key = { it.id }) { product ->
                    PosProductCard(
                        product = product,
                        onClick = { onProductClick(product) }
                    )
                }
            }
        }
    }
}

@Composable
fun PosProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Category Tag & Stock badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = EmeraldLight,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = product.category.titleAr,
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldDark,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    color = if (product.isLowStock) StatusDanger.copy(alpha = 0.15f) else TajerSurfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${product.totalStock} ق",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (product.isLowStock) StatusDanger else TajerTextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = TajerTextPrimary,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${product.colors.size} ألوان • ${product.sizes.size} مقاسات",
                style = MaterialTheme.typography.bodySmall,
                color = TajerTextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${String.format("%,.0f", product.sellingPrice)} ج.م",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )

                FilledIconButton(
                    onClick = onClick,
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "اختيار", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CartContent(
    cartItems: List<CartItem>,
    subtotal: Double,
    discount: Double,
    total: Double,
    paid: Double,
    remaining: Double,
    selectedMethod: PaymentMethod,
    onMethodSelect: (PaymentMethod) -> Unit,
    customers: List<com.example.data.model.Customer>,
    selectedCustomerId: String?,
    onCustomerSelect: (String?) -> Unit,
    onDiscountChange: (Double) -> Unit,
    onPaidAmountChange: (Double?) -> Unit,
    onUpdateQuantity: (Int, Int) -> Unit,
    onRemoveItem: (Int) -> Unit,
    onClearCart: () -> Unit,
    onCheckout: () -> Unit
) {
    var discountInput by remember { mutableStateOf(if (discount > 0) discount.toString() else "") }
    var paidInput by remember { mutableStateOf("") }
    var customerDropdownExpanded by remember { mutableStateOf(false) }

    val selectedCustomer = customers.find { it.id == selectedCustomerId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Cart Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = EmeraldPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "فاتورة المبيعات الحالية",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TajerTextPrimary
                )
            }

            if (cartItems.isNotEmpty()) {
                TextButton(onClick = onClearCart) {
                    Text(text = "إفراغ السلة", color = StatusDanger, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = TajerBorder)
        Spacer(modifier = Modifier.height(10.dp))

        // Cart Items List
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.AddShoppingCart,
                        contentDescription = null,
                        tint = TajerTextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "السلة فارغة",
                        style = MaterialTheme.typography.titleMedium,
                        color = TajerTextMuted
                    )
                    Text(
                        text = "اضغط على أي منتج لاختيار المقاس واللون وإضافته للفاتورة",
                        style = MaterialTheme.typography.bodySmall,
                        color = TajerTextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                cartItems.forEachIndexed { idx, item ->
                    CartItemRow(
                        item = item,
                        onIncrement = { onUpdateQuantity(idx, item.quantity + 1) },
                        onDecrement = { onUpdateQuantity(idx, item.quantity - 1) },
                        onRemove = { onRemoveItem(idx) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = TajerBorder)
        Spacer(modifier = Modifier.height(12.dp))

        // Customer Selection
        Text(text = "العميل:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TajerTextPrimary)
        Spacer(modifier = Modifier.height(6.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedCard(
                onClick = { customerDropdownExpanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = selectedCustomer?.name ?: "عميل نقدي (Walk-in)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TajerTextPrimary
                        )
                    }
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }

            DropdownMenu(
                expanded = customerDropdownExpanded,
                onDismissRequest = { customerDropdownExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("عميل نقدي (افتراضي)") },
                    onClick = {
                        onCustomerSelect(null)
                        customerDropdownExpanded = false
                    }
                )
                customers.forEach { c ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(c.name, fontWeight = FontWeight.Bold)
                                Text("${c.phone} ${if (c.outstandingBalance > 0) "• ديون: ${c.outstandingBalance} ج.م" else ""}", style = MaterialTheme.typography.bodySmall, color = TajerTextMuted)
                            }
                        },
                        onClick = {
                            onCustomerSelect(c.id)
                            customerDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Payment Method Chips
        Text(text = "طريقة الدفع:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TajerTextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            PaymentMethod.values().forEach { method ->
                val isSelected = selectedMethod == method
                FilterChip(
                    selected = isSelected,
                    onClick = { onMethodSelect(method) },
                    label = { Text(method.titleAr) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(method.badgeColorHex),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Discount & Custom Paid Amount Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = discountInput,
                onValueChange = {
                    discountInput = it
                    onDiscountChange(it.toDoubleOrNull() ?: 0.0)
                },
                label = { Text("الخصم (ج.م)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = paidInput,
                onValueChange = {
                    paidInput = it
                    onPaidAmountChange(it.toDoubleOrNull())
                },
                label = { Text("المبلغ المدفوع") },
                placeholder = { Text("${String.format("%,.0f", total)}") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Financial Calculation Summary Box
        Surface(
            color = TajerSurfaceVariant,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "المجموع الفرعي:", style = MaterialTheme.typography.bodySmall, color = TajerTextSecondary)
                    Text(text = "${String.format("%,.2f", subtotal)} ج.م", style = MaterialTheme.typography.bodySmall, color = TajerTextPrimary)
                }
                if (discount > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "قيمة الخصم:", style = MaterialTheme.typography.bodySmall, color = StatusDanger)
                        Text(text = "- ${String.format("%,.2f", discount)} ج.م", style = MaterialTheme.typography.bodySmall, color = StatusDanger)
                    }
                }
                HorizontalDivider(color = TajerBorder, modifier = Modifier.padding(vertical = 6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "الإجمالي المستحق:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TajerTextPrimary)
                    Text(text = "${String.format("%,.2f", total)} ج.م", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                }
                if (remaining > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "المتبقي آجل على العميل:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = StatusWarning)
                        Text(text = "${String.format("%,.2f", remaining)} ج.م", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = StatusWarning)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Checkout Button
        Button(
            onClick = onCheckout,
            enabled = cartItems.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Print, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "إتمام البيع وطباعة الفاتورة (${String.format("%,.0f", total)} ج.م)",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        color = TajerSurfaceVariant,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = TajerTextPrimary
                )
                Text(
                    text = "لون: ${item.variant.color} | مقاس: ${item.variant.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = EmeraldPrimary
                )
                Text(
                    text = "${String.format("%,.0f", item.unitPrice)} ج.م × ${item.quantity} = ${String.format("%,.0f", item.totalPrice)} ج.م",
                    style = MaterialTheme.typography.bodySmall,
                    color = TajerTextSecondary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                FilledIconButton(
                    onClick = onDecrement,
                    modifier = Modifier.size(28.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                }

                Text(
                    text = "${item.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                FilledIconButton(
                    onClick = onIncrement,
                    modifier = Modifier.size(28.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }

                IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = StatusDanger, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
