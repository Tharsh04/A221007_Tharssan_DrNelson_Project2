package com.example.a221007_tharssan_drnelson_project2

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a221007_tharssan_drnelson_project2.data.User

@Composable
fun LoginScreen(onLoginClick: (String, String) -> Boolean, onNavigateToRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Favorite, null, tint = Color(0xFFEA580C), modifier = Modifier.size(64.dp))
        Text("FeedForward", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEA580C))
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; loginError = false },
            label = { Text("Gmail Address") },
            modifier = Modifier.fillMaxWidth(),
            isError = loginError
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; loginError = false },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = loginError
        )

        if (loginError) {
            Text("Invalid Gmail or Password", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
        }

        Button(
            onClick = {
                val success = onLoginClick(email, password)
                if (!success) loginError = true
            },
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFFEA580C))
        ) { Text("Login") }

        TextButton(onClick = onNavigateToRegister) {
            Text("New user? Create an account", color = Color.Gray)
        }
    }
}

@Composable
fun RegisterScreen(onRegisterAttempt: (User, (String?) -> Unit) -> Unit, onBackToLogin: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var matric by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val isEmailValid = email.endsWith("@gmail.com") && email.length > 10
    val passwordsMatch = password == confirmPassword && password.isNotEmpty()
    var showErrors by remember { mutableStateOf(false) }

    // Asynchronous backend warning context holder
    var registrationError by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Register Account", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEA580C), modifier = Modifier.padding(vertical = 32.dp))

        OutlinedTextField(value = name, onValueChange = { name = it; registrationError = null }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            value = email,
            onValueChange = { email = it.trim(); registrationError = null },
            label = { Text("Gmail (@gmail.com)") },
            modifier = Modifier.fillMaxWidth(),
            isError = (showErrors && !isEmailValid) || registrationError?.contains("Gmail") == true,
            supportingText = { if(showErrors && !isEmailValid) Text("Must be a valid @gmail.com address") }
        )
        OutlinedTextField(
            value = matric,
            onValueChange = { matric = it.trim(); registrationError = null },
            label = { Text("Matric Number") },
            isError = registrationError?.contains("Matric") == true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; registrationError = null },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it; registrationError = null },
            label = { Text("Re-confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            isError = showErrors && !passwordsMatch,
            supportingText = { if(showErrors && !passwordsMatch) Text("Passwords do not match") }
        )

        if (registrationError != null) {
            Text(
                text = registrationError!!,
                color = Color.Red,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Button(
            onClick = {
                if (isEmailValid && passwordsMatch && name.isNotBlank() && matric.isNotBlank()) {
                    val newUserPayload = User(
                        email = email,
                        name = name,
                        matric = matric,
                        username = email,
                        password = password
                    )

                    onRegisterAttempt(newUserPayload) { errorResult ->
                        if (errorResult != null) {
                            registrationError = errorResult
                        } else {
                            registrationError = null
                        }
                    }
                } else {
                    showErrors = true
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFFEA580C))
        ) { Text("Register") }

        TextButton(onClick = onBackToLogin) {
            Text("Already have an account? Login", color = Color.Gray)
        }
    }
}