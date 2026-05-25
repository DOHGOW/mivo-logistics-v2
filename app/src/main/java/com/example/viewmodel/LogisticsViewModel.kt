package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.db.LogisticsRepository
import com.example.db.UserEntity
import com.example.firebase.FirebaseSyncManager
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LogisticsViewModel(val repository: LogisticsRepository) : ViewModel() {

    // Global navigation states
    private val _currentScreen = MutableStateFlow(NavigationScreen.SPLASH)
    val currentScreen: StateFlow<NavigationScreen> = _currentScreen.asStateFlow()

    private val _currentRole = MutableStateFlow(UserRole.CUSTOMER)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Authentic details
    private val _authEmail = MutableStateFlow("")
    val authEmail: StateFlow<String> = _authEmail.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    // Customer / Booking State
    private val _availableTrucks = MutableStateFlow<List<Truck>>(emptyList())
    val availableTrucks: StateFlow<List<Truck>> = _availableTrucks.asStateFlow()

    private val _selectedTruck = MutableStateFlow<Truck?>(null)
    val selectedTruck: StateFlow<Truck?> = _selectedTruck.asStateFlow()

    // Form states
    var pickupAddress = MutableStateFlow("42 Trans-Sahara Highway Cargo Hub, Sector GP")
    var destinationAddress = MutableStateFlow("85 coastal Container Terminal, Berth 12B")
    var pickupDate = MutableStateFlow("Today, May 22")
    var pickupTime = MutableStateFlow("ASAP (Within 25 mins)")
    var cargoType = MutableStateFlow("Industrial General Goods")
    var cargoWeightKg = MutableStateFlow(5200)
    var cargoDimensions = MutableStateFlow("4.2m x 2.1m x 2.0m")
    var fragileToggle = MutableStateFlow(false)
    var priorityToggle = MutableStateFlow(true)

    // Current Booking & History
    private val _currentBooking = MutableStateFlow<Booking?>(null)
    val currentBooking: StateFlow<Booking?> = _currentBooking.asStateFlow()

    private val _bookingHistory = MutableStateFlow<List<Booking>>(emptyList())
    val bookingHistory: StateFlow<List<Booking>> = _bookingHistory.asStateFlow()

    // Real-time tracking animation states
    private val _routeSimulatedProgress = MutableStateFlow(0.0f)
    val routeSimulatedProgress: StateFlow<Float> = _routeSimulatedProgress.asStateFlow()

    private val _simulatedSpeedKmph = MutableStateFlow(0f)
    val simulatedSpeedKmph: StateFlow<Float> = _simulatedSpeedKmph.asStateFlow()

    private val _simulatedEtaMinutes = MutableStateFlow(0)
    val simulatedEtaMinutes: StateFlow<Int> = _simulatedEtaMinutes.asStateFlow()

    // Chat states
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Financial & Wallet states
    private val _walletBalance = MutableStateFlow(12500.00)
    val walletBalance: StateFlow<Double> = _walletBalance.asStateFlow()

    private val _walletTransactions = MutableStateFlow<List<WalletTransaction>>(emptyList())
    val walletTransactions: StateFlow<List<WalletTransaction>> = _walletTransactions.asStateFlow()

    // Driver specific states
    private val _driverOnline = MutableStateFlow(true)
    val driverOnline: StateFlow<Boolean> = _driverOnline.asStateFlow()

    private val _driverTodayEarnings = MutableStateFlow(420.00)
    val driverTodayEarnings: StateFlow<Double> = _driverTodayEarnings.asStateFlow()

    private val _driverTripRequests = MutableStateFlow<List<Booking>>(emptyList())
    val driverTripRequests: StateFlow<List<Booking>> = _driverTripRequests.asStateFlow()

    // Fleet & Admin dashboards states
    private val _fleetVehicles = MutableStateFlow<List<FleetVehicle>>(emptyList())
    val fleetVehicles: StateFlow<List<FleetVehicle>> = _fleetVehicles.asStateFlow()

    private val _fleetInsight = MutableStateFlow(
        FleetInsight(
            activeFleetTrucks = 14,
            grossRevenue = 142500.00,
            liveBookings = 3,
            dispatchEfficiency = 98,
            fuelSavedLiters = 1420
        )
    )
    val fleetInsight: StateFlow<FleetInsight> = _fleetInsight.asStateFlow()

    private var trackingJob: Job? = null
    private var dbCollectionJob: Job? = null

    init {
        // Hydrate default realistic assets
        populateMockTrucks()
        hydrateSimulationRequests()
        
        // Let's populate mock fleet vehicles dynamically if they are empty
        viewModelScope.launch {
            repository.getAllVehicles().collect { vehicles ->
                if (vehicles.isEmpty()) {
                    val defaults = listOf(
                        FleetVehicle("FL1", "TX-1002-SF", "Bedford Boxster", "Daniel Peterson", 12.5f, 98, "ONLINE", 0),
                        FleetVehicle("FL2", "TX-9988-CA", "Super Titan Hauler", "Marcus Vance", 34.2f, 85, "ONLINE", 85),
                        FleetVehicle("FL3", "TX-1144-NV", "Mega Flatbed Steel", "Ahmed Al-Bakary", 28.0f, 92, "ONLINE", 40),
                        FleetVehicle("FL4", "TX-Reef-88", "ThermoCold Freezer", "Sergei Orlov", 19.8f, 74, "MAINTENANCE", 0),
                        FleetVehicle("FL5", "TX-8833-OH", "Goliath Iron Trailer", "Unassigned", 42.0f, 60, "OFFLINE", 0)
                    )
                    repository.insertVehicles(defaults)
                } else {
                    _fleetVehicles.value = vehicles
                }
            }
        }
    }

    // Role switcher
    fun setRole(role: UserRole) {
        _currentRole.value = role
        // Redirect screen automatically to appropriate home
        if (_isAuthenticated.value) {
            _currentScreen.value = NavigationScreen.DASHBOARD_CUSTOMER
        } else {
            _currentScreen.value = NavigationScreen.AUTH
        }
    }

    // Navigation helper
    fun navigateTo(screen: NavigationScreen) {
        _currentScreen.value = screen
    }

    // Authenticate / Login User (Supports Database match and custom presets Seeding)
    fun authenticate(email: String, passwordText: String = "12345", onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            val user = repository.getUser(email)
            if (user != null) {
                if (user.password == passwordText) {
                    onLoginSuccess(user)
                    onResult(true, "Success")
                } else {
                    onResult(false, "Invalid network passkey")
                }
            } else {
                // Seeding click-and-run support for preset demo users
                if (email.contains("@mivo.logistics")) {
                    val defaultRole = when {
                        email.contains("shipper") -> UserRole.CUSTOMER
                        email.contains("vance") || email.contains("driver") -> UserRole.DRIVER
                        email.contains("fleet") -> UserRole.FLEET_OWNER
                        email.contains("dispatcher") || email.contains("admin") -> UserRole.DISPATCH_ADMIN
                        else -> UserRole.CUSTOMER
                    }
                    val defaultName = when (defaultRole) {
                        UserRole.CUSTOMER -> "Enterprise Shipper"
                        UserRole.DRIVER -> "Marcus Vance"
                        UserRole.FLEET_OWNER -> "Fleet Operator Nexus"
                        UserRole.DISPATCH_ADMIN -> "Alpha Dispatcher"
                    }
                    val created = UserEntity(email, passwordText.ifBlank { "12345" }, defaultName, defaultRole.name, 12500.00)
                    repository.insertUser(created)
                    seedUserInitialMockData(email, defaultRole)
                    onLoginSuccess(created)
                    onResult(true, "Success")
                } else {
                    onResult(false, "Profile ID not registered. Support register tab!")
                }
            }
        }
    }

    // Register User with SQLite Persistence Support!
    fun register(name: String, email: String, passkey: String, role: UserRole, onResult: (Boolean, String) -> Unit) {
        if (name.isBlank() || email.isBlank() || passkey.isBlank()) {
            onResult(false, "All parameters are required!")
            return
        }
        viewModelScope.launch {
            val existing = repository.getUser(email)
            if (existing != null) {
                onResult(false, "Email is already registered!")
            } else {
                val newUser = UserEntity(
                    email = email,
                    password = passkey,
                    name = name,
                    role = role.name,
                    walletBalance = if (role == UserRole.CUSTOMER) 15000.00 else 0.0
                )
                repository.insertUser(newUser)
                seedUserInitialMockData(email, role)
                onLoginSuccess(newUser)
                onResult(true, "Account registered successfully!")
            }
        }
    }

    private fun onLoginSuccess(user: UserEntity) {
        _authEmail.value = user.email
        _currentRole.value = try {
            UserRole.valueOf(user.role)
        } catch (e: Exception) {
            UserRole.CUSTOMER
        }
        _isAuthenticated.value = true

        // Bind DB Flows
        dbCollectionJob?.cancel()
        dbCollectionJob = viewModelScope.launch {
            // Flow: Wallet Balance
            launch {
                repository.getUserFlow(user.email).collect { curr ->
                    if (curr != null) {
                        _walletBalance.value = curr.walletBalance
                    }
                }
            }
            // Flow: Support Chats
            launch {
                repository.getChatForUser(user.email).collect { chats ->
                    _chatMessages.value = chats
                }
            }
            // Flow: Ledger Payments
            launch {
                repository.getTransactionsForUser(user.email).collect { txs ->
                    _walletTransactions.value = txs
                }
            }
            // Flow: Historic Bookings
            launch {
                repository.getBookingsForUser(user.email).collect { bookings ->
                    _bookingHistory.value = bookings
                    val active = bookings.firstOrNull { it.status != ShipmentStatus.DELIVERED }
                    if (active != null) {
                        _currentBooking.value = active
                    }
                }
            }
        }

        // Redirect appropriately
        _currentScreen.value = NavigationScreen.DASHBOARD_CUSTOMER
    }

    fun logout() {
        _isAuthenticated.value = false
        _currentScreen.value = NavigationScreen.ROLE_SELECTION
    }

    // Selection helpers
    fun selectTruck(truck: Truck) {
        _selectedTruck.value = truck
    }

    // Booking trigger (Saves to Room database first before starting simulations!)
    fun confirmBooking(paymentMethod: String) {
        val truck = _selectedTruck.value ?: _availableTrucks.value.first()
        val weight = cargoWeightKg.value
        val isFragile = fragileToggle.value
        val isPriority = priorityToggle.value

        val weightSurcharge = (weight * 0.12)
        val priorityMultiplier = if (isPriority) 1.25 else 1.0
        val totalBookingValue = (truck.price + weightSurcharge) * priorityMultiplier

        val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val bookingTimeStr = format.format(Date())
        val bookingId = "MIVO-TX-${(100000..999999).random()}"

        val trackerStatus = ShipmentStatus.BOOKED
        val initialMilestones = listOf(
            ShipmentMilestone(ShipmentStatus.BOOKED, "$bookingTimeStr - Core dispatch log created", "HQ Server Node A", true),
            ShipmentMilestone(ShipmentStatus.DRIVER_ASSIGNED, "--:-- Pending alignment", "Terminal Depot Alpha", false),
            ShipmentMilestone(ShipmentStatus.CARGO_PICKED_UP, "--:-- Awaiting container load", pickupAddress.value, false),
            ShipmentMilestone(ShipmentStatus.IN_TRANSIT, "--:-- Custom optimization check", "Sahara Highway Route 1", false),
            ShipmentMilestone(ShipmentStatus.DELIVERED, "--:-- Gate reception verified", destinationAddress.value, false)
        )

        val newBooking = Booking(
            id = bookingId,
            pickupLocation = pickupAddress.value,
            destination = destinationAddress.value,
            date = pickupDate.value,
            time = pickupTime.value,
            selectedTruck = truck,
            cargoType = cargoType.value,
            weightKg = weight,
            dimensions = cargoDimensions.value,
            isFragile = isFragile,
            priorityCargo = isPriority,
            price = totalBookingValue,
            status = trackerStatus,
            milestones = initialMilestones
        )

        _currentBooking.value = newBooking

        // Backup booking to Firebase Database
        FirebaseSyncManager.syncBooking(_authEmail.value, newBooking)

        viewModelScope.launch {
            // Save Booking directly to the user's SQL DB profile!
            repository.insertBooking(_authEmail.value, newBooking)

            // Deduct from wallet if wallet selected
            if (paymentMethod == "Wallet Balance") {
                val newBal = _walletBalance.value - totalBookingValue
                repository.updateWalletBalance(_authEmail.value, newBal)
                
                val transaction = WalletTransaction(
                    id = "WLT-DEB-${(1000..9999).random()}",
                    title = "Shipment #$bookingId Pre-auth",
                    amount = totalBookingValue,
                    type = "DEBIT",
                    date = "Today",
                    reference = bookingId
                )
                repository.insertTransaction(_authEmail.value, transaction)
            }

            // Populate driver job board too
            _driverTripRequests.value = listOf(newBooking) + _driverTripRequests.value

            _currentScreen.value = NavigationScreen.BOOKING_SUCCESS

            // Start route transit simulation
            startLiveTransitSimulation(newBooking)
        }
    }

    private fun startLiveTransitSimulation(booking: Booking) {
        trackingJob?.cancel()
        _routeSimulatedProgress.value = 0.0f
        _simulatedSpeedKmph.value = 0f
        _simulatedEtaMinutes.value = booking.selectedTruck.etaMinutes

        trackingJob = viewModelScope.launch {
            // Milestone 1 -> BOOKED. Let's wait 3.5 seconds
            _simulatedSpeedKmph.value = 0f
            delay(3500)

            // Milestone 2 -> DRIVER ASSIGNED
            updateBookingStatus(ShipmentStatus.DRIVER_ASSIGNED, "Driver ${_currentBooking.value?.selectedTruck?.driverName} accepted load.", "Depot Alpha Central")
            _simulatedSpeedKmph.value = 45f
            _routeSimulatedProgress.value = 0.15f
            delay(4000)

            // Milestone 3 -> PICKED UP
            updateBookingStatus(ShipmentStatus.CARGO_PICKED_UP, "Seal code MIVO-${(1000..9999).random()} successfully locked. Weight confirmed.", booking.pickupLocation)
            _simulatedSpeedKmph.value = 78f
            _routeSimulatedProgress.value = 0.35f
            delay(5000)

            // Milestone 4 -> IN TRANSIT
            updateBookingStatus(ShipmentStatus.IN_TRANSIT, "Route Optimized via expressway. Predictive highway ETA active.", "Highway Node 4 - Bypass")
            
            // Simulating continuous progress bar moving
            for (step in 1..10) {
                _simulatedSpeedKmph.value = (70..85).random().toFloat()
                _routeSimulatedProgress.value = 0.35f + (step * 0.05f)
                val etaFrac = booking.selectedTruck.etaMinutes - (booking.selectedTruck.etaMinutes * (step * 0.08f)).toInt()
                _simulatedEtaMinutes.value = etaFrac.coerceIn(5, 120)
                delay(1200)
            }

            // Milestone 5 -> NEAR DESTINATION
            _routeSimulatedProgress.value = 0.90f
            updateBookingStatus(ShipmentStatus.NEAR_DESTINATION, "Entering customs check-point. Security queue verified.", "Port Terminal Zone")
            _simulatedSpeedKmph.value = 25f
            delay(4000)

            // Milestone 6 -> DELIVERED
            _routeSimulatedProgress.value = 1.0f
            updateBookingStatus(ShipmentStatus.DELIVERED, "Delivered successfully. Confirmed via biometric signature.", booking.destination)
            _simulatedSpeedKmph.value = 0f
            _simulatedEtaMinutes.value = 0

            // Add the driver earnings
            _driverTodayEarnings.value += booking.price * 0.85
        }
    }

    private suspend fun updateBookingStatus(status: ShipmentStatus, details: String, location: String) {
        val booking = _currentBooking.value ?: return
        val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val timeStr = format.format(Date())

        val updatedMilestones = booking.milestones.map { milestone ->
            if (milestone.status == status) {
                ShipmentMilestone(status, "$timeStr - $details", location, true)
            } else if (milestone.status.ordinal < status.ordinal) {
                milestone.copy(done = true)
            } else {
                milestone
            }
        }

        val updatedBooking = booking.copy(
            status = status,
            milestones = updatedMilestones
        )

        _currentBooking.value = updatedBooking
        repository.insertBooking(_authEmail.value, updatedBooking)

        // Sync status updates directly to Firebase database
        FirebaseSyncManager.syncBooking(_authEmail.value, updatedBooking)

        // Send a custom chat update from Driver
        val formatChat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeChat = formatChat.format(Date())
        val systemUpdate = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "Mivo System [ETA Engine]",
            senderRole = "AI Dispatcher",
            messageText = "🚨 Dispatch Update: App status changed to [${status.displayName}] at $location. Speed: ${_simulatedSpeedKmph.value.toInt()} km/h.",
            timestamp = timeChat,
            isAi = true
        )
        repository.insertChatMessage(_authEmail.value, systemUpdate)
        FirebaseSyncManager.syncChatMessage(_authEmail.value, systemUpdate)
    }

    // Driver: toggle status
    fun toggleDriverOnlineState() {
        _driverOnline.value = !_driverOnline.value
    }

    // Driver: Accept pending booking
    fun driverAcceptTrip(booking: Booking) {
        startLiveTransitSimulation(booking)
        _driverTripRequests.value = _driverTripRequests.value.filter { it.id != booking.id }
    }

    // Wallet Interactions (Saves balance dynamically to SQL User profile)
    fun addWalletFunds(amount: Double) {
        viewModelScope.launch {
            val updatedBal = _walletBalance.value + amount
            repository.updateWalletBalance(_authEmail.value, updatedBal)
            
            val transaction = WalletTransaction(
                id = "WLT-CRD-${(1000..9999).random()}",
                title = "Card Refill - Visa Premium Gateway",
                amount = amount,
                type = "CREDIT",
                date = "Today",
                reference = "MIVO-DEP-${UUID.randomUUID().toString().take(6).uppercase()}"
            )
            repository.insertTransaction(_authEmail.value, transaction)
        }
    }

    // Send chat messages (Saves to Room database to retain history flawlessly!)
    fun sendUserMessage(text: String) {
        if (text.isBlank()) return
        val formatChat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeChat = formatChat.format(Date())
        
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "You",
            senderRole = "Shipper",
            messageText = text,
            timestamp = timeChat
        )
        
        viewModelScope.launch {
            repository.insertChatMessage(_authEmail.value, userMsg)
            FirebaseSyncManager.syncChatMessage(_authEmail.value, userMsg)

            // AI automated responsive response triggers
            delay(1500)
            val aiResponseText = when {
                text.lowercase(Locale.ROOT).contains("pricing") || text.contains("cost") || text.contains("price") -> {
                    "Mivo AI Logistics Engine calculates fees dynamically using fuel surcharge index (\$1.22/L), trailer configuration specs, and peak operational tolls. Do you want custom cost optimization proposals?"
                }
                text.lowercase(Locale.ROOT).contains("eta") || text.contains("where") || text.contains("status") || text.contains("truck") -> {
                    val active = _currentBooking.value
                    if (active != null) {
                        "Your shipment #[${active.id}] in active transit is at *${active.milestones.find { it.done && it.status == active.status }?.location ?: "transit hub"}*. Current ETA indicator: ${_simulatedEtaMinutes.value} minutes remaining!"
                    } else {
                        "There is currently no active shipment routing on your dashboard. Start booking a truck first!"
                    }
                }
                text.lowercase(Locale.ROOT).contains("insurance") || text.contains("safe") || text.contains("fragile") -> {
                    "Mivo logistics partner operations cover comprehensive cargo-in-transit liability up to \$150,000. All dispatch containers feature high-security GPS electronic locks."
                }
                else -> {
                    "Hello! Mivo automated support terminal at your service. Here are high-importance telemetry options: type 'eta' to fetch live GPS updates, 'price' for cargo fuel rates, or 'claim' for fleet incident support."
                }
            }

            val aiMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                senderName = "Mivo Support Bot",
                senderRole = "System AI",
                messageText = aiResponseText,
                timestamp = timeChat,
                isAi = true
            )
            repository.insertChatMessage(_authEmail.value, aiMsg)
            FirebaseSyncManager.syncChatMessage(_authEmail.value, aiMsg)
        }
    }

    // Default Hydration values
    private fun populateMockTrucks() {
        _availableTrucks.value = listOf(
            Truck("T1", "Belfast Super Container", TruckType.CONTAINER, "18,000 kg Capacity", 2800.00, 4.9f, 15, "Marcus Vance", "+1 (555) 0192-33", plateNumber = "TX-9988-CA"),
            Truck("T2", "Interstate Titan Trailer", TruckType.TRAILER, "24,000 kg Capacity", 3500.00, 4.8f, 22, "Ahmed Al-Bakary", "+1 (555) 4302-12", plateNumber = "TX-1144-NV"),
            Truck("T3", "Reefer Temp-Controlled Pro", TruckType.REFRIGERATED, "10,000 kg Capacity", 2100.00, 4.7f, 8, "Sergei Orlov", "+1 (555) 8802-99", plateNumber = "TX-Reef-88"),
            Truck("T4", "Flatbed Steel Goliath", TruckType.FLATBED, "8,000 kg Capacity", 1500.00, 4.6f, 30, "John Miller", "+1 (555) 2039-44", plateNumber = "TX-8833-OH"),
            Truck("T5", "Sahara Expressway Tanker", TruckType.TANKER, "12,000 kg Capacity", 2200.00, 4.9f, 18, "Zara Larsson", "+1 (555) 0011-88", plateNumber = "TX-TNK-556"),
            Truck("T6", "Mini Urban Bedford Express", TruckType.MINI_TRUCK, "1,500 kg Capacity", 850.00, 4.9f, 5, "Daniel Peterson", "+1 (555) 7529-10", plateNumber = "TX-CITY-01")
        )
        _selectedTruck.value = _availableTrucks.value.first()
    }

    private suspend fun seedUserInitialMockData(email: String, role: UserRole) {
        if (role == UserRole.CUSTOMER) {
            val dummyTruck = Truck("TH-1", "Atlas Mega box", TruckType.CONTAINER, "15k kg", 2400.00, 4.8f, 10, "Roy Rogers", "+1 332", plateNumber = "TX-9988-CA")
            val historicBooking1 = Booking(
                id = "MIVO-TX-429810",
                pickupLocation = "Silicon Valley Depot Suite 4B",
                destination = "San Francisco Port Terminal 3",
                date = "Yesterday",
                time = "14:22",
                selectedTruck = dummyTruck,
                cargoType = "High-tech Lithium Batteries",
                weightKg = 8500,
                dimensions = "3m x 2m x 2m",
                isFragile = true,
                priorityCargo = true,
                price = 2850.00,
                status = ShipmentStatus.DELIVERED,
                milestones = listOf(
                    ShipmentMilestone(ShipmentStatus.BOOKED, "10:15 - Logged", "Hub SB", true),
                    ShipmentMilestone(ShipmentStatus.DELIVERED, "14:22 - Signature captured", "SF Cargo Berth", true)
                )
            )
            val historicBooking2 = Booking(
                id = "MIVO-TX-109402",
                pickupLocation = "Dallas Agro Yard A",
                destination = "Houston Cold Store Warehouse",
                date = "May 12",
                time = "Completed",
                selectedTruck = dummyTruck,
                cargoType = "Frozen Organic Vegetables",
                weightKg = 12000,
                dimensions = "10m x 2.4m x 2.4m",
                isFragile = false,
                priorityCargo = false,
                price = 1800.00,
                status = ShipmentStatus.DELIVERED,
                milestones = listOf(
                    ShipmentMilestone(ShipmentStatus.BOOKED, "12:00 - Logged", "Dallas", true),
                    ShipmentMilestone(ShipmentStatus.DELIVERED, "16:45 - Biometric checked", "Houston Cold", true)
                )
            )
            repository.insertBooking(email, historicBooking1)
            repository.insertBooking(email, historicBooking2)

            // Wallet details
            val tx1 = WalletTransaction("W1", "Card Refill - Visa *1244", 15000.00, "CREDIT", "Today", "TXN-880214829")
            val tx2 = WalletTransaction("W2", "Initial Promotional Cash reward", 250.00, "CREDIT", "Today", "TXN-442891048")
            repository.insertTransaction(email, tx1)
            repository.insertTransaction(email, tx2)

            val welcomeChat = ChatMessage(
                id = UUID.randomUUID().toString(),
                senderName = "Mivo Support Bot",
                senderRole = "System AI",
                messageText = "Welcome to Mivo! All sections are fully connected to your personal secure local database. You have matching starting ledger balances. Let's trace your first load shipment!",
                timestamp = "Just now",
                isAi = true
            )
            repository.insertChatMessage(email, welcomeChat)
        }
    }

    private fun hydrateSimulationRequests() {
        val dummyTruck = Truck("TH-1", "Atlas Mega box", TruckType.CONTAINER, "15,000 kg", 2400.0, 4.8f, 10, "Roy Rogers", "+1 332", plateNumber = "TX-9988-CA")
        _driverTripRequests.value = listOf(
            Booking(
                id = "MIVO-TX-773199",
                pickupLocation = "Petrochemical Hub Gate 3",
                destination = "Central Power grid Terminal C",
                date = "Today",
                time = "ASAP",
                selectedTruck = dummyTruck,
                cargoType = "Refined Energy Lubricants",
                weightKg = 16000,
                dimensions = "6m x 2.4m x 2.4m",
                isFragile = false,
                priorityCargo = true,
                price = 3200.00,
                status = ShipmentStatus.BOOKED,
                milestones = emptyList()
            )
        )
    }
}
