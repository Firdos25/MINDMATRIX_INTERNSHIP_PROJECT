package com.example.raithavarta.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.raithavarta.di.AppContainer
import com.example.raithavarta.ui.screens.DiseaseInputScreen
import com.example.raithavarta.ui.screens.LoginScreen
import com.example.raithavarta.ui.screens.ProfileScreen
import com.example.raithavarta.ui.screens.RegisterScreen
import com.example.raithavarta.ui.screens.ResultScreen
import com.example.raithavarta.ui.theme.RaithavartaTheme
import com.example.raithavarta.ui.viewmodel.DiseaseViewModel
import com.example.raithavarta.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.first

@Composable
fun RaithavartaApp(container: AppContainer) {
    RaithavartaTheme {
        RaithavartaNavHost(container = container)
    }
}

@Composable
private fun RaithavartaNavHost(container: AppContainer) {
    val navController = rememberNavController()
    val diseaseVm: DiseaseViewModel = viewModel(factory = DiseaseViewModel.factory(container))

    NavHost(navController = navController, startDestination = "loading") {
        composable("loading") {
            LoadingRoute(navController = navController, container = container)
        }
        composable("login") {
            LoginScreen(
                navController = navController,
                container = container,
                onLoggedIn = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("register") {
            RegisterScreen(navController = navController, container = container)
        }
        composable("main") {
            MainShell(
                rootNav = navController,
                container = container,
                diseaseVm = diseaseVm
            )
        }
    }
}

@Composable
private fun LoadingRoute(navController: NavHostController, container: AppContainer) {
    LaunchedEffect(Unit) {
        val loggedIn = container.userPreferences.isLoggedIn.first()
        val remember = container.userPreferences.rememberMe.first()
        // If user chose not to be remembered, require login again on next cold start.
        if (loggedIn && !remember) {
            val email = container.userPreferences.userEmail.first()
            if (!email.isNullOrBlank()) {
                container.authRepository.logout(email)
            } else {
                container.userPreferences.clearSession()
            }
            navController.navigate("login") {
                popUpTo("loading") { inclusive = true }
            }
            return@LaunchedEffect
        }
        if (loggedIn) {
            navController.navigate("main") {
                popUpTo("loading") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("loading") { inclusive = true }
            }
        }
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun MainShell(
    rootNav: NavHostController,
    container: AppContainer,
    diseaseVm: DiseaseViewModel
) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var showResult by rememberSaveable { mutableStateOf(false) }
    val diseaseState by diseaseVm.uiState.collectAsState()

    val profileVm: ProfileViewModel = viewModel(factory = ProfileViewModel.factory(container))

    LaunchedEffect(tab, showResult) {
        if (tab == 1 && !showResult) {
            profileVm.refresh()
        }
    }

    Scaffold(
        bottomBar = {
            if (!showResult) {
                NavigationBar {
                    NavigationBarItem(
                        selected = tab == 0,
                        onClick = { tab = 0 },
                        icon = { Icon(Icons.Filled.LocalFlorist, contentDescription = null) },
                        label = { Text("Analyze") }
                    )
                    NavigationBarItem(
                        selected = tab == 1,
                        onClick = { tab = 1 },
                        icon = { Icon(Icons.Filled.Person, contentDescription = null) },
                        label = { Text("Profile") }
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when {
                showResult && diseaseState.result != null -> {
                    ResultScreen(
                        insight = diseaseState.result!!,
                        onBack = {
                            showResult = false
                            diseaseVm.clearResult()
                        }
                    )
                }
                showResult && diseaseState.result == null -> {
                    LaunchedEffect(Unit) {
                        showResult = false
                    }
                }
                tab == 0 -> {
                    DiseaseInputScreen(viewModel = diseaseVm) {
                        showResult = true
                    }
                }
                else -> {
                    ProfileScreen(viewModel = profileVm) {
                        rootNav.navigate("login") {
                            popUpTo("main") { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}
