package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.data.model.ProductVariant
import com.example.ui.theme.*
import com.example.ui.viewmodel.TajerViewModel
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductsScreen(
    viewModel: TajerViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val searchQuery by viewModel.productSearchQuery.collectAsState()
    val categoryFilter by viewModel.productCategoryFilter.collectAsState()
    val onlyLowStock by viewModel.onlyLowStockFilter.collectAsState()

    var showAddProductDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<Product?>(null) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    // Filtering logic
    val filteredProducts = remember(products, searchQuery, categoryFilter, onlyLowStock) {
        products.filter { p ->
            val matchesQuery = searchQuery.isBlank() ||
                    p.name.contains(searchQuery, ignoreCase = true) ||
                    p.sku.contains(searchQuery, ignoreCase = true) ||
                    p.barcode.contains(searchQuery, ignoreCase = true)
            val matchesCategory = categoryFilter == null || p.category == categoryFilter
            val matchesLowStock = !onlyLowStock || p.isLowStock
            matchesQuery && matchesCategory && matchesLowStock
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddProductDialog = true },
                containerColor = EmeraldPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "إضافة منتج", style = MaterialTheme.typography.labelMedium)
                }
            }
        },
        containerColor = TajerBackground
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "المنتجات والمخزون",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TajerTextPrimary
                    )
                    Text(
                        text = "إدارة الأصناف، الألوان، المقاسات، والكميات المتوفرة بالمحل",
                        style = MaterialTheme.typography.bodySmall,
                        color = TajerTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar & Low-stock toggle
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setProductSearch(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("ابحث باسم المنتج، كود الـ SKU، أو الباركود...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EmeraldPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setProductSearch("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "مسح")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldPrimary,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Chips & Low-Stock Toggle Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = categoryFilter == null,
                        onClick = { viewModel.setProductCategoryFilter(null) },
                        label = { Text("الكل (${products.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }

                items(ProductCategory.values()) { cat ->
                    val count = products.count { it.category == cat }
                    FilterChip(
                        selected = categoryFilter == cat,
                        onClick = { viewModel.setProductCategoryFilter(if (categoryFilter == cat) null else cat) },
                        label = { Text("${cat.titleAr} ($count)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }

                item {
                    FilterChip(
                        selected = onlyLowStock,
                        onClick = { viewModel.toggleLowStockFilter() },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp), tint = if (onlyLowStock) Color.White else StatusDanger)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("النواقص فقط")
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StatusDanger,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Product List
            if (filteredProducts.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = TajerTextMuted,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "لا توجد منتجات مطابقة لخيارات البحث",
                            style = MaterialTheme.typography.titleMedium,
                            color = TajerTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "جرب البحث بكلمات أخرى أو أضف منتجاً جديداً للمخزن",
                            style = MaterialTheme.typography.bodySmall,
                            color = TajerTextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        ProductItemCard(
                            product = product,
                            onEdit = { productToEdit = product },
                            onDelete = { productToDelete = product }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }
            }
        }
    }

    // Add Product Dialog
    if (showAddProductDialog) {
        AddEditProductDialog(
            existingProduct = null,
            onDismiss = { showAddProductDialog = false },
            onSave = { product ->
                viewModel.addProduct(
                    name = product.name,
                    sku = product.sku,
                    barcode = product.barcode,
                    category = product.category,
                    purchasePrice = product.purchasePrice,
                    sellingPrice = product.sellingPrice,
                    colors = product.colors,
                    sizes = product.sizes,
                    variants = product.variants
                )
                showAddProductDialog = false
            }
        )
    }

    // Edit Product Dialog
    productToEdit?.let { product ->
        AddEditProductDialog(
            existingProduct = product,
            onDismiss = { productToEdit = null },
            onSave = { updated ->
                viewModel.updateProduct(updated)
                productToEdit = null
            }
        )
    }

    // Delete Confirmation Dialog
    productToDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text(text = "حذف المنتج من المخزن") },
            text = { Text(text = "هل أنت متأكد من رغبتك في حذف '${product.name}'؟ سيتم حذف جميع الألوان والمقاسات التابعة له.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProduct(product.id)
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
                ) {
                    Text("نعم، حذف", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { productToDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductItemCard(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var expandedVariants by remember { mutableStateOf(false) }
    val profit = product.sellingPrice - product.purchasePrice
    val profitMargin = if (product.sellingPrice > 0) (profit / product.sellingPrice) * 100 else 0.0

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Category tag, SKU/Barcode, Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = EmeraldLight,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = product.category.titleAr,
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldDark,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "SKU: ${product.sku}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TajerTextMuted
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = StatusDanger, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product Name & Total Stock Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TajerTextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    color = if (product.isLowStock) StatusDanger.copy(alpha = 0.12f) else EmeraldLight,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (product.isLowStock) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = StatusDanger, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = "المخزن: ${product.totalStock} قطعة",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (product.isLowStock) StatusDanger else EmeraldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pricing Row: Cost vs Sell vs Profit
            Surface(
                color = TajerSurfaceVariant,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "سعر الشراء (التكلفة)", style = MaterialTheme.typography.bodySmall, color = TajerTextMuted)
                        Text(text = "${String.format("%,.0f", product.purchasePrice)} ج.م", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TajerTextSecondary)
                    }

                    Column {
                        Text(text = "سعر البيع", style = MaterialTheme.typography.bodySmall, color = TajerTextMuted)
                        Text(text = "${String.format("%,.0f", product.sellingPrice)} ج.م", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "هامش الربح", style = MaterialTheme.typography.bodySmall, color = TajerTextMuted)
                        Text(
                            text = "+${String.format("%,.0f", profit)} ج.م (${String.format("%.0f", profitMargin)}%)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = WarmGoldDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Variants Matrix toggle button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedVariants = !expandedVariants },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "تفاصيل الألوان والمقاسات (${product.variants.size} خيار متاح)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )
                Icon(
                    imageVector = if (expandedVariants) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = EmeraldPrimary
                )
            }

            // Expanded Variants Grid
            if (expandedVariants) {
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    product.variants.forEach { v ->
                        val isLow = v.quantity <= v.lowStockThreshold
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isLow) StatusDanger.copy(alpha = 0.08f) else Color.White,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isLow) StatusDanger.copy(alpha = 0.4f) else TajerBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${v.color} - ${v.size}:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TajerTextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${v.quantity} ق",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLow) StatusDanger else EmeraldPrimary
                                )
                                if (isLow) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "⚠️", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditProductDialog(
    existingProduct: Product?,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit
) {
    var name by remember { mutableStateOf(existingProduct?.name ?: "") }
    var sku by remember { mutableStateOf(existingProduct?.sku ?: "TJR-${(100..999).random()}") }
    var barcode by remember { mutableStateOf(existingProduct?.barcode ?: "622100${(100..999).random()}") }
    var selectedCategory by remember { mutableStateOf(existingProduct?.category ?: ProductCategory.MEN_CLOTHING) }
    var purchasePriceInput by remember { mutableStateOf(existingProduct?.purchasePrice?.toString() ?: "300") }
    var sellingPriceInput by remember { mutableStateOf(existingProduct?.sellingPrice?.toString() ?: "550") }

    // Colors list
    val defaultColors = listOf("أسود", "أبيض", "كحلي", "بيج", "رمادي", "زيتي", "أحمر", "هافان")
    var selectedColors by remember {
        mutableStateOf(existingProduct?.colors ?: listOf("أسود", "أبيض"))
    }
    var newColorInput by remember { mutableStateOf("") }

    // Sizes list
    val clothingSizes = listOf("S", "M", "L", "XL", "XXL", "3XL")
    val shoesSizes = listOf("38", "39", "40", "41", "42", "43", "44", "45")
    var selectedSizes by remember {
        mutableStateOf(existingProduct?.sizes ?: listOf("M", "L", "XL"))
    }

    // Variants list mapping
    val initialVariants = remember(selectedColors, selectedSizes) {
        val list = mutableListOf<ProductVariant>()
        selectedColors.forEach { color ->
            selectedSizes.forEach { size ->
                val existing = existingProduct?.variants?.find { it.color == color && it.size == size }
                list.add(
                    existing ?: ProductVariant(
                        id = "v-${UUID.randomUUID().toString().take(6)}",
                        color = color,
                        size = size,
                        quantity = 5,
                        lowStockThreshold = 2
                    )
                )
            }
        }
        list
    }

    var currentVariants by remember { mutableStateOf(initialVariants) }

    // Keep currentVariants in sync if colors/sizes change
    LaunchedEffect(selectedColors, selectedSizes) {
        val updated = mutableListOf<ProductVariant>()
        selectedColors.forEach { color ->
            selectedSizes.forEach { size ->
                val found = currentVariants.find { it.color == color && it.size == size }
                updated.add(
                    found ?: ProductVariant(
                        id = "v-${UUID.randomUUID().toString().take(6)}",
                        color = color,
                        size = size,
                        quantity = 5,
                        lowStockThreshold = 2
                    )
                )
            }
        }
        currentVariants = updated
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (existingProduct == null) "إضافة منتج جديد للمخزن" else "تعديل بيانات المنتج",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TajerTextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إلغاء")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم المنتج (مثال: قميص كتان تركي أوفر سايز)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // SKU & Barcode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = sku,
                        onValueChange = { sku = it },
                        label = { Text("كود الـ SKU") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = barcode,
                        onValueChange = { barcode = it },
                        label = { Text("الباركود") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category Selector
                Text(text = "القسم / التصنيف:", style = MaterialTheme.typography.labelSmall, color = TajerTextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(ProductCategory.values()) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.titleAr) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Prices
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = purchasePriceInput,
                        onValueChange = { purchasePriceInput = it },
                        label = { Text("سعر الشراء (التكلفة)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = sellingPriceInput,
                        onValueChange = { sellingPriceInput = it },
                        label = { Text("سعر البيع للجمهور") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Colors selection
                Text(text = "اختر الألوان المتوفرة:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TajerTextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    defaultColors.forEach { col ->
                        val isSelected = selectedColors.contains(col)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedColors = if (isSelected) {
                                    if (selectedColors.size > 1) selectedColors - col else selectedColors
                                } else {
                                    selectedColors + col
                                }
                            },
                            label = { Text(col) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sizes selection
                Text(text = "اختر المقاسات المتوفرة:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TajerTextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                val availableSizes = if (selectedCategory == ProductCategory.MEN_SHOES || selectedCategory == ProductCategory.WOMEN_SHOES) shoesSizes else clothingSizes
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableSizes.forEach { sz ->
                        val isSelected = selectedSizes.contains(sz)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedSizes = if (isSelected) {
                                    if (selectedSizes.size > 1) selectedSizes - sz else selectedSizes
                                } else {
                                    selectedSizes + sz
                                }
                            },
                            label = { Text(sz) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Variants Inventory Matrix
                Text(
                    text = "كمية المخزون لكل توليفة (لون ومقاس):",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                currentVariants.forEachIndexed { idx, variant ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${variant.color} - ${variant.size}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TajerTextPrimary,
                            modifier = Modifier.weight(1f)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledIconButton(
                                onClick = {
                                    val newQty = (variant.quantity - 1).coerceAtLeast(0)
                                    val updated = currentVariants.toMutableList()
                                    updated[idx] = variant.copy(quantity = newQty)
                                    currentVariants = updated
                                },
                                modifier = Modifier.size(32.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = TajerSurfaceVariant)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                            }

                            Text(
                                text = "${variant.quantity}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )

                            FilledIconButton(
                                onClick = {
                                    val updated = currentVariants.toMutableList()
                                    updated[idx] = variant.copy(quantity = variant.quantity + 1)
                                    currentVariants = updated
                                },
                                modifier = Modifier.size(32.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = EmeraldLight)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Save button
                Button(
                    onClick = {
                        val cost = purchasePriceInput.toDoubleOrNull() ?: 0.0
                        val sell = sellingPriceInput.toDoubleOrNull() ?: 0.0
                        val product = Product(
                            id = existingProduct?.id ?: "p-${System.currentTimeMillis()}",
                            name = name.ifBlank { "منتج بدون اسم" },
                            sku = sku,
                            barcode = barcode,
                            category = selectedCategory,
                            purchasePrice = cost,
                            sellingPrice = sell,
                            colors = selectedColors,
                            sizes = selectedSizes,
                            variants = currentVariants
                        )
                        onSave(product)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text(
                        text = if (existingProduct == null) "حفظ المنتج وإضافته للمخزن" else "حفظ التعديلات",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}
