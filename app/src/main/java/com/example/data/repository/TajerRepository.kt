package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class TajerRepository {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _sales = MutableStateFlow<List<Sale>>(emptyList())
    val sales: StateFlow<List<Sale>> = _sales.asStateFlow()

    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers: StateFlow<List<Customer>> = _customers.asStateFlow()

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _returns = MutableStateFlow<List<ReturnRecord>>(emptyList())
    val returns: StateFlow<List<ReturnRecord>> = _returns.asStateFlow()

    private val _storeProfile = MutableStateFlow(StoreProfile())
    val storeProfile: StateFlow<StoreProfile> = _storeProfile.asStateFlow()

    init {
        seedInitialData()
    }

    fun seedInitialData() {
        val now = System.currentTimeMillis()
        val oneDay = 86400000L

        // Initial Products with clothing & footwear variants
        val initialProducts = listOf(
            Product(
                id = "p-101",
                name = "قميص كتان أوفر سايز كاجوال",
                sku = "TJR-SH-01",
                barcode = "622100101",
                category = ProductCategory.MEN_CLOTHING,
                purchasePrice = 320.0,
                sellingPrice = 580.0,
                colors = listOf("أبيض", "بيج", "كحلي", "زيتي"),
                sizes = listOf("M", "L", "XL", "XXL"),
                variants = listOf(
                    ProductVariant("v-101-1", "أبيض", "M", 6, 2),
                    ProductVariant("v-101-2", "أبيض", "L", 8, 2),
                    ProductVariant("v-101-3", "أبيض", "XL", 2, 3), // Low stock
                    ProductVariant("v-101-4", "بيج", "M", 5, 2),
                    ProductVariant("v-101-5", "بيج", "L", 7, 2),
                    ProductVariant("v-101-6", "بيج", "XL", 1, 3), // Low stock
                    ProductVariant("v-101-7", "كحلي", "L", 4, 2),
                    ProductVariant("v-101-8", "زيتي", "XXL", 3, 2)
                )
            ),
            Product(
                id = "p-102",
                name = "بنطلون جينز تركي سليم فيت",
                sku = "TJR-JN-02",
                barcode = "622100102",
                category = ProductCategory.MEN_CLOTHING,
                purchasePrice = 450.0,
                sellingPrice = 790.0,
                colors = listOf("أزرق داكن", "أسود فاحم", "رمادي ثلجي"),
                sizes = listOf("32", "34", "36", "38"),
                variants = listOf(
                    ProductVariant("v-102-1", "أزرق داكن", "32", 4, 2),
                    ProductVariant("v-102-2", "أزرق داكن", "34", 10, 3),
                    ProductVariant("v-102-3", "أزرق داكن", "36", 1, 3), // Low stock
                    ProductVariant("v-102-4", "أسود فاحم", "32", 6, 2),
                    ProductVariant("v-102-5", "أسود فاحم", "34", 8, 2),
                    ProductVariant("v-102-6", "رمادي ثلجي", "36", 3, 2)
                )
            ),
            Product(
                id = "p-103",
                name = "حذاء رياضي سنيكرز جلد طبيعي خفيف",
                sku = "TJR-SHOE-03",
                barcode = "622100103",
                category = ProductCategory.MEN_SHOES,
                purchasePrice = 650.0,
                sellingPrice = 1150.0,
                colors = listOf("أبيض ملكي", "أسود مط", "جملي دافئ"),
                sizes = listOf("41", "42", "43", "44", "45"),
                variants = listOf(
                    ProductVariant("v-103-1", "أبيض ملكي", "42", 5, 2),
                    ProductVariant("v-103-2", "أبيض ملكي", "43", 4, 2),
                    ProductVariant("v-103-3", "أبيض ملكي", "44", 2, 3), // Low stock
                    ProductVariant("v-103-4", "أسود مط", "42", 6, 2),
                    ProductVariant("v-103-5", "أسود مط", "43", 3, 2),
                    ProductVariant("v-103-6", "جملي دافئ", "41", 2, 2) // Low stock
                )
            ),
            Product(
                id = "p-104",
                name = "تي شيرت بولو قطن مصري 100%",
                sku = "TJR-TS-04",
                barcode = "622100104",
                category = ProductCategory.MEN_CLOTHING,
                purchasePrice = 220.0,
                sellingPrice = 420.0,
                colors = listOf("أخضر زمردي", "أبيض", "كحلي", "عنابي"),
                sizes = listOf("M", "L", "XL", "XXL"),
                variants = listOf(
                    ProductVariant("v-104-1", "أخضر زمردي", "L", 12, 3),
                    ProductVariant("v-104-2", "أخضر زمردي", "XL", 9, 3),
                    ProductVariant("v-104-3", "أبيض", "L", 14, 3),
                    ProductVariant("v-104-4", "كحلي", "M", 8, 3),
                    ProductVariant("v-104-5", "عنابي", "XXL", 4, 2)
                )
            ),
            Product(
                id = "p-105",
                name = "فستان كريب ميدي أنيق بحزام ذهبي",
                sku = "TJR-DR-05",
                barcode = "622100105",
                category = ProductCategory.WOMEN_CLOTHING,
                purchasePrice = 520.0,
                sellingPrice = 980.0,
                colors = listOf("كشمير", "أزرق بترولي", "أسود سواريه"),
                sizes = listOf("38", "40", "42", "44"),
                variants = listOf(
                    ProductVariant("v-105-1", "كشمير", "38", 3, 2),
                    ProductVariant("v-105-2", "كشمير", "40", 2, 3), // Low stock
                    ProductVariant("v-105-3", "أزرق بترولي", "40", 5, 2),
                    ProductVariant("v-105-4", "أسود سواريه", "42", 4, 2)
                )
            ),
            Product(
                id = "p-106",
                name = "حذاء كعب عالي كلاسيك شمواه",
                sku = "TJR-HL-06",
                barcode = "622100106",
                category = ProductCategory.WOMEN_SHOES,
                purchasePrice = 480.0,
                sellingPrice = 850.0,
                colors = listOf("بيج نود", "أسود", "نبيتي"),
                sizes = listOf("37", "38", "39", "40"),
                variants = listOf(
                    ProductVariant("v-106-1", "بيج نود", "38", 4, 2),
                    ProductVariant("v-106-2", "بيج نود", "39", 1, 2), // Low stock
                    ProductVariant("v-106-3", "أسود", "37", 3, 2),
                    ProductVariant("v-106-4", "أسود", "38", 5, 2),
                    ProductVariant("v-106-5", "نبيتي", "39", 2, 2)
                )
            ),
            Product(
                id = "p-107",
                name = "هودي ميلتون شتوي مبطن فرو",
                sku = "TJR-HD-07",
                barcode = "622100107",
                category = ProductCategory.MEN_CLOTHING,
                purchasePrice = 380.0,
                sellingPrice = 690.0,
                colors = listOf("رمادي فحمي", "أسود", "أخضر زيتي"),
                sizes = listOf("L", "XL", "XXL"),
                variants = listOf(
                    ProductVariant("v-107-1", "رمادي فحمي", "L", 7, 2),
                    ProductVariant("v-107-2", "رمادي فحمي", "XL", 5, 2),
                    ProductVariant("v-107-3", "أسود", "XXL", 1, 2) // Low stock
                )
            ),
            Product(
                id = "p-108",
                name = "حزام جلد طبيعي بإبزيم ذهبي كلاسيك",
                sku = "TJR-AC-08",
                barcode = "622100108",
                category = ProductCategory.ACCESSORIES,
                purchasePrice = 110.0,
                sellingPrice = 240.0,
                colors = listOf("أسود", "بني غامق", "هافان"),
                sizes = listOf("110cm", "120cm", "130cm"),
                variants = listOf(
                    ProductVariant("v-108-1", "أسود", "120cm", 15, 3),
                    ProductVariant("v-108-2", "بني غامق", "120cm", 8, 3),
                    ProductVariant("v-108-3", "هافان", "110cm", 6, 2)
                )
            )
        )
        _products.value = initialProducts

        // Initial Customers with balances
        val initialCustomers = listOf(
            Customer(
                id = "c-1",
                name = "م. أحمد عبد العزيز",
                phone = "01012345678",
                address = "شارع الثورة، مصر الجديدة",
                outstandingBalance = 850.0,
                totalPurchases = 4250.0,
                paymentHistory = listOf(
                    CustomerPayment(
                        id = "cp-1",
                        timestamp = now - (oneDay * 3),
                        amount = 1000.0,
                        paymentMethod = PaymentMethod.INSTAPAY,
                        receiptNumber = "PAY-981",
                        notes = "دفعة حساب عبر إنستاباي"
                    )
                ),
                notes = "عميل مميز - يفضل ماركة القمصان الكتان"
            ),
            Customer(
                id = "c-2",
                name = "د. سارة محمود",
                phone = "01287654321",
                address = "المعادي، القاهرة",
                outstandingBalance = 0.0,
                totalPurchases = 6120.0,
                notes = "سداد فوري كاش / فيزا دائماً"
            ),
            Customer(
                id = "c-3",
                name = "أ. كريم الشناوي",
                phone = "01155443322",
                address = "مدينة نصر، عباس العقاد",
                outstandingBalance = 1400.0,
                totalPurchases = 3800.0,
                paymentHistory = listOf(
                    CustomerPayment(
                        id = "cp-2",
                        timestamp = now - (oneDay * 5),
                        amount = 500.0,
                        paymentMethod = PaymentMethod.VODAFONE_CASH,
                        receiptNumber = "PAY-764",
                        notes = "تحويل فودافون كاش"
                    )
                ),
                notes = "مشتريات ملابس وأحذية سنيكرز"
            ),
            Customer(
                id = "c-4",
                name = "أ. منى إبراهيم",
                phone = "01509876543",
                address = "التجمع الخامس",
                outstandingBalance = 380.0,
                totalPurchases = 2940.0,
                notes = "تشتري فساتين وأحذية حريمي"
            ),
            Customer(
                id = "c-5",
                name = "مصطفى الجوهري",
                phone = "01067891234",
                address = "المهندسين، الجيزة",
                outstandingBalance = 0.0,
                totalPurchases = 1850.0
            )
        )
        _customers.value = initialCustomers

        // Initial Sales
        val initialSales = listOf(
            Sale(
                id = "s-1001",
                invoiceNumber = "INV-2026-001",
                timestamp = now - (3600000L * 2), // 2 hours ago today
                items = listOf(
                    CartItem(
                        product = initialProducts[0],
                        variant = initialProducts[0].variants[0],
                        quantity = 1,
                        unitPrice = 580.0
                    ),
                    CartItem(
                        product = initialProducts[1],
                        variant = initialProducts[1].variants[1],
                        quantity = 1,
                        unitPrice = 790.0
                    )
                ),
                subtotal = 1370.0,
                discount = 70.0,
                total = 1300.0,
                paidAmount = 1300.0,
                remainingBalance = 0.0,
                paymentMethod = PaymentMethod.INSTAPAY,
                customerId = "c-2",
                customerName = "د. سارة محمود",
                customerPhone = "01287654321"
            ),
            Sale(
                id = "s-1002",
                invoiceNumber = "INV-2026-002",
                timestamp = now - (3600000L * 4), // 4 hours ago today
                items = listOf(
                    CartItem(
                        product = initialProducts[2],
                        variant = initialProducts[2].variants[0],
                        quantity = 1,
                        unitPrice = 1150.0
                    )
                ),
                subtotal = 1150.0,
                discount = 0.0,
                total = 1150.0,
                paidAmount = 1150.0,
                remainingBalance = 0.0,
                paymentMethod = PaymentMethod.VODAFONE_CASH,
                customerName = "عميل نقدي"
            ),
            Sale(
                id = "s-1003",
                invoiceNumber = "INV-2026-003",
                timestamp = now - (3600000L * 6), // 6 hours ago today
                items = listOf(
                    CartItem(
                        product = initialProducts[3],
                        variant = initialProducts[3].variants[0],
                        quantity = 2,
                        unitPrice = 420.0
                    ),
                    CartItem(
                        product = initialProducts[7],
                        variant = initialProducts[7].variants[0],
                        quantity = 1,
                        unitPrice = 240.0
                    )
                ),
                subtotal = 1080.0,
                discount = 80.0,
                total = 1000.0,
                paidAmount = 1000.0,
                remainingBalance = 0.0,
                paymentMethod = PaymentMethod.CASH,
                customerName = "عميل نقدي"
            ),
            Sale(
                id = "s-1004",
                invoiceNumber = "INV-2026-004",
                timestamp = now - oneDay, // yesterday
                items = listOf(
                    CartItem(
                        product = initialProducts[4],
                        variant = initialProducts[4].variants[0],
                        quantity = 1,
                        unitPrice = 980.0
                    ),
                    CartItem(
                        product = initialProducts[5],
                        variant = initialProducts[5].variants[0],
                        quantity = 1,
                        unitPrice = 850.0
                    )
                ),
                subtotal = 1830.0,
                discount = 130.0,
                total = 1700.0,
                paidAmount = 1700.0,
                remainingBalance = 0.0,
                paymentMethod = PaymentMethod.CREDIT_CARD,
                customerId = "c-4",
                customerName = "أ. منى إبراهيم",
                customerPhone = "01509876543"
            ),
            Sale(
                id = "s-1005",
                invoiceNumber = "INV-2026-005",
                timestamp = now - (oneDay * 2),
                items = listOf(
                    CartItem(
                        product = initialProducts[1],
                        variant = initialProducts[1].variants[0],
                        quantity = 2,
                        unitPrice = 790.0
                    )
                ),
                subtotal = 1580.0,
                discount = 0.0,
                total = 1580.0,
                paidAmount = 730.0,
                remainingBalance = 850.0,
                paymentMethod = PaymentMethod.DEBT,
                customerId = "c-1",
                customerName = "م. أحمد عبد العزيز",
                customerPhone = "01012345678",
                notes = "تم دفع 730 نقدي والباقي 850 آجل"
            ),
            Sale(
                id = "s-1006",
                invoiceNumber = "INV-2026-006",
                timestamp = now - (oneDay * 3),
                items = listOf(
                    CartItem(
                        product = initialProducts[2],
                        variant = initialProducts[2].variants[1],
                        quantity = 1,
                        unitPrice = 1150.0
                    ),
                    CartItem(
                        product = initialProducts[0],
                        variant = initialProducts[0].variants[1],
                        quantity = 1,
                        unitPrice = 580.0
                    )
                ),
                subtotal = 1730.0,
                discount = 50.0,
                total = 1680.0,
                paidAmount = 1680.0,
                remainingBalance = 0.0,
                paymentMethod = PaymentMethod.CASH,
                customerName = "عميل نقدي"
            ),
            Sale(
                id = "s-1007",
                invoiceNumber = "INV-2026-007",
                timestamp = now - (oneDay * 4),
                items = listOf(
                    CartItem(
                        product = initialProducts[6],
                        variant = initialProducts[6].variants[0],
                        quantity = 2,
                        unitPrice = 690.0
                    )
                ),
                subtotal = 1380.0,
                discount = 80.0,
                total = 1300.0,
                paidAmount = 1300.0,
                remainingBalance = 0.0,
                paymentMethod = PaymentMethod.INSTAPAY,
                customerName = "عميل نقدي"
            )
        )
        _sales.value = initialSales

        // Expenses
        val initialExpenses = listOf(
            Expense("e-1", "إيجار المحل لشهر نوفمبر", ExpenseCategory.RENT, 12000.0, now - (oneDay * 4), "تم الدفع لمالك العقار"),
            Expense("e-2", "فاتورة كهرباء وتكييفات المحل", ExpenseCategory.UTILITIES, 1850.0, now - (oneDay * 2), "شركة كهرباء شمال القاهرة"),
            Expense("e-3", "شحن كراتين بضاعة من مصانع المحلة", ExpenseCategory.SUPPLIES, 450.0, now - oneDay, "شركة شحن سريع"),
            Expense("e-4", "ضيافة عملاء وبوفيه ومستلزمات نظافة", ExpenseCategory.HOSPITALITY, 320.0, now - (3600000L * 5), "قهوة وشاي وأكياس تغليف تاجِر"),
            Expense("e-5", "إعلانات سبونسر إنستجرام وفيسبوك", ExpenseCategory.MARKETING, 1200.0, now - (oneDay * 3), "حملة الجمعة البيضاء للأزياء")
        )
        _expenses.value = initialExpenses

        // Returns
        val initialReturns = listOf(
            ReturnRecord(
                id = "ret-1",
                returnNumber = "RET-001",
                originalInvoiceNumber = "INV-2026-006",
                timestamp = now - (oneDay * 1),
                items = listOf(
                    ReturnItem(
                        productId = "p-101",
                        productName = "قميص كتان أوفر سايز كاجوال",
                        variantId = "v-101-2",
                        color = "أبيض",
                        size = "L",
                        quantity = 1,
                        refundAmount = 580.0,
                        reason = ReturnReason.SIZE_UNSUITABLE
                    )
                ),
                totalRefund = 580.0,
                notes = "استبدال بمقاس XL مع دفع فرق سعر رمزي"
            )
        )
        _returns.value = initialReturns
    }

    // Products Operations
    fun addProduct(product: Product) {
        val current = _products.value.toMutableList()
        current.add(0, product)
        _products.value = current
    }

    fun updateProduct(product: Product) {
        val current = _products.value.toMutableList()
        val index = current.indexOfFirst { it.id == product.id }
        if (index != -1) {
            current[index] = product
            _products.value = current
        }
    }

    fun deleteProduct(productId: String) {
        _products.value = _products.value.filter { it.id != productId }
    }

    // Sales Operations
    fun createSale(sale: Sale): Sale {
        // 1. Decrement inventory quantities for each sold variant
        val productList = _products.value.toMutableList()
        for (item in sale.items) {
            val pIdx = productList.indexOfFirst { it.id == item.product.id }
            if (pIdx != -1) {
                val p = productList[pIdx]
                val updatedVariants = p.variants.map { v ->
                    if (v.id == item.variant.id) {
                        val newQty = (v.quantity - item.quantity).coerceAtLeast(0)
                        v.copy(quantity = newQty)
                    } else v
                }
                productList[pIdx] = p.copy(variants = updatedVariants)
            }
        }
        _products.value = productList

        // 2. Add to sales list
        val salesList = _sales.value.toMutableList()
        salesList.add(0, sale)
        _sales.value = salesList

        // 3. Update customer balance if debt
        if (!sale.customerId.isNullOrBlank() && sale.remainingBalance > 0) {
            val customerList = _customers.value.toMutableList()
            val cIdx = customerList.indexOfFirst { it.id == sale.customerId }
            if (cIdx != -1) {
                val c = customerList[cIdx]
                customerList[cIdx] = c.copy(
                    outstandingBalance = c.outstandingBalance + sale.remainingBalance,
                    totalPurchases = c.totalPurchases + sale.total
                )
                _customers.value = customerList
            }
        } else if (!sale.customerId.isNullOrBlank()) {
            val customerList = _customers.value.toMutableList()
            val cIdx = customerList.indexOfFirst { it.id == sale.customerId }
            if (cIdx != -1) {
                val c = customerList[cIdx]
                customerList[cIdx] = c.copy(
                    totalPurchases = c.totalPurchases + sale.total
                )
                _customers.value = customerList
            }
        }

        return sale
    }

    // Customer Operations
    fun addCustomer(customer: Customer) {
        val current = _customers.value.toMutableList()
        current.add(0, customer)
        _customers.value = current
    }

    fun recordCustomerPayment(
        customerId: String,
        amount: Double,
        method: PaymentMethod,
        notes: String
    ): Boolean {
        val customerList = _customers.value.toMutableList()
        val cIdx = customerList.indexOfFirst { it.id == customerId }
        if (cIdx != -1) {
            val c = customerList[cIdx]
            val newBalance = (c.outstandingBalance - amount).coerceAtLeast(0.0)
            val payment = CustomerPayment(
                id = "cp-${System.currentTimeMillis()}",
                timestamp = System.currentTimeMillis(),
                amount = amount,
                paymentMethod = method,
                receiptNumber = "RCP-${(100..999).random()}",
                notes = notes
            )
            val updatedHistory = listOf(payment) + c.paymentHistory
            customerList[cIdx] = c.copy(
                outstandingBalance = newBalance,
                paymentHistory = updatedHistory
            )
            _customers.value = customerList
            return true
        }
        return false
    }

    // Expense Operations
    fun addExpense(expense: Expense) {
        val current = _expenses.value.toMutableList()
        current.add(0, expense)
        _expenses.value = current
    }

    fun deleteExpense(id: String) {
        _expenses.value = _expenses.value.filter { it.id != id }
    }

    // Returns Operations
    fun processReturn(record: ReturnRecord) {
        // Restock items
        val productList = _products.value.toMutableList()
        for (item in record.items) {
            val pIdx = productList.indexOfFirst { it.id == item.productId }
            if (pIdx != -1) {
                val p = productList[pIdx]
                val updatedVariants = p.variants.map { v ->
                    if (v.id == item.variantId) {
                        v.copy(quantity = v.quantity + item.quantity)
                    } else v
                }
                productList[pIdx] = p.copy(variants = updatedVariants)
            }
        }
        _products.value = productList

        // Add return record
        val current = _returns.value.toMutableList()
        current.add(0, record)
        _returns.value = current
    }

    fun updateStoreProfile(profile: StoreProfile) {
        _storeProfile.value = profile
    }
}
