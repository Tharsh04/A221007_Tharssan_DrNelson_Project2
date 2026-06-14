package com.example.a221007_tharssan_drnelson_project2

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a221007_tharssan_drnelson_project2.data.FoodDonation
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(totalAmount: String, donationData: FoodDonation?, onComplete: () -> Unit, onCancel: () -> Unit) {
    var cardNumber by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    var isProcessing by remember { mutableStateOf(false) }
    var showReceipt by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Dropdown state logic parameters
    var dropdownExpanded by remember { mutableStateOf(false) }
    val validExpiryDatesList = remember {
        listOf("06/26", "12/26", "03/27", "08/27", "12/27", "05/28", "10/28", "12/28", "01/29", "06/29", "12/29")
    }

    LaunchedEffect(isProcessing) {
        if (isProcessing) {
            delay(2000)
            isProcessing = false
            showReceipt = true
        }
    }

    Crossfade(targetState = showReceipt, label = "") { isVisible ->
        if (isVisible) {
            Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(24.dp)) {
                        Text("Donation Receipt", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        HorizontalDivider(Modifier.padding(vertical = 12.dp))
                        Text("Donor Account: ${donationData?.donorName}")
                        Text("Charity Target: ${donationData?.charityName}")
                        Text("Total Amount: $totalAmount", fontWeight = FontWeight.Bold, color = Color(0xFFEA580C))
                    }
                }
                Button(onClick = onComplete, Modifier.padding(top = 24.dp).fillMaxWidth(), colors = ButtonDefaults.buttonColors(Color(0xFFEA580C))) { Text("Finish") }
            }
        } else {
            Column(Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Secure Gateway", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Surface(color = Color(0xFFFFF7ED), modifier = Modifier.padding(vertical = 24.dp).fillMaxWidth()) {
                    Text(totalAmount, fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color(0xFFEA580C), modifier = Modifier.padding(16.dp))
                }

                // 1. Card Number Field
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { input ->
                        val digitsOnly = input.filter { it.isDigit() }
                        if (digitsOnly.length <= 16) {
                            cardNumber = digitsOnly
                            errorMessage = null
                        }
                    },
                    label = { Text("Card Number (16 Digits)") },
                    placeholder = { Text("0000000000000000") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null && cardNumber.length < 16,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Row(Modifier.padding(top = 12.dp), verticalAlignment = Alignment.Top) {
                    // 2. FIXED: Automated Exposed Dropdown Menu Box Selection Layout Block
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = !dropdownExpanded },
                        modifier = Modifier.weight(1.2f)
                    ) {
                        OutlinedTextField(
                            value = expiry,
                            onValueChange = {},
                            readOnly = true, // Prevents manual keyboard input completely
                            label = { Text("Expiry Date") },
                            placeholder = { Text("Select MM/YY") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            isError = errorMessage != null && expiry.isEmpty(),
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            validExpiryDatesList.forEach { dateSelection ->
                                DropdownMenuItem(
                                    text = { Text(text = dateSelection, fontSize = 15.sp) },
                                    onClick = {
                                        expiry = dateSelection
                                        dropdownExpanded = false
                                        errorMessage = null
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }

                    Spacer(Modifier.width(8.dp))

                    // 3. CVV Field
                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { input ->
                            val digitsOnly = input.filter { it.isDigit() }
                            if (digitsOnly.length <= 3) {
                                cvv = digitsOnly
                                errorMessage = null
                            }
                        },
                        label = { Text("CVV") },
                        placeholder = { Text("000") },
                        modifier = Modifier.weight(0.8f),
                        visualTransformation = PasswordVisualTransformation(),
                        isError = errorMessage != null && cvv.length < 3,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                if (isProcessing) {
                    CircularProgressIndicator(Modifier.padding(top = 32.dp), color = Color(0xFFEA580C))
                } else {
                    Button(
                        onClick = {
                            val isCardValid = cardNumber.length == 16
                            val isExpiryValid = expiry.isNotEmpty()
                            val isCvvValid = cvv.length == 3

                            if (isCardValid && isExpiryValid && isCvvValid) {
                                errorMessage = null
                                isProcessing = true
                            } else {
                                errorMessage = when {
                                    !isCardValid -> "Invalid Card Number! Must be exactly 16 digits."
                                    !isExpiryValid -> "Please select a valid Expiry Date from the dropdown."
                                    !isCvvValid -> "Invalid CVV! Security code must be exactly 3 digits."
                                    else -> "Please fulfill all transaction criteria details completely."
                                }
                            }
                        },
                        modifier = Modifier.padding(top = 32.dp).fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Color(0xFFEA580C))
                    ) {
                        Text("Confirm Payment")
                    }

                    TextButton(onClick = onCancel) { Text("Cancel", color = Color.Gray) }
                }
            }
        }
    }
}