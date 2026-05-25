@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.viewmodel.LogisticsViewModel
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import java.util.Locale
import java.util.UUID
import coil.compose.AsyncImage

@Composable
fun SplashScreenLayout(viewModel: LogisticsViewModel) {
    val logoScale = remember { androidx.compose.animation.core.Animatable(0.4f) }
    LaunchedEffect(Unit) {
        logoScale.animateTo(
            targetValue = 1.0f,
            animationSpec = androidx.compose.animation.core.spring(
                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                stiffness = androidx.compose.animation.core.Spring.StiffnessLow
            )
        )
        kotlinx.coroutines.delay(1800)
        viewModel.navigateTo(NavigationScreen.ONBOARDING)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFEA580C), Color(0xFF9A3412))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer {
                scaleX = logoScale.value
                scaleY = logoScale.value
            }
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(Color.White, RoundedCornerShape(32.dp))
                    .shadow(12.dp, RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalShipping,
                    contentDescription = "Mivo Logo",
                    tint = PrimaryOrange,
                    modifier = Modifier.size(72.dp)
                )
            }
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "Mivo",
                fontSize = 62.sp,
                fontWeight = FontWeight.W900,
                color = Color.White,
                letterSpacing = (-1).sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "KINETIC PRECISION LOGISTICS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.9f),
                letterSpacing = 2.5.sp
            )
        }
    }
}

@Composable
fun OnboardingScreenLayout(viewModel: LogisticsViewModel) {
    var step by remember { mutableStateOf(1) }

    val headlineText = when (step) {
        1 -> "Book a Truck Instantly"
        2 -> "Real-time Precision"
        else -> "Secure & Verified"
    }

    val bodyText = when (step) {
        1 -> "Fast and reliable logistics at your fingertips. Experience the next generation of logistics orchestration."
        2 -> "Track your cargo with millisecond precision and high-definition route mapping in our latest version."
        else -> "Our partners are verified and trained for secure freight handling. Safety first, always."
    }

    val iconVector = when (step) {
        1 -> Icons.Default.LocalShipping
        2 -> Icons.Default.AccessTime
        else -> Icons.Default.Shield
    }

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundLight)
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Step Indicator Text Left
                Column {
                    Text(
                        text = "STEP",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = LightGray,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "$step OF 3",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = DarkText
                    )
                }

                // Proceed Button Right
                Button(
                    onClick = {
                        if (step < 3) {
                            step++
                        } else {
                            viewModel.navigateTo(NavigationScreen.ROLE_SELECTION)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = if (step == 3) "GET STARTED" else "NEXT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(innerPadding)
        ) {
            // Drop visual graphic box taking ~45% of height
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            ) {
                // Curved Native Local Canvas & Vector Graphic Background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                        .background(
                            brush = when (step) {
                                1 -> Brush.verticalGradient(listOf(Color(0xFFEA580C), Color(0xFFF97316)))
                                2 -> Brush.verticalGradient(listOf(Color(0xFF3F3F46), Color(0xFF18181B)))
                                else -> Brush.verticalGradient(listOf(Color(0xFF0F766E), Color(0xFF115E59)))
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        try {
                            val width = size.width
                            val height = size.height
                            if (width > 0f && height > 0f) {
                                // Futuristic design elements inside local canvas
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.05f),
                                    radius = width * 0.4f,
                                    center = Offset(width * 0.5f, height * 0.5f)
                                )
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.03f),
                                    radius = width * 0.6f,
                                    center = Offset(width * 0.5f, height * 0.5f)
                                )
                                
                                // Decorative floating particles
                                drawCircle(color = Color.White.copy(alpha = 0.08f), radius = 10f, center = Offset(width * 0.15f, height * 0.25f))
                                drawCircle(color = Color.White.copy(alpha = 0.08f), radius = 6f, center = Offset(width * 0.85f, height * 0.35f))
                                drawCircle(color = Color.White.copy(alpha = 0.06f), radius = 14f, center = Offset(width * 0.25f, height * 0.75f))
                            }
                        } catch (e: Exception) {
                            // Suppress canvas rendering issues on early frames
                        }
                    }

                    // Native central glowing graphic representation based on step
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(28.dp))
                                .border(1.5.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(28.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(54.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = when (step) {
                                1 -> "FREIGHT FLEET DEPLOYMENT"
                                2 -> "REAL-TIME TELEMETRY TRACE"
                                else -> "SECURE & VERIFIED CARGO"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.85f),
                            letterSpacing = 1.5.sp
                        )
                    }
                }

                // Skip button floating top-right (light aesthetic)
                Box(
                    modifier = Modifier
                        .statusBarsPadding()
                        .align(Alignment.TopEnd)
                        .padding(top = 16.dp, end = 24.dp)
                        .background(Color.Black.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                        .clickable { viewModel.navigateTo(NavigationScreen.ROLE_SELECTION) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "SKIP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }

                // Float Card hovering and overlapping the bottom
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .align(Alignment.BottomCenter)
                        .offset(y = 28.dp)
                        .shadow(8.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White.copy(alpha = 0.95f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(PrimaryOrange.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = null,
                                tint = PrimaryOrange,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "MIVO LOGISTICS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepAmber,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Swift. Precise. Seamless.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Text Info & Page Indicators Column
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
            ) {
                Text(
                    text = headlineText,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = DarkText,
                    lineHeight = 38.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = bodyText,
                    fontSize = 15.sp,
                    color = GrayText,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Dots Indicators (Step indicator on video has active wider)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..3).forEach { idx ->
                        val active = (idx == step)
                        Box(
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .size(width = if (active) 24.dp else 8.dp, height = 8.dp)
                                .background(
                                    color = if (active) PrimaryOrange else LightGray.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoleSelectionScreenLayout(viewModel: LogisticsViewModel) {
    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 24.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Secure emblem",
                        tint = SuccessGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SECURE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrayText,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.width(36.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified emblem",
                        tint = PrimaryOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "VERIFIED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrayText,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Top Centered Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.White, RoundedCornerShape(18.dp))
                    .shadow(4.dp, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalShipping,
                    contentDescription = null,
                    tint = PrimaryOrange,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome to Mivo",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Choose your role to get started",
                fontSize = 14.sp,
                color = GrayText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Card 1: Customer (Book a Truck)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.setRole(UserRole.CUSTOMER)
                        viewModel.navigateTo(NavigationScreen.AUTH)
                    }
                    .shadow(2.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFFFF7ED), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = PrimaryOrange,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Book a Truck",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Ship your goods across the country with ease.",
                            fontSize = 13.sp,
                            color = GrayText
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Forward link",
                        tint = LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card 2: Driver (Join as Driver)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.setRole(UserRole.DRIVER)
                        viewModel.navigateTo(NavigationScreen.AUTH)
                    }
                    .shadow(2.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFFFF7ED), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = PrimaryOrange,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Join as Driver",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Join our fleet and earn on your schedule.",
                            fontSize = 13.sp,
                            color = GrayText
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Forward link",
                        tint = LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AuthScreenLayout(viewModel: LogisticsViewModel) {
    val role by viewModel.currentRole.collectAsState()
    var isRegisterState by remember { mutableStateOf(false) }

    var nameText by remember { mutableStateOf("") }
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }

    var statusText by remember { mutableStateOf("") }
    var statusIsError by remember { mutableStateOf(false) }

    // Preset testing emails based on role selection
    LaunchedEffect(role, isRegisterState) {
        statusText = ""
        if (!isRegisterState) {
            emailText = when (role) {
                UserRole.CUSTOMER -> "shipper.terminal@mivo.logistics"
                UserRole.DRIVER -> "vance.highway@mivo.logistics"
                UserRole.FLEET_OWNER -> "fleet.nexus@mivo.logistics"
                UserRole.DISPATCH_ADMIN -> "dispatcher.alpha@mivo.logistics"
            }
            passwordText = "12345"
        } else {
            nameText = ""
            emailText = ""
            passwordText = ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Decorative soft dynamic amber vector accent
        Canvas(modifier = Modifier.fillMaxSize()) {
            try {
                val circleRadius = 280.dp.toPx()
                val width = size.width
                if (width > 0f && !circleRadius.isNaN() && !circleRadius.isInfinite()) {
                    drawCircle(
                        color = PrimaryOrange.copy(alpha = 0.08f),
                        radius = circleRadius,
                        center = Offset(width, 0f)
                    )
                }
            } catch (e: Exception) {
                // Safeguard canvas rendering
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.White, RoundedCornerShape(18.dp))
                    .shadow(2.dp, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VpnKey,
                    contentDescription = null,
                    tint = PrimaryOrange,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Secure Credential Vault",
                fontSize = 22.sp,
                fontWeight = FontWeight.W900,
                color = DarkText
            )

            Text(
                text = "Verification for ${role.displayName}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryOrange,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sliding Selection Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!isRegisterState) PrimaryOrange else Color.Transparent)
                        .clickable { isRegisterState = false }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Sign In Gate",
                        color = if (!isRegisterState) Color.White else DarkText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isRegisterState) PrimaryOrange else Color.Transparent)
                        .clickable { isRegisterState = true }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Secure Sign Up",
                        color = if (isRegisterState) Color.White else DarkText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isRegisterState) {
                OutlinedTextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    label = { Text("Company Name / Driver Name") },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryOrange,
                        focusedLabelColor = PrimaryOrange
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = emailText,
                onValueChange = { emailText = it },
                label = { Text("Identity Email") },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryOrange,
                    focusedLabelColor = PrimaryOrange
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = passwordText,
                onValueChange = { passwordText = it },
                label = { Text("Network Passkey") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryOrange,
                    focusedLabelColor = PrimaryOrange
                )
            )

            if (!isRegisterState) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Sync Device Bio-ID",
                        fontSize = 13.sp,
                        color = PrimaryOrange,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { }
                    )
                }
            }

            if (statusText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = statusText,
                    color = if (statusIsError) ErrorRed else SuccessGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            GradientActionButton(
                text = if (isRegisterState) "Create Local Database Profile" else "Authenticate Terminal Connection",
                onClick = {
                    statusText = ""
                    if (isRegisterState) {
                        viewModel.register(nameText, emailText, passwordText, role) { success, msg ->
                            statusIsError = !success
                            statusText = msg
                        }
                    } else {
                        viewModel.authenticate(emailText, passwordText) { success, msg ->
                            statusIsError = !success
                            statusText = msg
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Standard licensing legal agreements apply. ", color = GrayText, fontSize = 11.sp)
                Text("Terms & Privacy", color = PrimaryOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable {})
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = { viewModel.navigateTo(NavigationScreen.ROLE_SELECTION) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Change Access Client Type", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CustomerDashboardLayout(viewModel: LogisticsViewModel) {
    val pickup by viewModel.pickupAddress.collectAsState()
    val destination by viewModel.destinationAddress.collectAsState()
    val currentTrucks by viewModel.availableTrucks.collectAsState()
    val selectedTruck by viewModel.selectedTruck.collectAsState()
    val walletBal by viewModel.walletBalance.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
            // Visual simulated map as background of upper half
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.38f)
            ) {
                SimulatedInteractiveMap(
                    progress = 0.0f,
                    showNearbyVehicles = true
                )

                // App top header overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, null, tint = PrimaryOrange, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mivo", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FirebaseStatusChip()

                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.9f), CircleShape)
                                .clickable { viewModel.logout() }
                                .padding(8.dp)
                        ) {
                            Icon(Icons.Default.ExitToApp, "Logout", tint = ErrorRed, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // Balance sticker
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Ledger Balance: $${String.format(Locale.US, "%,.2f", walletBal)}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Swipe up booking details panel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .fillMaxHeight(0.64f)
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(BackgroundLight)
                    .border(
                        width = 1.dp,
                        color = LightGray.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    ).padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                // Handle indicator line
                Box(
                        modifier = Modifier
                            .size(width = 48.dp, height = 4.dp)
                            .background(LightGray.copy(alpha = 0.5f), CircleShape)
                            .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Request Dispatch Rigs",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W800,
                    color = DarkText
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ROUTE SELECTOR PANEL (Pickup / Destination)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.RadioButtonChecked, "Pickup", tint = PrimaryOrange, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Pickup Depot Hub", fontSize = 11.sp, color = GrayText)
                                BasicTextField(
                                    value = pickup,
                                    onValueChange = { viewModel.pickupAddress.value = it },
                                    textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkText)
                                )
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = LightGray.copy(alpha = 0.2f))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, "Destination", tint = SuccessGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Destination Warehouse", fontSize = 11.sp, color = GrayText)
                                BasicTextField(
                                    value = destination,
                                    onValueChange = { viewModel.destinationAddress.value = it },
                                    textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkText)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                MivoSectionHeader(title = "Select Intelligence Configuration")

                // Horizontal Carousel of available trucking classes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 12.dp)
                ) {
                    currentTrucks.forEach { truck ->
                        val selected = (selectedTruck?.id == truck.id)
                        Card(
                            modifier = Modifier
                                .width(145.dp)
                                .padding(end = 12.dp)
                                .border(
                                    width = 2.dp,
                                    color = if (selected) PrimaryOrange else Color.Transparent,
                                    shape = RoundedCornerShape(16.dp)
                                ).clickable { viewModel.selectTruck(truck) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selected) Color.White else Color.White.copy(alpha = 0.6f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(
                                            if (selected) PrimaryOrange.copy(alpha = 0.15f) else LightGray.copy(alpha = 0.2f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = null,
                                        tint = if (selected) PrimaryOrange else DarkText,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = truck.type.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Cap: ${truck.capacity}",
                                    fontSize = 11.sp,
                                    color = GrayText
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "$${String.format(Locale.US, "%,.2f", truck.price)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W800,
                                    color = PrimaryOrange
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Detail Specs toggle expander
                GradientActionButton(
                    text = "Configure Booking & Cargo Details",
                    onClick = { viewModel.navigateTo(NavigationScreen.SELECT_TRUCK) }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
}

@Composable
fun SelectTruckScreenLayout(viewModel: LogisticsViewModel) {
    val selectedTruck by viewModel.selectedTruck.collectAsState()
    val weight by viewModel.cargoWeightKg.collectAsState()
    val dimensions by viewModel.cargoDimensions.collectAsState()
    val priority by viewModel.priorityToggle.collectAsState()
    val fragile by viewModel.fragileToggle.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cargo Specifications", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(NavigationScreen.DASHBOARD_CUSTOMER) }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundLight)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Enter Cargo Parameters",
                fontSize = 16.sp,
                fontWeight = FontWeight.W800,
                color = DarkText
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Spec parameters
            GlassCard {
                Text("Total Weight of Containers (KG)", color = GrayText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = weight.toString(),
                    onValueChange = { viewModel.cargoWeightKg.value = it.toIntOrNull() ?: 0 },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = { Text("kg", fontWeight = FontWeight.Bold, color = PrimaryOrange) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Cargo Dimensions (L x W x H meters)", color = GrayText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = dimensions,
                    onValueChange = { viewModel.cargoDimensions.value = it },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = { Icon(Icons.Default.AspectRatio, null, tint = PrimaryOrange) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Transit Overrides", fontSize = 16.sp, fontWeight = FontWeight.W800, color = DarkText)
            Spacer(modifier = Modifier.height(12.dp))

            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = PrimaryOrange)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Fragile Shipment Status", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Requires specialized shock buffers", fontSize = 11.sp, color = GrayText)
                        }
                    }
                    Switch(
                        checked = fragile,
                        onCheckedChange = { viewModel.fragileToggle.value = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = PrimaryOrange, checkedTrackColor = PrimaryOrange.copy(alpha = 0.4f))
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp), color = LightGray.copy(alpha = 0.2f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.OfflineBolt, null, tint = PrimaryOrange)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Express Priority Route", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Bypass holding cells (25% faster)", fontSize = 11.sp, color = GrayText)
                        }
                    }
                    Switch(
                        checked = priority,
                        onCheckedChange = { viewModel.priorityToggle.value = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = PrimaryOrange, checkedTrackColor = PrimaryOrange.copy(alpha = 0.4f))
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            GradientActionButton(
                text = "Show Detailed Rig Overview",
                onClick = { viewModel.navigateTo(NavigationScreen.TRUCK_DETAILS) }
            )
        }
    }
}

@Composable
fun TruckDetailsScreenLayout(viewModel: LogisticsViewModel) {
    val selectedTruck by viewModel.selectedTruck.collectAsState()
    val pickup by viewModel.pickupAddress.collectAsState()
    val destination by viewModel.destinationAddress.collectAsState()

    val truck = selectedTruck ?: return

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dispatcher Vehicle Match", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(NavigationScreen.SELECT_TRUCK) }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundLight)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Simulated truck details banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(LightGray.copy(alpha = 0.4f), Color.White)
                        )
                    ).border(1.dp, LightGray.copy(alpha = 0.25f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = PrimaryOrange,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = truck.plateNumber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryOrange,
                        modifier = Modifier
                            .background(PrimaryOrange.copy(alpha = 0.15f), CircleShape)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = truck.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.W900,
                color = DarkText
            )

            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Star, "Rating", tint = PrimaryOrange, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${truck.rating} (Super-vetted operator)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Specifications & Compliance", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkText)
            Spacer(modifier = Modifier.height(8.dp))

            GlassCard {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Carrier Class", color = GrayText, fontSize = 13.sp)
                    Text(truck.type.displayName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Divider(modifier = Modifier.padding(vertical = 10.dp), color = LightGray.copy(alpha = 0.15f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Vetted Crew Chief", color = GrayText, fontSize = 13.sp)
                    Text(truck.driverName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Divider(modifier = Modifier.padding(vertical = 10.dp), color = LightGray.copy(alpha = 0.15f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Insurance Surcharge Status", color = GrayText, fontSize = 13.sp)
                    Text(truck.insuranceStatus, color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Predicted Transit Frame", fontSize = 11.sp, color = GrayText)
                    Text("Ready in ${truck.etaMinutes} mins", fontSize = 17.sp, fontWeight = FontWeight.W800, color = PrimaryOrange)
                }

                Box(
                    modifier = Modifier
                        .background(SuccessGreen.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Dynamic Surcharge Grid Lock", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            GradientActionButton(
                text = "Generate Freight Invoice",
                onClick = { viewModel.navigateTo(NavigationScreen.PAYMENT_CHECKOUT) }
            )
        }
    }
}

@Composable
fun PaymentCheckoutScreenLayout(viewModel: LogisticsViewModel) {
    val selectedTruck by viewModel.selectedTruck.collectAsState()
    val weight by viewModel.cargoWeightKg.collectAsState()
    val priority by viewModel.priorityToggle.collectAsState()
    val walletBal by viewModel.walletBalance.collectAsState()

    val truck = selectedTruck ?: return

    // Invoice breakdown
    val basePrice = truck.price
    val weightFee = weight * 0.12
    val priorityValue = if (priority) (basePrice + weightFee) * 0.25 else 0.0
    val totalInvoicePrice = basePrice + weightFee + priorityValue

    var selectedMethod by remember { mutableStateOf("Wallet Balance") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Freight Ledger Settlement", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(NavigationScreen.TRUCK_DETAILS) }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundLight)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Freight Bill Ledger Summary", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkText)
            Spacer(modifier = Modifier.height(10.dp))

            GlassCard {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Basic Rig Haul Surcharge", color = GrayText)
                    Text("$${String.format(Locale.US, "%,.2f", basePrice)}", fontWeight = FontWeight.Bold)
                }
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = LightGray.copy(alpha = 0.15f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tonnage Surcharge (${weight} kg)", color = GrayText)
                    Text("$${String.format(Locale.US, "%,.2f", weightFee)}", fontWeight = FontWeight.Bold)
                }
                if (priority) {
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = LightGray.copy(alpha = 0.15f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Dynamic Express Route Lock", color = GrayText)
                        Text("$${String.format(Locale.US, "%,.2f", priorityValue)}", color = PrimaryOrange, fontWeight = FontWeight.Bold)
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = PrimaryOrange.copy(alpha = 0.25f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Freight Settlement", fontWeight = FontWeight.Bold, color = DarkText)
                    Text("$${String.format(Locale.US, "%,.2f", totalInvoicePrice)}", color = PrimaryOrange, fontSize = 20.sp, fontWeight = FontWeight.W900)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Select Liquidity Channel", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkText)
            Spacer(modifier = Modifier.height(10.dp))

            // Wallet payment option details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = if (selectedMethod == "Wallet Balance") PrimaryOrange else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    ).clickable { selectedMethod = "Wallet Balance" },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = PrimaryOrange,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Mivo Yield Cashback Wallet", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Available balance: $${String.format(Locale.US, "%,.2f", walletBal)}", fontSize = 12.sp, color = GrayText)
                    }
                    if (walletBal < totalInvoicePrice) {
                        Text("Insufficient", color = ErrorRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card option (Stripe/Paystack mock representation)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = if (selectedMethod == "Visa") PrimaryOrange else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    ).clickable { selectedMethod = "Visa" },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = DeepAmber,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Visa Gateway Vault •••• 1244", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Verified secure connection active", fontSize = 11.sp, color = SuccessGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            GradientActionButton(
                text = "Execute Surcharge Settlement",
                enabled = (selectedMethod == "Visa" || walletBal >= totalInvoicePrice),
                onClick = { viewModel.confirmBooking(selectedMethod) }
            )
        }
    }
}

@Composable
fun BookingSuccessScreenLayout(viewModel: LogisticsViewModel) {
    val activeBooking by viewModel.currentBooking.collectAsState()

    LaunchedEffect(Unit) {
        // Automatically switch screen after 4 seconds to Tracking
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(SuccessGreen.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Cargo Logistics Engaged",
                fontSize = 24.sp,
                fontWeight = FontWeight.W900,
                color = DarkText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Transaction receipt logged successfully. Dynamic route optimization curves computed by AI node GP-9.",
                fontSize = 14.sp,
                color = GrayText,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Transaction Detail Card
            if (activeBooking != null) {
                GlassCard {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Shipment Ref", fontSize = 12.sp, color = GrayText)
                        Text(activeBooking!!.id, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryOrange)
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = LightGray.copy(alpha = 0.15f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Selected Truck", fontSize = 12.sp, color = GrayText)
                        Text(activeBooking!!.selectedTruck.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            GradientActionButton(
                text = "Access Active Tracking HUD",
                onClick = { viewModel.navigateTo(NavigationScreen.LIVE_TRACKING) }
            )
        }
    }
}

@Composable
fun LiveTrackingScreenLayout(viewModel: LogisticsViewModel) {
    val activeBooking by viewModel.currentBooking.collectAsState()
    val progress by viewModel.routeSimulatedProgress.collectAsState()
    val simulatedSpeed by viewModel.simulatedSpeedKmph.collectAsState()
    val simulatedEta by viewModel.simulatedEtaMinutes.collectAsState()

    val booking = activeBooking ?: return

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Active Freight Route Stream", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(booking.id, fontSize = 11.sp, color = PrimaryOrange, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(NavigationScreen.DASHBOARD_CUSTOMER) }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White.copy(alpha = 0.9f))
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Big procedural Map Canvas in background
            SimulatedInteractiveMap(
                progress = progress,
                activeBooking = booking,
                showNearbyVehicles = false
            )

            // Dynamic float telemetry badges
            Row(
                modifier = Modifier
                    .fillModifierWithInsets(Modifier.fillMaxWidth().padding(16.dp)),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.90f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Column {
                        Text("Current Velocity", fontSize = 10.sp, color = GrayText)
                        Text("${simulatedSpeed.toInt()} km/h", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryOrange)
                    }
                }

                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.90f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Estimated Duration", fontSize = 10.sp, color = GrayText)
                        Text(
                            text = if (simulatedEta > 0) "$simulatedEta mins" else "Delivered",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }
                }
            }

            // Bottom milestones sheet
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.52f)
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(Color.White)
                    .border(
                        width = 1.dp,
                        color = LightGray.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    ),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(PrimaryOrange.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, null, tint = PrimaryOrange, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(booking.selectedTruck.driverName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Assigned Cargo Chief", fontSize = 11.sp, color = GrayText)
                            }
                        }

                        Row {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(SuccessGreen.copy(alpha = 0.15f), CircleShape)
                                    .clickable { viewModel.navigateTo(NavigationScreen.CHAT_SUPPORT) }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Message, "Chat Driver", tint = SuccessGreen, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 14.dp), color = LightGray.copy(alpha = 0.15f))

                    Text("Freight Milestones", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkText)
                    Spacer(modifier = Modifier.height(12.dp))

                    MivoMilestoneTimeline(milestones = booking.milestones)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// Custom padding helper to avoid overlay clashes
fun Modifier.fillModifierWithInsets(modifier: Modifier): Modifier = this.then(modifier)

@Composable
fun WalletScreenLayout(viewModel: LogisticsViewModel) {
    val walletBal by viewModel.walletBalance.collectAsState()
    val transactions by viewModel.walletTransactions.collectAsState()
    var depositDialogVisible by remember { mutableStateOf(false) }
    var inputDepositAmount by remember { mutableStateOf("1500") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Freight Financial Ledger", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundLight)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            // Wallet Card visual
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(DarkText, Color(0xFF1E293B))
                        )
                    ).padding(24.dp)
            ) {
                Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("MIVO YIELD LEDGER", color = PrimaryOrange, fontWeight = FontWeight.W800, fontSize = 12.sp)
                        Icon(Icons.Default.CreditCard, null, tint = Color.White)
                    }

                    Text(
                        text = "$${String.format(Locale.US, "%,.2f", walletBal)}",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.W900
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("ACTIVE SECURITY LOCK", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("Card Vault Enabled", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { depositDialogVisible = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AddCircle, null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Refill Balance")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            MivoSectionHeader(title = "Transaction Audit Trails")

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(transactions) { tx ->
                    GlassCard(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            if (tx.type == "CREDIT") SuccessGreen.copy(alpha = 0.15f) else ErrorRed.copy(alpha = 0.15f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (tx.type == "CREDIT") Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                        contentDescription = null,
                                        tint = if (tx.type == "CREDIT") SuccessGreen else ErrorRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(tx.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Ref: ${tx.id} • ${tx.date}", fontSize = 11.sp, color = GrayText)
                                }
                            }

                            Text(
                                text = if (tx.type == "CREDIT") "+$${tx.amount}" else "-$${tx.amount}",
                                color = if (tx.type == "CREDIT") SuccessGreen else ErrorRed,
                                fontWeight = FontWeight.W800,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal deposit representation
    if (depositDialogVisible) {
        AlertDialog(
            onDismissRequest = { depositDialogVisible = false },
            title = { Text("Deposit Liquidity Vault", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter surcharge injection amount (\$)", color = GrayText, fontSize = 12.sp)
                    OutlinedTextField(
                        value = inputDepositAmount,
                        onValueChange = { inputDepositAmount = it },
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = inputDepositAmount.toDoubleOrNull() ?: 0.0
                        viewModel.addWalletFunds(amt)
                        depositDialogVisible = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                ) {
                    Text("Confirm Injection")
                }
            },
            dismissButton = {
                TextButton(onClick = { depositDialogVisible = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ChatSupportScreenLayout(viewModel: LogisticsViewModel) {
    val messages by viewModel.chatMessages.collectAsState()
    var textMsgInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("AI Dispatch Core Chat", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundLight)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Chat bubble listing
            LazyColumn(
                modifier = Modifier.weight(1f),
                reverseLayout = false
            ) {
                items(messages) { msg ->
                    val isUser = (msg.senderName == "You")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Column(
                            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            Text(
                                text = "${msg.senderName} [${msg.timestamp}]",
                                fontSize = 10.sp,
                                color = GrayText,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isUser) 16.dp else 4.dp,
                                            bottomEnd = if (isUser) 4.dp else 16.dp
                                        )
                                    ).background(
                                        if (isUser) PrimaryOrange else if (msg.isAi) DarkText else Color.White
                                    ).border(
                                        width = 1.dp,
                                        color = if (isUser || msg.isAi) Color.Transparent else LightGray.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(16.dp)
                                    ).padding(12.dp)
                            ) {
                                Text(
                                    text = msg.messageText,
                                    color = if (isUser || msg.isAi) Color.White else DarkText,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Typing input panel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .border(1.dp, LightGray.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = textMsgInput,
                    onValueChange = { textMsgInput = it },
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(fontSize = 14.sp, color = DarkText),
                    decorationBox = { innerTextField ->
                        if (textMsgInput.isEmpty()) {
                            Text("Query dynamic AI dispatcher... (type 'eta')", color = LightGray, fontSize = 13.sp)
                        }
                        innerTextField()
                    }
                )

                IconButton(
                    onClick = {
                        viewModel.sendUserMessage(textMsgInput)
                        textMsgInput = ""
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.Send,
                        contentDescription = "Send",
                        tint = PrimaryOrange
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryScreenLayout(viewModel: LogisticsViewModel) {
    val history by viewModel.bookingHistory.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Freight Ledger History", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundLight)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            MivoSectionHeader(title = "Historical Delivery Logs", badgeText = "${history.size} Trips")

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(history) { b ->
                    GlassCard(modifier = Modifier.padding(vertical = 6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(b.cargoType, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Ref: ${b.id}", fontSize = 11.sp, color = GrayText)
                                Text("${b.pickupLocation.take(24)}... -> ${b.destination.take(24)}...", fontSize = 11.sp, color = GrayText)
                            }

                            Box(
                                modifier = Modifier
                                    .background(
                                        if (b.status == ShipmentStatus.DELIVERED) SuccessGreen.copy(alpha = 0.15f) else PrimaryOrange.copy(alpha = 0.15f),
                                        CircleShape
                                    ).padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = b.status.displayName,
                                    color = if (b.status == ShipmentStatus.DELIVERED) SuccessGreen else PrimaryOrange,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = LightGray.copy(alpha = 0.1f))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Surcharge Value:", fontSize = 11.sp, color = GrayText)
                            Text("$${String.format(Locale.US, "%,.2f", b.price)}", fontWeight = FontWeight.Bold, color = PrimaryOrange)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DriverDashboardLayout(viewModel: LogisticsViewModel) {
    val online by viewModel.driverOnline.collectAsState()
    val earnings by viewModel.driverTodayEarnings.collectAsState()
    val requests by viewModel.driverTripRequests.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Driver Surcharge Node", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (online) "ONLINE" else "OFFLINE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (online) SuccessGreen else ErrorRed)
                        Spacer(modifier = Modifier.width(6.dp))
                        Switch(
                            checked = online,
                            onCheckedChange = { viewModel.toggleDriverOnlineState() },
                            colors = SwitchDefaults.colors(checkedThumbColor = SuccessGreen)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            // Stats panel
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkText),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("TODAY FREIGHT EARNINGS", color = PrimaryOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(
                        text = "$${String.format(Locale.US, "%,.2f", earnings)}",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.W900,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Acceptance", color = LightGray, fontSize = 11.sp)
                            Text("98.4%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column {
                            Text("Completed", color = LightGray, fontSize = 11.sp)
                            Text("4 Loads", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column {
                            Text("Crew Rating", color = LightGray, fontSize = 11.sp)
                            Text("⭐ 4.90", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            MivoSectionHeader(title = "Pending Load Board", badgeText = "${requests.size} requests")

            if (requests.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.HourglassEmpty, null, modifier = Modifier.size(48.dp), tint = LightGray)
                        Text("No pending cargo orders in this highway sector", color = GrayText, modifier = Modifier.padding(top = 10.dp))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(requests) { req ->
                        GlassCard(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(req.cargoType, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("Weight: ${req.weightKg} kg • Price: $${req.price}", fontSize = 12.sp, color = PrimaryOrange, fontWeight = FontWeight.Bold)
                                    Text("From: ${req.pickupLocation.take(30)}...", fontSize = 12.sp, color = GrayText)
                                    Text("To: ${req.destination.take(30)}...", fontSize = 12.sp, color = GrayText)
                                }

                                Button(
                                    onClick = { viewModel.driverAcceptTrip(req) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                ) {
                                    Text("Accept")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SignatureConfirmationCanvas(onSignatureCaptured = { })
        }
    }
}

@Composable
fun AdminDashboardLayout(viewModel: LogisticsViewModel) {
    val vehicles by viewModel.fleetVehicles.collectAsState()
    val insights by viewModel.fleetInsight.collectAsState()

    Scaffold(
        topBar = {
            Surface(
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(PrimaryOrange, RoundedCornerShape(10.dp))
                                .shadow(2.dp, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Hub,
                                contentDescription = "Hub",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "OPERATIONS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText,
                                letterSpacing = 1.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(SuccessGreen, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "All Systems Live",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GrayText
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = GrayText, modifier = Modifier.size(20.dp))
                        }
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(DeepAmber, CircleShape)
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("AF", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Carousel Metrics row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Info Card 1: Active Fleet
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .background(PrimaryOrange.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                        .border(1.dp, PrimaryOrange.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "ACTIVE FLEET",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepAmber
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${insights.activeFleetTrucks}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = DarkText
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "+12%",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }
                    }
                }

                // Info Card 2: Revenue
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .background(Color(0xFFEFF6FF), RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFDBEAFE), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "REVENUE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$${String.format(Locale.US, "%,.1f", insights.grossRevenue / 1000)}k",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = DarkText
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "↑",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }
                    }
                }

                // Info Card 3: Idle Time
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "IDLE TIME",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "42m",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = DarkText
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "-5%",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Fleet Health Score Overview card with micro-progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .shadow(1.dp, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFFFEDD5), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            tint = PrimaryOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Fleet Health Score",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Text(
                            text = "98.4% Operational Efficiency",
                            fontSize = 9.sp,
                            color = GrayText
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(6.dp)
                            .background(LightGray.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.94f)
                                .fillMaxHeight()
                                .background(SuccessGreen, CircleShape)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            MivoSectionHeader(title = "Fleet Compliance Ledger")

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(vehicles) { v ->
                    GlassCard(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(v.plateNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(PrimaryOrange.copy(alpha = 0.12f), CircleShape)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(v.model, color = PrimaryOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text("Crew Head: ${v.activeDriver}", fontSize = 11.sp, color = GrayText, modifier = Modifier.padding(top = 2.dp))
                                Text("Fuel specs: ${v.fuelEfficiency} L/100km", fontSize = 11.sp, color = GrayText)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (v.status == "ONLINE") SuccessGreen.copy(alpha = 0.15f) else ErrorRed.copy(alpha = 0.15f),
                                            CircleShape
                                        ).padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = v.status,
                                        color = if (v.status == "ONLINE") SuccessGreen else ErrorRed,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "Inspect Node: ${v.healthScore}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (v.healthScore >= 80) SuccessGreen else ErrorRed,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
