package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.model.NavigationScreen
import com.example.model.UserRole
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.LogisticsViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // 1. Core Recovery & Exception Safeguard
    val sharedPrefs = getSharedPreferences("mivo_crash_prefs", android.content.Context.MODE_PRIVATE)
    
    val oldHandler = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        try {
            // Ensure stack trace is printed to Logcat so developers and automated agents can diagnose the root cause
            throwable.printStackTrace()
            
            val exceptionStr = android.util.Log.getStackTraceString(throwable)
            val isOurCrash = exceptionStr.contains("com.example") || exceptionStr.contains("com.aistudio")
            
            if (isOurCrash) {
                sharedPrefs.edit().putString("last_crash_log", exceptionStr).commit()
                
                // Relaunch Application securely to prevent unresponsive InputDispatcher blackouts
                val intent = packageManager.getLaunchIntentForPackage(packageName)
                intent?.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                
                android.os.Process.killProcess(android.os.Process.myPid())
                java.lang.System.exit(10)
            } else {
                // If the exception happened outside our package, report/delegate to standard handler
                oldHandler?.uncaughtException(thread, throwable)
            }
        } catch (e: Exception) {
            oldHandler?.uncaughtException(thread, throwable)
        }
    }

    setContent {
      MyApplicationTheme {
        var crashLog by remember { mutableStateOf(sharedPrefs.getString("last_crash_log", null)) }

        if (crashLog != null) {
            // Display beautiful, highly interactive and robust diagnostic recovery terminal page instead of hard crashing
            Scaffold(
                containerColor = Color(0xFF0F172A) // Modern dark cosmic backdrop
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .background(Color(0xFFEF4444).copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "System Diagnostics Error",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Core Recovery Terminal",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.W900
                    )

                    Text(
                        text = "Diagnostics active. The system intercepted a crash.",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Crash console log output
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                            .background(Color(0xFF020617), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = crashLog ?: "Unknown null telemetry log",
                            color = Color(0xFF38BDF8), // Light terminal blue code colors
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Diagnostic Actions
                    Button(
                        onClick = {
                            sharedPrefs.edit().remove("last_crash_log").commit()
                            crashLog = null
                            
                            // Safe restart trigger
                            val intent = packageManager.getLaunchIntentForPackage(packageName)
                            intent?.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316)), // Recovery Orange
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Icon(Icons.Default.Refresh, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Clear Error & Hot Reload", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            sharedPrefs.edit().clear().commit()
                            crashLog = null
                            
                            // Attempt factory reset mapping on database to clear corrupted migrations
                            try {
                                deleteDatabase("mivo_logistics_database")
                            } catch (e: Exception) {
                                // Handled
                            }
                            
                            val intent = packageManager.getLaunchIntentForPackage(packageName)
                            intent?.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                        },
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Icon(Icons.Default.Delete, null, tint = Color(0xFFEF4444))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Factory Reset Database & Logs", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Standard execution path
            // Initializing the ViewModel through a standard Android VM DSL Factory connected to our database
            val factory = viewModelFactory {
                initializer {
                    val app = this@MainActivity.application
                    val repository = if (app is LogisticsApplication) {
                        app.repository
                    } else {
                        // Fallback configuration if the running context is wrapped or not a subclass of LogisticsApplication
                        val database = com.example.db.AppDatabase.getDatabase(app)
                        com.example.db.LogisticsRepository(database)
                    }
                    LogisticsViewModel(repository)
                }
            }
            val viewModel: LogisticsViewModel = viewModel(factory = factory)
            val currentScreen by viewModel.currentScreen.collectAsState()
            val currentRole by viewModel.currentRole.collectAsState()
            val isAuthenticated by viewModel.isAuthenticated.collectAsState()

            if (isAuthenticated && currentRole == UserRole.DRIVER) {
                DriverDashboardLayout(viewModel = viewModel)
            } else if (isAuthenticated && (currentRole == UserRole.FLEET_OWNER || currentRole == UserRole.DISPATCH_ADMIN)) {
                AdminDashboardLayout(viewModel = viewModel)
            } else if (isAuthenticated && currentRole == UserRole.CUSTOMER) {
                CustomerMainLayout(viewModel = viewModel)
            } else {
                when (currentScreen) {
                    NavigationScreen.SPLASH -> SplashScreenLayout(viewModel = viewModel)
                    NavigationScreen.ONBOARDING -> OnboardingScreenLayout(viewModel = viewModel)
                    NavigationScreen.ROLE_SELECTION -> RoleSelectionScreenLayout(viewModel = viewModel)
                    NavigationScreen.AUTH -> AuthScreenLayout(viewModel = viewModel)
                    else -> OnboardingScreenLayout(viewModel = viewModel)
                }
            }
        }
      }
    }
  }
}

// Stateful persistent scaffold layout for Customers - avoids screen reset and navigation trapping
@Composable
fun CustomerMainLayout(viewModel: LogisticsViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    val selectedTab = when (currentScreen) {
        NavigationScreen.DASHBOARD_CUSTOMER -> 0
        NavigationScreen.WALLET -> 1
        NavigationScreen.CHAT_SUPPORT -> 2
        NavigationScreen.HISTORY -> 3
        else -> 0
    }

    val showBottomBar = currentScreen in listOf(
        NavigationScreen.DASHBOARD_CUSTOMER,
        NavigationScreen.WALLET,
        NavigationScreen.CHAT_SUPPORT,
        NavigationScreen.HISTORY
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = (selectedTab == 0),
                        onClick = { viewModel.navigateTo(NavigationScreen.DASHBOARD_CUSTOMER) },
                        icon = { Icon(Icons.Default.LocalShipping, null) },
                        label = { Text("Book Truck") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFF6F00), // PrimaryOrange
                            indicatorColor = Color(0xFFFFE0B2) // Light primary container accent
                        )
                    )
                    NavigationBarItem(
                        selected = (selectedTab == 1),
                        onClick = { viewModel.navigateTo(NavigationScreen.WALLET) },
                        icon = { Icon(Icons.Default.AccountBalanceWallet, null) },
                        label = { Text("Wallet") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFF6F00),
                            indicatorColor = Color(0xFFFFE0B2)
                        )
                    )
                    NavigationBarItem(
                        selected = (selectedTab == 2),
                        onClick = { viewModel.navigateTo(NavigationScreen.CHAT_SUPPORT) },
                        icon = { Icon(Icons.Default.Chat, null) },
                        label = { Text("AI Assist") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFF6F00),
                            indicatorColor = Color(0xFFFFE0B2)
                        )
                    )
                    NavigationBarItem(
                        selected = (selectedTab == 3),
                        onClick = { viewModel.navigateTo(NavigationScreen.HISTORY) },
                        icon = { Icon(Icons.Default.History, null) },
                        label = { Text("History") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFF6F00),
                            indicatorColor = Color(0xFFFFE0B2)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
            when (currentScreen) {
                NavigationScreen.DASHBOARD_CUSTOMER -> CustomerDashboardLayout(viewModel = viewModel)
                NavigationScreen.WALLET -> WalletScreenLayout(viewModel = viewModel)
                NavigationScreen.CHAT_SUPPORT -> ChatSupportScreenLayout(viewModel = viewModel)
                NavigationScreen.HISTORY -> HistoryScreenLayout(viewModel = viewModel)
                NavigationScreen.SELECT_TRUCK -> SelectTruckScreenLayout(viewModel = viewModel)
                NavigationScreen.TRUCK_DETAILS -> TruckDetailsScreenLayout(viewModel = viewModel)
                NavigationScreen.PAYMENT_CHECKOUT -> PaymentCheckoutScreenLayout(viewModel = viewModel)
                NavigationScreen.BOOKING_SUCCESS -> BookingSuccessScreenLayout(viewModel = viewModel)
                NavigationScreen.LIVE_TRACKING -> LiveTrackingScreenLayout(viewModel = viewModel)
                else -> CustomerDashboardLayout(viewModel = viewModel)
            }
        }
    }
}
