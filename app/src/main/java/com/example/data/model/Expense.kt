package com.example.data.model

enum class ExpenseCategory(val titleAr: String) {
    RENT("إيجار المحل"),
    UTILITIES("كهرباء ومياه وإنترنت"),
    SALARIES("رواتب الموظفين"),
    SUPPLIES("شحن ونقل بضاعة"),
    HOSPITALITY("ضيافة ومستلزمات نظافة"),
    MARKETING("تسويق وإعلانات"),
    OTHER("مصروفات أخرى")
}

data class Expense(
    val id: String,
    val title: String,
    val category: ExpenseCategory,
    val amount: Double,
    val timestamp: Long,
    val notes: String = ""
)
