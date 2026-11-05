package com.example.data.model

data class ProductVariant(
    val id: String,
    val color: String,
    val size: String,
    val quantity: Int,
    val lowStockThreshold: Int = 3
)

enum class ProductCategory(val titleAr: String) {
    MEN_CLOTHING("ملابس رجالي"),
    WOMEN_CLOTHING("ملابس حريمي"),
    KIDS_CLOTHING("ملابس أطفال"),
    MEN_SHOES("أحذية رجالي"),
    WOMEN_SHOES("أحذية حريمي"),
    ACCESSORIES("إكسسوارات وحقائب")
}

data class Product(
    val id: String,
    val name: String,
    val sku: String,
    val barcode: String,
    val category: ProductCategory,
    val purchasePrice: Double, // سعر الشراء / التكلفة
    val sellingPrice: Double,  // سعر البيع
    val colors: List<String>,
    val sizes: List<String>,
    val variants: List<ProductVariant>,
    val imageUrl: String? = null
) {
    val totalStock: Int
        get() = variants.sumOf { it.quantity }

    val isLowStock: Boolean
        get() = variants.any { it.quantity <= it.lowStockThreshold }

    val lowStockVariantsCount: Int
        get() = variants.count { it.quantity <= it.lowStockThreshold }
}
