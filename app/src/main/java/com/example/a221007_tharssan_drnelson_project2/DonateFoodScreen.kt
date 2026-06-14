package com.example.a221007_tharssan_drnelson_project2

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a221007_tharssan_drnelson_project2.data.FoodDonation
import com.example.a221007_tharssan_drnelson_project2.data.charityCampaigns
import java.text.SimpleDateFormat
import java.util.*

data class FoodPackageItem(val id: String, val name: String, val description: String, val items: List<String>, val color: Color)

@Composable
fun DonateFoodScreen(donorName: String, donorEmail: String, onDonate: (FoodDonation) -> Unit, onBack: () -> Unit) {
    var selectedPackage by remember { mutableStateOf<String?>(null) }
    var selectedCharity by remember { mutableStateOf("") }
    var itemQuantities by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

    val foodPackages = listOf(
        FoodPackageItem("emergency", "Emergency Food Package", "Essential items for immediate relief", listOf("Canned goods", "Rice & pasta", "Dried beans", "Bottled water"), Color(0xFFDC2626)),
        FoodPackageItem("basic", "Basic Grocery Package", "Weekly staples for a family", listOf("Fresh vegetables", "Fruits", "Bread", "Milk", "Eggs"), Color(0xFF16A34A)),
        FoodPackageItem("baby", "Baby Food Package", "Nutrition essentials for infants", listOf("Infant formula", "Baby cereal", "Pureed fruits", "Baby snacks"), Color(0xFFDB2777))
    )

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                Text("Back", color = Color.Gray)
            }
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Donate Food Packages", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Select Charity Target", fontWeight = FontWeight.Bold)
                    charityCampaigns.forEach { charity ->
                        Row(Modifier.fillMaxWidth().clickable { selectedCharity = charity.id }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selectedCharity == charity.id, onClick = { selectedCharity = charity.id }, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFEA580C)))
                            Text(charity.organizationName, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
        }
        items(foodPackages.size) { index ->
            val pkg = foodPackages[index]
            val isSelected = selectedPackage == pkg.id
            Card(
                modifier = Modifier.fillMaxWidth().clickable { selectedPackage = pkg.id; itemQuantities = pkg.items.associateWith { 1 } },
                colors = CardDefaults.cardColors(containerColor = if (isSelected) pkg.color.copy(alpha = 0.1f) else Color.White)
            ) {
                Row(modifier = Modifier.border(2.dp, if (isSelected) pkg.color else Color.Transparent, RoundedCornerShape(12.dp)).padding(16.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text(pkg.name, fontWeight = FontWeight.Bold)
                        Text(pkg.description, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
        item {
            val currentPkg = foodPackages.find { it.id == selectedPackage }
            if (currentPkg != null && selectedCharity.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Customize Quantities", fontWeight = FontWeight.Bold)
                        currentPkg.items.forEach { item ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(item)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = {
                                        val c = itemQuantities[item] ?: 1
                                        if (c > 1) itemQuantities = itemQuantities + (item to c - 1)
                                    }) { Icon(Icons.Default.Remove, null) }
                                    Text((itemQuantities[item] ?: 1).toString(), fontWeight = FontWeight.Bold)
                                    IconButton(onClick = {
                                        val c = itemQuantities[item] ?: 1
                                        itemQuantities = itemQuantities + (item to c + 1)
                                    }) { Icon(Icons.Default.Add, null) }
                                }
                            }
                        }
                    }
                }
                Button(
                    onClick = {
                        val charity = charityCampaigns.find { it.id == selectedCharity } ?: return@Button
                        val flatItemsJson = com.google.gson.Gson().toJson(itemQuantities)

                        onDonate(FoodDonation(
                            id = UUID.randomUUID().toString(), donorName = donorName, donorEmail = donorEmail,
                            charityName = charity.organizationName, packageType = currentPkg.name, itemsJson = flatItemsJson,
                            totalItemCount = itemQuantities.values.sum(), date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        ))
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFFEA580C))
                ) { Text("Proceed to Secure Payment →") }
            }
        }
    }
}