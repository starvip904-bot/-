package com.example.data.model

enum class ReturnReason(val titleAr: String) {
    SIZE_UNSUITABLE("المقاس غير مناسب"),
    DEFECTIVE("وجود عيب صناعة أو تلف"),
    CUSTOMER_PREFERENCE("رغبة العميل في استبدال الموديل"),
    OTHER("سبب آخر")
}

data class ReturnItem(
    val productId: String,
    val productName: String,
    val variantId: String,
    val color: String,
    val size: String,
    val quantity: Int,
    val refundAmount: Double,
    val reason: ReturnReason
)

data class ReturnRecord(
    val id: String,
    val returnNumber: String,
    val originalInvoiceNumber: String,
    val timestamp: Long,
    val items: List<ReturnItem>,
    val totalRefund: Double,
    val notes: String = ""
)
