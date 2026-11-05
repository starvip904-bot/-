package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CartItem
import com.example.data.model.PaymentMethod
import com.example.data.model.Sale
import com.example.data.repository.TajerRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app_name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("تاجِر", appName)
    }

    @Test
    fun `repository initial data seeded correctly`() {
        val repository = TajerRepository()
        val products = repository.products.value
        val customers = repository.customers.value
        val sales = repository.sales.value

        assertTrue("Products should be populated", products.isNotEmpty())
        assertTrue("Customers should be populated", customers.isNotEmpty())
        assertTrue("Sales should be populated", sales.isNotEmpty())

        val product = products.first()
        assertTrue("Product must have variants", product.variants.isNotEmpty())
        assertTrue("Stock must be greater than zero", product.totalStock > 0)
    }

    @Test
    fun `create sale decrements inventory variant and records sale`() {
        val repository = TajerRepository()
        val product = repository.products.value.first()
        val variant = product.variants.first()
        val initialVariantStock = variant.quantity

        val sale = Sale(
            id = "test-sale-1",
            invoiceNumber = "INV-TEST-001",
            timestamp = System.currentTimeMillis(),
            items = listOf(
                CartItem(
                    product = product,
                    variant = variant,
                    quantity = 2,
                    unitPrice = product.sellingPrice
                )
            ),
            subtotal = product.sellingPrice * 2,
            discount = 0.0,
            total = product.sellingPrice * 2,
            paidAmount = product.sellingPrice * 2,
            remainingBalance = 0.0,
            paymentMethod = PaymentMethod.CASH,
            customerName = "عميل نقدي"
        )

        repository.createSale(sale)

        val updatedProduct = repository.products.value.find { it.id == product.id }
        assertNotNull(updatedProduct)
        val updatedVariant = updatedProduct!!.variants.find { it.id == variant.id }
        assertEquals(initialVariantStock - 2, updatedVariant?.quantity)
    }
}
