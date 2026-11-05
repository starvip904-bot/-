package com.example.data.model

enum class PaymentMethod(val titleAr: String, val badgeColorHex: Long) {
    CASH("كاش (نقدي)", 0xFF126B4A),
    VODAFONE_CASH("فودافون كاش", 0xFFE60000),
    INSTAPAY("إنستاباي (InstaPay)", 0xFF632B90),
    CREDIT_CARD("فيزا / ماستركارد", 0xFF1E78D6),
    DEBT("آجل (على الحساب)", 0xFFE57A00)
}

enum class SaleStatus(val titleAr: String) {
    COMPLETED("مكتملة"),
    PARTIALLY_PAID("سداد جزئي"),
    RETURNED("مسترجعة"),
    CANCELLED("ملغاة")
}

data class CartItem(
    val product: Product,
    val variant: ProductVariant,
    val quantity: Int,
    val unitPrice: Double
) {
    val totalPrice: Double
        get() = unitPrice * quantity
}

data class Sale(
    val id: String,
    val invoiceNumber: String,
    val timestamp: Long,
    val items: List<CartItem>,
    val subtotal: Double,
    val discount: Double,
    val total: Double,
    val paidAmount: Double,
    val remainingBalance: Double, // الديون المتبقية إذا كان آجل
    val paymentMethod: PaymentMethod,
    val customerId: String? = null,
    val customerName: String? = "عميل نقدي",
    val customerPhone: String? = null,
    val notes: String = "",
    val status: SaleStatus = SaleStatus.COMPLETED
)
