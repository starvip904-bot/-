package com.example.data.model

data class CustomerPayment(
    val id: String,
    val timestamp: Long,
    val amount: Double,
    val paymentMethod: PaymentMethod,
    val receiptNumber: String,
    val notes: String = ""
)

data class Customer(
    val id: String,
    val name: String,
    val phone: String,
    val address: String = "",
    val outstandingBalance: Double = 0.0, // الرصيد المتبقي / الديون
    val totalPurchases: Double = 0.0,
    val paymentHistory: List<CustomerPayment> = emptyList(),
    val notes: String = ""
)
