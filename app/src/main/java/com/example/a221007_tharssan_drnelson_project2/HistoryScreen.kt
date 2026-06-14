package com.example.a221007_tharssan_drnelson_project2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a221007_tharssan_drnelson_project2.data.FoodDonation

@Composable
fun HistoryScreen(viewModel: DonorViewModel) {
    val history = viewModel.donationHistory

    Column(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth().background(Color(0xFFEA580C)).padding(24.dp)) {
            Text("My Impact History", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        if (history.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No donations yet recorded offline.", color = Color.Gray)
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(history.reversed()) { donation ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(48.dp).background(Color(0xFFFFF7ED), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.VolunteerActivism, null, tint = Color(0xFFEA580C))
                            }
                            Column(Modifier.padding(start = 16.dp).weight(1f)) {
                                Text(donation.charityName, fontWeight = FontWeight.Bold)
                                Text(donation.packageType, fontSize = 13.sp, color = Color.Gray)
                            }
                            Text("+${donation.totalItemCount} Items", color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}