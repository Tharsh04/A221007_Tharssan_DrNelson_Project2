package com.example.a221007_tharssan_drnelson_project2

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a221007_tharssan_drnelson_project2.data.FoodDonation
import com.example.a221007_tharssan_drnelson_project2.ui.theme.BottomNav

@Composable
fun AppNavigation(donorViewModel: DonorViewModel = viewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var pendingDonation by remember { mutableStateOf<FoodDonation?>(null) }
    var pendingCostString by remember { mutableStateOf("RM 50.00") }

    Scaffold(
        bottomBar = {
            if (donorViewModel.currentUser != null && currentRoute != "payment") {
                BottomNav(
                    currentView = currentRoute ?: "home",
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(padding)
        ) {
            composable("login") {
                var loginErrorState by remember { mutableStateOf(false) }
                LoginScreen(
                    onLoginClick = { email, pass ->
                        donorViewModel.login(email, pass) { success ->
                            if (success) {
                                loginErrorState = false
                                navController.navigate("home") { popUpTo("login") { inclusive = true } }
                            } else {
                                loginErrorState = true
                            }
                        }
                        !loginErrorState
                    },
                    onNavigateToRegister = { navController.navigate("register") }
                )
            }

            composable("register") {
                RegisterScreen(
                    onRegisterAttempt = { user, onValidationCallback ->
                        donorViewModel.register(user) { errorFeedbackString ->
                            onValidationCallback(errorFeedbackString)
                            if (errorFeedbackString == null) {
                                navController.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                            }
                        }
                    },
                    onBackToLogin = { navController.popBackStack() }
                )
            }

            composable("home") {
                HomeScreen(viewModel = donorViewModel, onNavigate = { route -> navController.navigate(route) })
            }

            composable("history") {
                HistoryScreen(viewModel = donorViewModel)
            }

            composable("locator") {
                FoodBankLocatorScreen(
                    viewModel = donorViewModel,
                    onNavigateToPayment = { cost, donation ->
                        pendingCostString = cost
                        pendingDonation = donation
                        navController.navigate("payment")
                    }
                )
            }

            composable("donate-food") {
                DonateFoodScreen(
                    donorName = donorViewModel.currentUser?.name ?: "",
                    donorEmail = donorViewModel.currentUser?.email ?: "",
                    onDonate = { donation ->
                        pendingCostString = "RM ${donation.totalItemCount.times(10)}.00"
                        pendingDonation = donation
                        navController.navigate("payment")
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable("payment") {
                PaymentScreen(
                    totalAmount = pendingCostString,
                    donationData = pendingDonation,
                    onComplete = {
                        // FIXED: Submits transaction values cleanly before popping the screen to prevent detachment glitches
                        pendingDonation?.let { donorViewModel.addDonation(it) }
                        navController.popBackStack(route = "home", inclusive = false)
                    },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable("profile") {
                ProfileSettingsScreen(
                    donorName = donorViewModel.currentUser?.name ?: "",
                    donorEmail = donorViewModel.currentUser?.email ?: "",
                    donorMatric = donorViewModel.currentUser?.matric ?: "",
                    onSaveProfile = { name, email, matric -> donorViewModel.updateProfile(name, email, matric) },
                    onLogout = {
                        donorViewModel.logout()
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}