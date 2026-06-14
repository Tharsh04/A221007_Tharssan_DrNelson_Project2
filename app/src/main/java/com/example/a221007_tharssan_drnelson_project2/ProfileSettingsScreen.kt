package com.example.a221007_tharssan_drnelson_project2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*

@Composable
fun ProfileSettingsScreen(donorName: String, donorEmail: String, donorMatric: String, onSaveProfile: (String, String, String) -> Unit, onLogout: () -> Unit, onBack: () -> Unit) {
    var isEditing by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(donorName) }
    var tempEmail by remember { mutableStateOf(donorEmail) }
    var tempMatric by remember { mutableStateOf(donorMatric) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }

        Column(Modifier.fillMaxWidth().padding(vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(80.dp).background(Color(0xFFEA580C), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(40.dp))
            }
            Text("My Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                EditableProfileRow(Icons.Default.Person, "Name", tempName, isEditing) { tempName = it }
                EditableProfileRow(Icons.Default.Email, "Email", tempEmail, isEditing) { tempEmail = it }
                EditableProfileRow(Icons.Default.Badge, "Matric", tempMatric, isEditing) { tempMatric = it }

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { if (isEditing) onSaveProfile(tempName, tempEmail, tempMatric); isEditing = !isEditing },
                    Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(Color(0xFFEA580C))
                ) {
                    Icon(if (isEditing) Icons.Default.Save else Icons.Default.Edit, null)
                    Text(if (isEditing) " Save Changes" else " Edit Profile Details", modifier = Modifier.padding(start = 8.dp))
                }

                if (!isEditing) {
                    OutlinedButton(onClick = onLogout, Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)) {
                        Text("Logout Account")
                    }
                }
            }
        }
    }
}

@Composable
fun EditableProfileRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, isEditing: Boolean, onValueChange: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(24.dp))
        Column(Modifier.padding(start = 12.dp).weight(1f)) {
            Text(label, fontSize = 11.sp, color = Color.Gray)
            if (isEditing) {
                OutlinedTextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(), singleLine = true)
            } else {
                Text(value, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            }
        }
    }
}