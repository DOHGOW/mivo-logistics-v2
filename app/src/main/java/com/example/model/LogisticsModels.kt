package com.example.model

enum class UserRole(val displayName: String, val description: String) {
    CUSTOMER("Shipper / Customer", "Book trucks, track cargo in real-time, and manage invoices."),
    DRIVER("Professional Driver", "Accept high-paying loads, view optimized routes, and upload signatures."),
    FLEET_OWNER("Fleet Operator", "Monitor vehicle health, fuel metrics, and assign drivers to trucks."),
    DISPATCH_ADMIN("Enterprise Dispatcher", "Command center dashboard with route telemetry and security queues.")
}

enum class NavigationScreen {
    SPLASH,
    ONBOARDING,
    ROLE_SELECTION,
    AUTH,
    DASHBOARD_CUSTOMER,
    SELECT_TRUCK,
    TRUCK_DETAILS,
    PAYMENT_CHECKOUT,
    BOOKING_SUCCESS,
    LIVE_TRACKING,
    WALLET,
    HISTORY,
    CHAT_SUPPORT
}

enum class TruckType(val displayName: String, val basePrice: Double, val capacityKg: Int) {
    MINI_TRUCK("Mini Cargo Bedford", 850.00, 1500),
    FLATBED("Heavy-Duty Flatbed", 1500.00, 8000),
    TANKER("Refrigerated Tanker", 2200.00, 12000),
    TRAILER("Commercial Multi-Trailer", 3500.00, 24000),
    CONTAINER("Secure Container Box", 2800.00, 18000),
    REFRIGERATED("Temp-Controlled Fridge", 2100.00, 10000),
    HEAVY_DUTY("Titan Ultra Hauler", 4800.00, 35000)
}

data class Truck(
    val id: String,
    val name: String,
    val type: TruckType,
    val capacity: String,
    val price: Double,
    val rating: Float,
    val etaMinutes: Int,
    val driverName: String,
    val driverPhone: String,
    val verified: Boolean = true,
    val insuranceStatus: String = "Certified Insured",
    val plateNumber: String
)

enum class ShipmentStatus(val displayName: String, val description: String) {
    BOOKED("Booking Confirmed", "Load has been registered and verified by dispatch engine."),
    DRIVER_ASSIGNED("Driver Dispatched", "Driver is heading to the specified pickup hub."),
    EN_ROUTE_PICKUP("En Route to Pickup", "Vehicle is within 2 km of loading point."),
    CARGO_PICKED_UP("Cargo Loaded", "Loading verified. Gate clearance approved, GPS seal locked."),
    IN_TRANSIT("In Transit", "Optimized highway route active. Transit speed: 72 km/h."),
    NEAR_DESTINATION("Near Destination", "Entering delivery zone. Preparing gate pass credentials."),
    DELIVERED("Cargo Delivered", "Successfully delivered. Digital signature & photo proof captured.")
}

data class ShipmentMilestone(
    val status: ShipmentStatus,
    val timestamp: String,
    val location: String,
    val done: Boolean
)

data class Booking(
    val id: String,
    val pickupLocation: String,
    val destination: String,
    val date: String,
    val time: String,
    val selectedTruck: Truck,
    val cargoType: String,
    val weightKg: Int,
    val dimensions: String,
    val isFragile: Boolean,
    val priorityCargo: Boolean,
    val price: Double,
    var status: ShipmentStatus,
    val milestones: List<ShipmentMilestone>
)

data class ChatMessage(
    val id: String,
    val senderName: String,
    val senderRole: String,
    val messageText: String,
    val timestamp: String,
    val isVoice: Boolean = false,
    val isAi: Boolean = false
)

data class FleetVehicle(
    val id: String,
    val plateNumber: String,
    val model: String,
    val activeDriver: String,
    val fuelEfficiency: Float, // liters/100km
    val healthScore: Int, // 0-100
    val status: String, // ONLINE, MAINTENANCE, OFFLINE
    val loadPercentage: Int
)

data class WalletTransaction(
    val id: String,
    val title: String,
    val amount: Double,
    val type: String, // CREDIT, DEBIT
    val date: String,
    val reference: String
)

data class FleetInsight(
    val activeFleetTrucks: Int,
    val grossRevenue: Double,
    val liveBookings: Int,
    val dispatchEfficiency: Int, // %
    val fuelSavedLiters: Int
)
