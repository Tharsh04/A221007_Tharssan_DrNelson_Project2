package com.example.a221007_tharssan_drnelson_project2

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import coil.compose.rememberAsyncImagePainter
import com.example.a221007_tharssan_drnelson_project2.data.CharityCampaign
import com.example.a221007_tharssan_drnelson_project2.data.charityCampaigns

@Composable
fun HomeScreen(viewModel: DonorViewModel, onNavigate: (String) -> Unit) {
    val user = viewModel.currentUser

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Box(Modifier.fillMaxWidth().background(Color(0xFFEA580C)).padding(24.dp)) {
                Column {
                    val firstName = user?.name?.split(" ")?.firstOrNull() ?: "Donor"
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.TopCenter) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("FeedForward", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.Favorite, "Logo", tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                    }
                    Text("Hello $firstName!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Matric Number: ${user?.matric ?: "A221007"}", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, null, tint = Color.White, modifier = Modifier.size(32.dp))
                            Column(Modifier.padding(start = 12.dp)) {
                                Text("Global Live Meals Shared (All Accounts)", color = Color.White, fontSize = 12.sp)
                                Text("${viewModel.globalMealsCount}", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }

            // Simple transition straight into the primary campaign section header
            Text(
                text = "ACTIVE CAMPAIGNS",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 8.dp),
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        items(charityCampaigns) { campaign ->
            CharityGoalCard(campaign = campaign, onDonate = { onNavigate("donate-food") })
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun CharityGoalCard(campaign: CharityCampaign, onDonate: () -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth().animateContentSize(spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box {
                Image(painter = rememberAsyncImagePainter(campaign.imageUrl), null, modifier = Modifier.height(160.dp).fillMaxWidth(), contentScale = ContentScale.Crop)
                if (campaign.isUrgent) {
                    Surface(color = Color.Red, shape = RoundedCornerShape(4.dp), modifier = Modifier.padding(8.dp).align(Alignment.TopEnd)) {
                        Text("URGENT", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
            Column(Modifier.padding(16.dp)) {
                Text(campaign.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(campaign.description, fontSize = 14.sp, color = Color.Gray, maxLines = if (isExpanded) Int.MAX_VALUE else 2, overflow = TextOverflow.Ellipsis)
                if (isExpanded) {
                    Spacer(Modifier.height(8.dp))
                    Text("Top Wishlist Items:", fontWeight = FontWeight.Bold, color = Color(0xFF9A3412), fontSize = 12.sp)
                    Row(Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        campaign.wishlist.forEach { item -> SuggestionChip(onClick = {}, label = { Text(item, fontSize = 10.sp) }) }
                    }
                    Text("Mission:", fontWeight = FontWeight.Bold, color = Color(0xFF9A3412), modifier = Modifier.padding(top = 8.dp))
                    Text(campaign.fullMission, fontSize = 13.sp, color = Color.DarkGray)
                }
                Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { isExpanded = !isExpanded }) {
                        Text(if (isExpanded) "Less" else "Learn More", color = Color(0xFFEA580C))
                        Icon(if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, tint = Color(0xFFEA580C))
                    }
                    Button(onClick = onDonate, colors = ButtonDefaults.buttonColors(Color(0xFFEA580C))) { Text("Donate Now") }
                }
            }
        }
    }
}