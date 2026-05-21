package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.NavigationScreen
import com.example.model.UserRole
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.LogisticsViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val viewModel: LogisticsViewModel = viewModel()
        val currentScreen by viewModel.currentScreen.collectAsState()
        val currentRole by viewModel.currentRole.collectAsState()
        val isAuthenticated by viewModel.isAuthenticated.collectAsState()

        if (isAuthenticated && currentRole == UserRole.DRIVER) {
            DriverDashboardLayout(viewModel = viewModel)
        } else if (isAuthenticated && (currentRole == UserRole.FLEET_OWNER || currentRole == UserRole.DISPATCH_ADMIN)) {
            AdminDashboardLayout(viewModel = viewModel)
        } else {
            when (currentScreen) {
                NavigationScreen.SPLASH -> SplashScreenLayout(viewModel = viewModel)
                NavigationScreen.ONBOARDING -> OnboardingScreenLayout(viewModel = viewModel)
                NavigationScreen.ROLE_SELECTION -> RoleSelectionScreenLayout(viewModel = viewModel)
                NavigationScreen.AUTH -> AuthScreenLayout(viewModel = viewModel)
                NavigationScreen.DASHBOARD_CUSTOMER -> CustomerDashboardLayout(viewModel = viewModel)
                NavigationScreen.SELECT_TRUCK -> SelectTruckScreenLayout(viewModel = viewModel)
                NavigationScreen.TRUCK_DETAILS -> TruckDetailsScreenLayout(viewModel = viewModel)
                NavigationScreen.PAYMENT_CHECKOUT -> PaymentCheckoutScreenLayout(viewModel = viewModel)
                NavigationScreen.BOOKING_SUCCESS -> BookingSuccessScreenLayout(viewModel = viewModel)
                NavigationScreen.LIVE_TRACKING -> LiveTrackingScreenLayout(viewModel = viewModel)
                NavigationScreen.WALLET -> WalletScreenLayout(viewModel = viewModel)
                NavigationScreen.CHAT_SUPPORT -> ChatSupportScreenLayout(viewModel = viewModel)
                NavigationScreen.HISTORY -> HistoryScreenLayout(viewModel = viewModel)
            }
        }
      }
    }
  }
}
