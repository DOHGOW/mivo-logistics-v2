package com.example.firebase

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.db.BookingEntity
import com.example.db.ChatMessageEntity
import com.example.model.Booking
import com.example.model.ChatMessage
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object FirebaseSyncManager {
    private const val TAG = "FirebaseSyncManager"

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    private val _isLiveFirebase = MutableStateFlow(false)
    val isLiveFirebase: StateFlow<Boolean> = _isLiveFirebase.asStateFlow()

    private var firebaseApp: FirebaseApp? = null
    private var database: FirebaseDatabase? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    sealed interface SyncStatus {
        object Idle : SyncStatus
        object Syncing : SyncStatus
        data class Success(val message: String) : SyncStatus
        data class Error(val error: String) : SyncStatus
    }

    fun initialize(context: Context) {
        val apiKey = BuildConfig.FIREBASE_API_KEY
        val projectId = BuildConfig.FIREBASE_PROJECT_ID
        val appId = BuildConfig.FIREBASE_APPLICATION_ID
        val databaseUrl = BuildConfig.FIREBASE_DATABASE_URL

        val hasValidKeys = apiKey.isNotEmpty() && apiKey != "YOUR_FIREBASE_API_KEY" &&
                projectId.isNotEmpty() && projectId != "YOUR_FIREBASE_PROJECT_ID" &&
                appId.isNotEmpty() && appId != "YOUR_FIREBASE_APPLICATION_ID" &&
                databaseUrl.isNotEmpty() && databaseUrl != "YOUR_FIREBASE_DATABASE_URL"

        if (!hasValidKeys) {
            Log.w(TAG, "Firebase credentials not configured. Running in local simulation mode.")
            _isLiveFirebase.value = false
            return
        }

        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                firebaseApp = FirebaseApp.getInstance()
            } else {
                val options = FirebaseOptions.Builder()
                    .setApiKey(apiKey)
                    .setProjectId(projectId)
                    .setApplicationId(appId)
                    .setDatabaseUrl(databaseUrl)
                    .build()
                firebaseApp = FirebaseApp.initializeApp(context, options)
            }

            database = FirebaseDatabase.getInstance(firebaseApp!!)
            _isLiveFirebase.value = true
            Log.i(TAG, "Firebase Live Database successfully connected!")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase Live: ${e.message}", e)
            _isLiveFirebase.value = false
        }
    }

    /**
     * Sends/Synces a booking entry to the Live Firebase Realtime Database
     */
    fun syncBooking(userEmail: String, booking: Booking) {
        if (!_isLiveFirebase.value) {
            Log.i(TAG, "Simulated Sync: Successfully backed up booking ${booking.id} to Simulated Cloud Database!")
            _syncStatus.value = SyncStatus.Success("Booking verified in local sandbox sync.")
            return
        }

        scope.launch {
            _syncStatus.value = SyncStatus.Syncing
            try {
                val dbRef = database?.getReference("users")
                    ?.child(sanitizeEmail(userEmail))
                    ?.child("bookings")
                    ?.child(booking.id)

                if (dbRef != null) {
                    val map = mapOf(
                        "id" to booking.id,
                        "pickupLocation" to booking.pickupLocation,
                        "destination" to booking.destination,
                        "date" to booking.date,
                        "time" to booking.time,
                        "cargoType" to booking.cargoType,
                        "weightKg" to booking.weightKg,
                        "dimensions" to booking.dimensions,
                        "isFragile" to booking.isFragile,
                        "priorityCargo" to booking.priorityCargo,
                        "price" to booking.price,
                        "status" to booking.status.name,
                        "truckId" to booking.selectedTruck.id,
                        "truckName" to booking.selectedTruck.name,
                        "driverName" to booking.selectedTruck.driverName,
                        "driverPhone" to booking.selectedTruck.driverPhone,
                        "plateNumber" to booking.selectedTruck.plateNumber
                    )
                    
                    dbRef.setValue(map)
                        .addOnSuccessListener {
                            _syncStatus.value = SyncStatus.Success("Live database backup updated!")
                        }
                        .addOnFailureListener {
                            _syncStatus.value = SyncStatus.Error("Realtime sync failed: ${it.message}")
                        }
                }
            } catch (e: Exception) {
                _syncStatus.value = SyncStatus.Error("Error: ${e.message}")
            }
        }
    }

    /**
     * Syces a chat message to the Live Firebase Realtime Database
     */
    fun syncChatMessage(userEmail: String, message: ChatMessage) {
        if (!_isLiveFirebase.value) {
            return
        }

        scope.launch {
            try {
                val dbRef = database?.getReference("users")
                    ?.child(sanitizeEmail(userEmail))
                    ?.child("chats")
                    ?.child(message.id)

                if (dbRef != null) {
                    val map = mapOf(
                        "id" to message.id,
                        "senderName" to message.senderName,
                        "senderRole" to message.senderRole,
                        "messageText" to message.messageText,
                        "timestamp" to message.timestamp,
                        "isVoice" to message.isVoice,
                        "isAi" to message.isAi
                    )
                    dbRef.setValue(map)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Chat sync exception: ${e.message}")
            }
        }
    }

    private fun sanitizeEmail(email: String): String {
        return email.replace(".", "_").replace("@", "_")
    }
}
