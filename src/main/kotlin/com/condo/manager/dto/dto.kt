package com.condo.manager.dto


import com.condo.manager.parcel.ParcelStatus
import com.condo.manager.user.UserRole
import java.time.LocalDateTime

// User DTOs
data class UserRegistrationDto(
    val username: String,
    val email: String,
    val password: String,
    val fullName: String,
    val phone: String? = null,
    val role: UserRole = UserRole.MANAGER,
)


data class UserDto(
    val id: Long,
    val username: String,
    val email: String,
    val fullName: String,
    val phone: String?,
    val userRole: UserRole
)

// Room DTOs
data class RoomCreationDto(
    val building: String,
    val floor: String,
    val roomNumber: String
)

data class RoomDto(
    val id: Long,
    val roomId: String,
    val building: String,
    val floor: String,
    val roomNumber: String
)

data class RoomAssignmentDto(
    val userId: Long,
    val roomId: Long
)
data class RoomCodeDto(
    val roomNumber: String,
    val roomCode: String,
)

// Parcel DTOs
data class ParcelCreationDto(
    val roomId: Long,
    val recipientId: Long,
    val trackingNumber: String?,
    val carrier: String?,
    val description: String?
)

data class ParcelDto(
    val id: Long,
    val roomDetails: RoomDto,
    val recipientDetails: UserDto,
    val managerDetails: UserDto,
    val trackingNumber: String?,
    val carrier: String?,
    val description: String?,
    val status: ParcelStatus,
    val deliveredAt: LocalDateTime?,
    val collectedAt: LocalDateTime?,
    val createdAt: LocalDateTime
)

data class ParcelStatusUpdateDto(
    val id: Long,
    val status: ParcelStatus
)

// Notification DTOs
data class NotificationDto(
    val id: Long,
    val message: String,
    val parcelId: Long,
    val isRead: Boolean,
    val createdAt: LocalDateTime
)