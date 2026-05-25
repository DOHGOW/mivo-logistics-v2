package com.example.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val password: String,
    val name: String,
    val role: String, // CUSTOMER, DRIVER, FLEET_OWNER, DISPATCH_ADMIN
    val walletBalance: Double = 12500.00
)

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String,
    val userEmail: String,
    val pickupLocation: String,
    val destination: String,
    val date: String,
    val time: String,
    val cargoType: String,
    val weightKg: Int,
    val dimensions: String,
    val isFragile: Boolean,
    val priorityCargo: Boolean,
    val price: Double,
    val status: String, // ShipmentStatus name
    val milestonesSerialized: String, // Serialized log history
    
    // Truck Snapshotted Fields
    val truckId: String,
    val truckName: String,
    val truckType: String,
    val truckCapacity: String,
    val truckPrice: Double,
    val truckRating: Float,
    val truckEtaMinutes: Int,
    val truckDriverName: String,
    val truckDriverPhone: String,
    val truckPlateNumber: String
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val userEmail: String,
    val senderName: String,
    val senderRole: String,
    val messageText: String,
    val timestamp: String,
    val isVoice: Boolean = false,
    val isAi: Boolean = false
)

@Entity(tableName = "wallet_transactions")
data class WalletTransactionEntity(
    @PrimaryKey val id: String,
    val userEmail: String,
    val title: String,
    val amount: Double,
    val type: String, // CREDIT, DEBIT
    val date: String,
    val reference: String
)

@Entity(tableName = "fleet_vehicles")
data class FleetVehicleEntity(
    @PrimaryKey val id: String,
    val plateNumber: String,
    val model: String,
    val activeDriver: String,
    val fuelEfficiency: Float,
    val healthScore: Int,
    val status: String, // ONLINE, MAINTENANCE, OFFLINE
    val loadPercentage: Int
)
