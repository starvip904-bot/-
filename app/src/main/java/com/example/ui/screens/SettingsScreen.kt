package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.StoreProfile
import com.example.ui.theme.*
import com.example.ui.viewmodel.TajerViewModel

@Composable
fun SettingsScreen(
    viewModel: TajerViewModel,
    modifier: Modifier = Modifier
) {
    val storeProfile by viewModel.storeProfile.collectAsState()

    var storeName by remember(storeProfile) { mutableStateOf(storeProfile.storeName) }
    var branchName by remember(storeProfile) { mutableStateOf(storeProfile.branchName) }
    var phone by remember(storeProfile) { mutableStateOf(storeProfile.phone) }
    var taxNumber by remember(storeProfile) { mutableStateOf(storeProfile.taxNumber) }
    var returnPolicy by remember(storeProfile) { mutableStateOf(storeProfile.returnPolicyNotice) }
    var thankYouMessage by remember(storeProfile) { mutableStateOf(storeProfile.thankYouMessage) }

    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TajerBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "إعدادات المتجر وبيانات الفاتورة",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TajerTextPrimary
        )
        Text(
            text = "تخصيص بيانات المحل، سياسة الاسترجاع المطبوعة، وإدارة البيانات النموذجية",
            style = MaterialTheme.typography.bodySmall,
            color = TajerTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Store Profile Form Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "بيانات المنشأة والفواتير:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TajerTextPrimary
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = storeName,
                    onValueChange = { storeName = it },
                    label = { Text("اسم المتجر أو العلامة التجارية") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = branchName,
                    onValueChange = { branchName = it },
                    label = { Text("اسم الفرع والعنوان في مصر") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("رقم هاتف المتجر") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = taxNumber,
                        onValueChange = { taxNumber = it },
                        label = { Text("الرقم الضريبي أو السجل") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = returnPolicy,
                    onValueChange = { returnPolicy = it },
                    label = { Text("سياسة الاستبدال والاسترجاع (المطبوعة على الإيصال)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = thankYouMessage,
                    onValueChange = { thankYouMessage = it },
                    label = { Text("رسالة شكر أسفل الفاتورة") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.updateStoreProfile(
                            StoreProfile(
                                storeName = storeName,
                                branchName = branchName,
                                phone = phone,
                                taxNumber = taxNumber,
                                returnPolicyNotice = returnPolicy,
                                thankYouMessage = thankYouMessage
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "حفظ بيانات المتجر", color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Backend Architecture Note
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudSync, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "هيكلية البيانات السحابية (Supabase Ready)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "تم بناء جميع نماذج البيانات (المنتجات، الفواتير، المقاسات، العملاء، والمصروفات) وفق أفضل معايير Clean Architecture في مجلد com.example.data لتكون جاهزة للربط الفوري مع قاعدة بيانات Supabase / PostgreSQL و Realtime Sync في أي وقت دون تعديل واجهات المستخدم.",
                    style = MaterialTheme.typography.bodySmall,
                    color = EmeraldDark
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reset Sample Data
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "البيانات التجريبية للعرض:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TajerTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "يمكنك إعادة تعيين البيانات واستعادة بيانات متجر الملابس والأحذية المصرية التجريبية بكامل فواتيرها ومنتجاتها في أي وقت.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TajerTextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmGoldDark)
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "استعادة البيانات التجريبية الافتراضية")
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(text = "استعادة البيانات التجريبية") },
            text = { Text(text = "هل ترغب في إعادة تعيين كافة المنتجات والفواتير والمصروفات إلى البيانات النموذجية الأولية؟") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetDemoData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("تأكيد الاستعادة", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
