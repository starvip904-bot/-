package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.TajerRepository
import com.example.ui.navigation.TajerScreen
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class DailySalesStat(
    val dayNameAr: String,
    val amount: Double
)

class TajerViewModel(
    val repository: TajerRepository = TajerRepository()
) : ViewModel() {

    // Navigation
    private val _currentScreen = MutableStateFlow(TajerScreen.DASHBOARD)
    val currentScreen: StateFlow<TajerScreen> = _currentScreen.asStateFlow()

    // Navigation History for back handling
    private val screenHistory = mutableListOf<TajerScreen>()

    // Snackbar / Feedback
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Repository Flows
    val products = repository.products
    val sales = repository.sales
    val customers = repository.customers
    val expenses = repository.expenses
    val returns = repository.returns
    val storeProfile = repository.storeProfile

    // POS Cart State
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _discountAmount = MutableStateFlow(0.0)
    val discountAmount: StateFlow<Double> = _discountAmount.asStateFlow()

    private val _selectedPaymentMethod = MutableStateFlow(PaymentMethod.CASH)
    val selectedPaymentMethod: StateFlow<PaymentMethod> = _selectedPaymentMethod.asStateFlow()

    private val _selectedCustomerId = MutableStateFlow<String?>(null)
    val selectedCustomerId: StateFlow<String?> = _selectedCustomerId.asStateFlow()

    private val _customPaidAmount = MutableStateFlow<Double?>(null)
    val customPaidAmount: StateFlow<Double?> = _customPaidAmount.asStateFlow()

    private val _posSearchQuery = MutableStateFlow("")
    val posSearchQuery: StateFlow<String> = _posSearchQuery.asStateFlow()

    private val _posSelectedCategory = MutableStateFlow<ProductCategory?>(null)
    val posSelectedCategory: StateFlow<ProductCategory?> = _posSelectedCategory.asStateFlow()

    // Variant Picker Dialog in POS
    private val _posProductForVariantSelection = MutableStateFlow<Product?>(null)
    val posProductForVariantSelection: StateFlow<Product?> = _posProductForVariantSelection.asStateFlow()

    // Receipt Dialog State
    private val _activeReceiptSale = MutableStateFlow<Sale?>(null)
    val activeReceiptSale: StateFlow<Sale?> = _activeReceiptSale.asStateFlow()

    // Products Search & Filter
    private val _productSearchQuery = MutableStateFlow("")
    val productSearchQuery: StateFlow<String> = _productSearchQuery.asStateFlow()

    private val _productCategoryFilter = MutableStateFlow<ProductCategory?>(null)
    val productCategoryFilter: StateFlow<ProductCategory?> = _productCategoryFilter.asStateFlow()

    private val _onlyLowStockFilter = MutableStateFlow(false)
    val onlyLowStockFilter: StateFlow<Boolean> = _onlyLowStockFilter.asStateFlow()

    // Customers Search
    private val _customerSearchQuery = MutableStateFlow("")
    val customerSearchQuery: StateFlow<String> = _customerSearchQuery.asStateFlow()

    // Returns Search
    private val _returnInvoiceQuery = MutableStateFlow("")
    val returnInvoiceQuery: StateFlow<String> = _returnInvoiceQuery.asStateFlow()

    // Navigation Methods
    fun navigateTo(screen: TajerScreen) {
        if (_currentScreen.value != screen) {
            screenHistory.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun handleBack(): Boolean {
        if (screenHistory.isNotEmpty()) {
            _currentScreen.value = screenHistory.removeAt(screenHistory.size - 1)
            return true
        }
        return false
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearMessage() {
        _snackbarMessage.value = null
    }

    // --- POS Cart Operations ---
    fun openVariantSelector(product: Product) {
        _posProductForVariantSelection.value = product
    }

    fun closeVariantSelector() {
        _posProductForVariantSelection.value = null
    }

    fun addToCart(product: Product, variant: ProductVariant, quantity: Int = 1) {
        if (variant.quantity < quantity) {
            showMessage("الكمية المتاحة في المخزن (${variant.quantity}) غير كافية!")
            return
        }

        val currentList = _cartItems.value.toMutableList()
        val existingIndex = currentList.indexOfFirst {
            it.product.id == product.id && it.variant.id == variant.id
        }

        if (existingIndex != -1) {
            val existing = currentList[existingIndex]
            val newTotalQty = existing.quantity + quantity
            if (newTotalQty > variant.quantity) {
                showMessage("لا يمكن إضافة أكثر من الكمية المتوفرة بالمخزن (${variant.quantity})")
                return
            }
            currentList[existingIndex] = existing.copy(quantity = newTotalQty)
        } else {
            currentList.add(
                CartItem(
                    product = product,
                    variant = variant,
                    quantity = quantity,
                    unitPrice = product.sellingPrice
                )
            )
        }
        _cartItems.value = currentList
        showMessage("تمت إضافة ${product.name} (${variant.color} - ${variant.size}) إلى الفاتورة")
        closeVariantSelector()
    }

    fun updateCartItemQuantity(index: Int, newQuantity: Int) {
        val currentList = _cartItems.value.toMutableList()
        if (index in currentList.indices) {
            val item = currentList[index]
            if (newQuantity <= 0) {
                currentList.removeAt(index)
            } else if (newQuantity > item.variant.quantity) {
                showMessage("الحد الأقصى المتوفر في المخزن هو ${item.variant.quantity}")
                return
            } else {
                currentList[index] = item.copy(quantity = newQuantity)
            }
            _cartItems.value = currentList
        }
    }

    fun removeCartItem(index: Int) {
        val currentList = _cartItems.value.toMutableList()
        if (index in currentList.indices) {
            currentList.removeAt(index)
            _cartItems.value = currentList
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _discountAmount.value = 0.0
        _customPaidAmount.value = null
        _selectedCustomerId.value = null
        _selectedPaymentMethod.value = PaymentMethod.CASH
    }

    fun setDiscount(amount: Double) {
        _discountAmount.value = amount.coerceAtLeast(0.0)
    }

    fun setPaymentMethod(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
    }

    fun selectCustomer(customerId: String?) {
        _selectedCustomerId.value = customerId
    }

    fun setCustomPaidAmount(amount: Double?) {
        _customPaidAmount.value = amount
    }

    fun setPosSearchQuery(q: String) {
        _posSearchQuery.value = q
    }

    fun setPosCategory(cat: ProductCategory?) {
        _posSelectedCategory.value = cat
    }

    fun completeCheckout() {
        val items = _cartItems.value
        if (items.isEmpty()) {
            showMessage("سلة البيع فارغة!")
            return
        }

        val subtotal = items.sumOf { it.totalPrice }
        val discount = _discountAmount.value
        val total = (subtotal - discount).coerceAtLeast(0.0)

        val paymentMethod = _selectedPaymentMethod.value
        val customer = repository.customers.value.find { it.id == _selectedCustomerId.value }

        // Paid amount calculation
        val paidAmount = when (paymentMethod) {
            PaymentMethod.DEBT -> _customPaidAmount.value ?: 0.0
            else -> _customPaidAmount.value ?: total
        }
        val remainingBalance = (total - paidAmount).coerceAtLeast(0.0)

        if (paymentMethod == PaymentMethod.DEBT && customer == null) {
            showMessage("يجب تحديد عميل لتسجيل بيع آجل أو على الحساب!")
            return
        }

        val invoiceNumber = "INV-2026-${(1000..9999).random()}"
        val newSale = Sale(
            id = "sale-${System.currentTimeMillis()}",
            invoiceNumber = invoiceNumber,
            timestamp = System.currentTimeMillis(),
            items = items,
            subtotal = subtotal,
            discount = discount,
            total = total,
            paidAmount = paidAmount,
            remainingBalance = remainingBalance,
            paymentMethod = paymentMethod,
            customerId = customer?.id,
            customerName = customer?.name ?: "عميل نقدي",
            customerPhone = customer?.phone,
            status = if (remainingBalance > 0) SaleStatus.PARTIALLY_PAID else SaleStatus.COMPLETED
        )

        repository.createSale(newSale)
        _activeReceiptSale.value = newSale
        clearCart()
        showMessage("تم تأكيد الفاتورة $invoiceNumber بنجاح!")
    }

    fun closeReceiptDialog() {
        _activeReceiptSale.value = null
    }

    fun showReceiptForSale(sale: Sale) {
        _activeReceiptSale.value = sale
    }

    // --- Product Filters ---
    fun setProductSearch(q: String) {
        _productSearchQuery.value = q
    }

    fun setProductCategoryFilter(cat: ProductCategory?) {
        _productCategoryFilter.value = cat
    }

    fun toggleLowStockFilter() {
        _onlyLowStockFilter.value = !_onlyLowStockFilter.value
    }

    fun addProduct(
        name: String,
        sku: String,
        barcode: String,
        category: ProductCategory,
        purchasePrice: Double,
        sellingPrice: Double,
        colors: List<String>,
        sizes: List<String>,
        variants: List<ProductVariant>
    ) {
        val newProduct = Product(
            id = "p-${System.currentTimeMillis()}",
            name = name,
            sku = sku,
            barcode = barcode,
            category = category,
            purchasePrice = purchasePrice,
            sellingPrice = sellingPrice,
            colors = colors,
            sizes = sizes,
            variants = variants
        )
        repository.addProduct(newProduct)
        showMessage("تمت إضافة المنتج $name بنجاح")
    }

    fun updateProduct(product: Product) {
        repository.updateProduct(product)
        showMessage("تم تحديث بيانات المنتج")
    }

    fun deleteProduct(productId: String) {
        repository.deleteProduct(productId)
        showMessage("تم حذف المنتج من المخزن")
    }

    // --- Customer Operations ---
    fun setCustomerSearch(q: String) {
        _customerSearchQuery.value = q
    }

    fun addNewCustomer(name: String, phone: String, address: String, notes: String) {
        val customer = Customer(
            id = "c-${System.currentTimeMillis()}",
            name = name,
            phone = phone,
            address = address,
            notes = notes
        )
        repository.addCustomer(customer)
        showMessage("تم تسجيل العميل $name بنجاح")
    }

    fun recordCustomerDebtPayment(customerId: String, amount: Double, method: PaymentMethod, notes: String) {
        if (amount <= 0) {
            showMessage("يرجى إدخال مبلغ صحيح")
            return
        }
        val success = repository.recordCustomerPayment(customerId, amount, method, notes)
        if (success) {
            showMessage("تم تسجيل سداد مبلغ $amount ج.م بنجاح")
        }
    }

    // --- Expense Operations ---
    fun addNewExpense(title: String, category: ExpenseCategory, amount: Double, notes: String) {
        if (amount <= 0 || title.isBlank()) {
            showMessage("يرجى إدخال بيانات المصروف بشكل صحيح")
            return
        }
        val expense = Expense(
            id = "e-${System.currentTimeMillis()}",
            title = title,
            category = category,
            amount = amount,
            timestamp = System.currentTimeMillis(),
            notes = notes
        )
        repository.addExpense(expense)
        showMessage("تم تسجيل مصروف $title بقيمة $amount ج.م")
    }

    fun deleteExpense(id: String) {
        repository.deleteExpense(id)
        showMessage("تم حذف المصروف")
    }

    // --- Returns Operations ---
    fun setReturnInvoiceQuery(q: String) {
        _returnInvoiceQuery.value = q
    }

    fun processReturn(
        originalInvoiceNumber: String,
        itemsToReturn: List<ReturnItem>,
        notes: String
    ) {
        if (itemsToReturn.isEmpty()) {
            showMessage("يرجى اختيار الأصناف المراد استرجاعها")
            return
        }
        val totalRefund = itemsToReturn.sumOf { it.refundAmount }
        val record = ReturnRecord(
            id = "ret-${System.currentTimeMillis()}",
            returnNumber = "RET-${(100..999).random()}",
            originalInvoiceNumber = originalInvoiceNumber,
            timestamp = System.currentTimeMillis(),
            items = itemsToReturn,
            totalRefund = totalRefund,
            notes = notes
        )
        repository.processReturn(record)
        showMessage("تم تسجيل استرجاع/استبدال البضاعة وإعادة الكميات للمخزن بنجاح")
    }

    // --- Settings & Profile ---
    fun updateStoreProfile(profile: StoreProfile) {
        repository.updateStoreProfile(profile)
        showMessage("تم حفظ إعدادات المتجر وبيانات الفاتورة")
    }

    fun resetDemoData() {
        repository.seedInitialData()
        clearCart()
        showMessage("تمت استعادة البيانات الافتراضية بنجاح")
    }
}
