package com.example.db

import com.example.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LogisticsRepository(private val db: AppDatabase) {

    private val userDao = db.userDao()
    private val bookingDao = db.bookingDao()
    private val chatMessageDao = db.chatMessageDao()
    private val walletTransactionDao = db.walletTransactionDao()
    private val fleetVehicleDao = db.fleetVehicleDao()

    // --- Users ---
    suspend fun getUser(email: String): UserEntity? = userDao.getUser(email)

    fun getUserFlow(email: String): Flow<UserEntity?> = userDao.getUserFlow(email)

    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)

    suspend fun updateWalletBalance(email: String, balance: Double) = userDao.updateWalletBalance(email, balance)


    // --- Bookings ---
    fun getBookingsForUser(userEmail: String): Flow<List<Booking>> {
        return bookingDao.getBookingsForUser(userEmail).map { entities ->
            entities.map { mapToBooking(it) }
        }
    }

    fun getAllBookings(): Flow<List<Booking>> {
        return bookingDao.getAllBookings().map { entities ->
            entities.map { mapToBooking(it) }
        }
    }

    suspend fun getBookingById(id: String): Booking? {
        val entity = bookingDao.getBookingById(id) ?: return null
        return mapToBooking(entity)
    }

    suspend fun insertBooking(userEmail: String, booking: Booking) {
        val entity = mapToBookingEntity(userEmail, booking)
        bookingDao.insertBooking(entity)
    }

    suspend fun updateBookingStatus(id: String, status: ShipmentStatus) {
        bookingDao.updateBookingStatus(id, status.name)
    }


    // --- Chats ---
    fun getChatForUser(userEmail: String): Flow<List<ChatMessage>> {
        return chatMessageDao.getChatForUser(userEmail).map { list ->
            list.map { mapToChatMessage(it) }
        }
    }

    suspend fun insertChatMessage(userEmail: String, message: ChatMessage) {
        chatMessageDao.insertChatMessage(mapToChatMessageEntity(userEmail, message))
    }

    suspend fun clearChat(userEmail: String) {
        chatMessageDao.clearChat(userEmail)
    }


    // --- Transactions ---
    fun getTransactionsForUser(userEmail: String): Flow<List<WalletTransaction>> {
        return walletTransactionDao.getTransactionsForUser(userEmail).map { list ->
            list.map { mapToWalletTransaction(it) }
        }
    }

    suspend fun insertTransaction(userEmail: String, transaction: WalletTransaction) {
        walletTransactionDao.insertTransaction(mapToWalletTransactionEntity(userEmail, transaction))
    }


    // --- Vehicles ---
    fun getAllVehicles(): Flow<List<FleetVehicle>> {
        return fleetVehicleDao.getAllVehicles().map { list ->
            list.map { mapToFleetVehicle(it) }
        }
    }

    suspend fun insertVehicles(vehicles: List<FleetVehicle>) {
        fleetVehicleDao.insertVehicles(vehicles.map { mapToFleetVehicleEntity(it) })
    }


    // --- Serializer Helpers for Milestones ---
    private fun serializeMilestones(milestones: List<ShipmentMilestone>): String {
        return milestones.joinToString("|") { m ->
            "${m.status.name}¤${m.timestamp}¤${m.location}¤${m.done}"
        }
    }

    private fun deserializeMilestones(str: String): List<ShipmentMilestone> {
        if (str.isEmpty()) return emptyList()
        return str.split("|").mapNotNull { part ->
            try {
                val sub = part.split("¤")
                if (sub.size >= 4) {
                    val statusValue = try {
                        ShipmentStatus.valueOf(sub[0])
                    } catch (e: Exception) {
                        ShipmentStatus.BOOKED
                    }
                    ShipmentMilestone(
                        status = statusValue,
                        timestamp = sub[1],
                        location = sub[2],
                        done = sub[3].toBoolean()
                    )
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }


    // --- Mapping Functions ---
    private fun mapToBooking(entity: BookingEntity): Booking {
        val truckTypeParsed = try {
            TruckType.valueOf(entity.truckType)
        } catch (e: Exception) {
            TruckType.CONTAINER
        }
        val shipmentStatusParsed = try {
            ShipmentStatus.valueOf(entity.status)
        } catch (e: Exception) {
            ShipmentStatus.BOOKED
        }

        return Booking(
            id = entity.id,
            pickupLocation = entity.pickupLocation,
            destination = entity.destination,
            date = entity.date,
            time = entity.time,
            selectedTruck = Truck(
                id = entity.truckId,
                name = entity.truckName,
                type = truckTypeParsed,
                capacity = entity.truckCapacity,
                price = entity.truckPrice,
                rating = entity.truckRating,
                etaMinutes = entity.truckEtaMinutes,
                driverName = entity.truckDriverName,
                driverPhone = entity.truckDriverPhone,
                verified = true,
                plateNumber = entity.truckPlateNumber
            ),
            cargoType = entity.cargoType,
            weightKg = entity.weightKg,
            dimensions = entity.dimensions,
            isFragile = entity.isFragile,
            priorityCargo = entity.priorityCargo,
            price = entity.price,
            status = shipmentStatusParsed,
            milestones = deserializeMilestones(entity.milestonesSerialized)
        )
    }

    private fun mapToBookingEntity(userEmail: String, domain: Booking): BookingEntity {
        val t = domain.selectedTruck
        return BookingEntity(
            id = domain.id,
            userEmail = userEmail,
            pickupLocation = domain.pickupLocation,
            destination = domain.destination,
            date = domain.date,
            time = domain.time,
            cargoType = domain.cargoType,
            weightKg = domain.weightKg,
            dimensions = domain.dimensions,
            isFragile = domain.isFragile,
            priorityCargo = domain.priorityCargo,
            price = domain.price,
            status = domain.status.name,
            milestonesSerialized = serializeMilestones(domain.milestones),
            truckId = t.id,
            truckName = t.name,
            truckType = t.type.name,
            truckCapacity = t.capacity,
            truckPrice = t.price,
            truckRating = t.rating,
            truckEtaMinutes = t.etaMinutes,
            truckDriverName = t.driverName,
            truckDriverPhone = t.driverPhone,
            truckPlateNumber = t.plateNumber
        )
    }

    private fun mapToChatMessage(e: ChatMessageEntity): ChatMessage {
        return ChatMessage(e.id, e.senderName, e.senderRole, e.messageText, e.timestamp, e.isVoice, e.isAi)
    }

    private fun mapToChatMessageEntity(userEmail: String, m: ChatMessage): ChatMessageEntity {
        return ChatMessageEntity(m.id, userEmail, m.senderName, m.senderRole, m.messageText, m.timestamp, m.isVoice, m.isAi)
    }

    private fun mapToWalletTransaction(e: WalletTransactionEntity): WalletTransaction {
        return WalletTransaction(e.id, e.title, e.amount, e.type, e.date, e.reference)
    }

    private fun mapToWalletTransactionEntity(userEmail: String, tx: WalletTransaction): WalletTransactionEntity {
        return WalletTransactionEntity(tx.id, userEmail, tx.title, tx.amount, tx.type, tx.date, tx.reference)
    }

    private fun mapToFleetVehicle(e: FleetVehicleEntity): FleetVehicle {
        return FleetVehicle(e.id, e.plateNumber, e.model, e.activeDriver, e.fuelEfficiency, e.healthScore, e.status, e.loadPercentage)
    }

    private fun mapToFleetVehicleEntity(v: FleetVehicle): FleetVehicleEntity {
        return FleetVehicleEntity(v.id, v.plateNumber, v.model, v.activeDriver, v.fuelEfficiency, v.healthScore, v.status, v.loadPercentage)
    }
}
