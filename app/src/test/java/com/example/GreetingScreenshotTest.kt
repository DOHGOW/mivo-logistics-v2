package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.SplashScreenLayout
import com.example.viewmodel.LogisticsViewModel
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
    val database = com.example.db.AppDatabase.getDatabase(context)
    val viewModel = LogisticsViewModel(com.example.db.LogisticsRepository(database))
    composeTestRule.setContent { MyApplicationTheme { SplashScreenLayout(viewModel) } }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }

  @Test
  fun test_comprehensive_transitions_and_screens() {
    val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
    val database = com.example.db.AppDatabase.getDatabase(context)
    val viewModel = LogisticsViewModel(com.example.db.LogisticsRepository(database))
    
    composeTestRule.setContent {
      MyApplicationTheme {
        val currentScreen by viewModel.currentScreen.collectAsState()
        val currentRole by viewModel.currentRole.collectAsState()
        val isAuthenticated by viewModel.isAuthenticated.collectAsState()

        if (isAuthenticated && currentRole == com.example.model.UserRole.DRIVER) {
            com.example.ui.DriverDashboardLayout(viewModel = viewModel)
        } else if (isAuthenticated && (currentRole == com.example.model.UserRole.FLEET_OWNER || currentRole == com.example.model.UserRole.DISPATCH_ADMIN)) {
            com.example.ui.AdminDashboardLayout(viewModel = viewModel)
        } else if (isAuthenticated && currentRole == com.example.model.UserRole.CUSTOMER) {
            com.example.CustomerMainLayout(viewModel = viewModel)
        } else {
            when (currentScreen) {
                com.example.model.NavigationScreen.SPLASH -> com.example.ui.SplashScreenLayout(viewModel = viewModel)
                com.example.model.NavigationScreen.ONBOARDING -> com.example.ui.OnboardingScreenLayout(viewModel = viewModel)
                com.example.model.NavigationScreen.ROLE_SELECTION -> com.example.ui.RoleSelectionScreenLayout(viewModel = viewModel)
                com.example.model.NavigationScreen.AUTH -> com.example.ui.AuthScreenLayout(viewModel = viewModel)
                else -> com.example.ui.OnboardingScreenLayout(viewModel = viewModel)
            }
        }
      }
    }
    
    // 1. SPLASH SCREEN (Default starting state)
    composeTestRule.waitForIdle()

    // 2. Transition to ONBOARDING
    composeTestRule.runOnUiThread {
        viewModel.navigateTo(com.example.model.NavigationScreen.ONBOARDING)
    }
    composeTestRule.waitForIdle()

    // 3. Transition to ROLE_SELECTION
    composeTestRule.runOnUiThread {
        viewModel.navigateTo(com.example.model.NavigationScreen.ROLE_SELECTION)
    }
    composeTestRule.waitForIdle()

    // 4. Transition to AUTH
    composeTestRule.runOnUiThread {
        viewModel.navigateTo(com.example.model.NavigationScreen.AUTH)
    }
    composeTestRule.waitForIdle()
    
    // 5. Simulate standard driver login block transition
    composeTestRule.runOnUiThread {
        viewModel.setRole(com.example.model.UserRole.DRIVER)
        viewModel.authenticate("vance.highway@mivo.logistics", "12345")
    }
    composeTestRule.waitForIdle()

    // 6. LOGOUT
    composeTestRule.runOnUiThread {
        viewModel.logout()
    }
    composeTestRule.waitForIdle()

    // 7. CUSTOMER SCENARIO & SCREEN FLOWS
    composeTestRule.runOnUiThread {
        viewModel.setRole(com.example.model.UserRole.CUSTOMER)
        viewModel.authenticate("shipper.terminal@mivo.logistics", "12345")
    }
    composeTestRule.waitForIdle()

    val customerScreens = listOf(
        com.example.model.NavigationScreen.DASHBOARD_CUSTOMER,
        com.example.model.NavigationScreen.WALLET,
        com.example.model.NavigationScreen.CHAT_SUPPORT,
        com.example.model.NavigationScreen.HISTORY,
        com.example.model.NavigationScreen.SELECT_TRUCK,
        com.example.model.NavigationScreen.TRUCK_DETAILS,
        com.example.model.NavigationScreen.PAYMENT_CHECKOUT,
        com.example.model.NavigationScreen.BOOKING_SUCCESS,
        com.example.model.NavigationScreen.LIVE_TRACKING
    )

    for (screen in customerScreens) {
        composeTestRule.runOnUiThread {
            viewModel.navigateTo(screen)
        }
        composeTestRule.waitForIdle()
    }

    // 8. LOGOUT CUSTOMER
    composeTestRule.runOnUiThread {
        viewModel.logout()
    }
    composeTestRule.waitForIdle()

    // 9. FLEET OWNER / ADMIN SCENARIO
    composeTestRule.runOnUiThread {
        viewModel.setRole(com.example.model.UserRole.FLEET_OWNER)
        viewModel.authenticate("fleet.nexus@mivo.logistics", "12345")
    }
    composeTestRule.waitForIdle()
  }
}
