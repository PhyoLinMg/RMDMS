package com.condo.manager.parcel

import com.condo.manager.dto.ParcelCreationDto
import com.condo.manager.dto.ParcelDto
import com.condo.manager.dto.ParcelStatusUpdateDto
import com.condo.manager.dto.RoomDto
import com.condo.manager.dto.UserDto
import com.condo.manager.dto.mapToUserDto
import com.condo.manager.notification.NotificationService
import com.condo.manager.room.RoomRepository
import com.condo.manager.user.UserRepository
import com.condo.manager.user.UserRole
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ParcelService(
    private val parcelRepository: ParcelRepository,
    private val roomRepository: RoomRepository,
    private val userRepository: UserRepository,
    private val notificationService: NotificationService
) {

    @Transactional
    fun createParcel(parcelCreationDto: ParcelCreationDto, managerId: Long): ParcelDto {
        val room = roomRepository.findById(parcelCreationDto.roomId)
            .orElseThrow { EntityNotFoundException("Room not found with id: ${parcelCreationDto.roomId}") }

        val recipient = userRepository.findById(parcelCreationDto.recipientId)
            .orElseThrow { EntityNotFoundException("Recipient not found with id: ${parcelCreationDto.recipientId}") }

        val manager = userRepository.findById(managerId)
            .orElseThrow { EntityNotFoundException("Manager not found with id: $managerId") }

        if (manager.userRole != UserRole.MANAGER) {
            throw IllegalArgumentException("Only management users can create parcels")
        }

        val parcel = Parcel(
            room = room,
            recipient = recipient,
            manager = manager,
            trackingNumber = parcelCreationDto.trackingNumber,
            carrier = parcelCreationDto.carrier,
            description = parcelCreationDto.description,
            status = ParcelStatus.PENDING
        )

        val savedParcel = parcelRepository.save(parcel)

        // Create a notification for the recipient
        notificationService.createNotification(
            recipient.id,
            savedParcel.id,
            "A new parcel has been registered for you"
        )

        return mapToParcelDto(savedParcel)
    }

    @Transactional
    fun updateParcelStatus(statusUpdateDto: ParcelStatusUpdateDto, managerId: Long): ParcelDto {
        val parcel = parcelRepository.findById(statusUpdateDto.id)
            .orElseThrow { EntityNotFoundException("Parcel not found with id: ${statusUpdateDto.id}") }

        val manager = userRepository.findById(managerId)
            .orElseThrow { EntityNotFoundException("Manager not found with id: $managerId") }

        if (manager.userRole!= UserRole.MANAGER) {
            throw IllegalArgumentException("Only management users can update parcel status")
        }

        val oldStatus = parcel.status
        parcel.status = statusUpdateDto.status

        // Update timestamps based on status
        when (statusUpdateDto.status) {
            ParcelStatus.DELIVERED -> parcel.deliveredAt = LocalDateTime.now()
            ParcelStatus.COLLECTED -> parcel.collectedAt = LocalDateTime.now()
            else -> {}
        }

        val updatedParcel = parcelRepository.save(parcel)

        // Create a notification for the recipient about a status change
        if (oldStatus != statusUpdateDto.status) {
            val message = when (statusUpdateDto.status) {
                ParcelStatus.DELIVERED -> "Your parcel has been delivered to your condo"
                ParcelStatus.COLLECTED -> "Your parcel has been marked as collected"
                ParcelStatus.PENDING -> "Your parcel status has been changed to pending"
            }

            notificationService.createNotification(
                updatedParcel.recipient.id,
                updatedParcel.id,
                message
            )
        }

        return mapToParcelDto(updatedParcel)
    }

    fun getParcelsByRoom(roomId: Long, pageable: Pageable): Page<ParcelDto> {
        val room = roomRepository.findById(roomId)
            .orElseThrow { EntityNotFoundException("Room not found with id: $roomId") }

        return parcelRepository.findByRoom(room, pageable).map { mapToParcelDto(it) }
    }

    fun getParcelsByUser(userId: Long, pageable: Pageable): Page<ParcelDto> {
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found with id: $userId") }

        return parcelRepository.findByRecipient(user, pageable).map { mapToParcelDto(it) }
    }

    fun getParcelById(id: Long): ParcelDto {
        val parcel = parcelRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Parcel not found with id: $id") }
        return mapToParcelDto(parcel)
    }

    private fun mapToParcelDto(parcel: Parcel): ParcelDto {
        return ParcelDto(
            id = parcel.id,
            roomDetails = RoomDto(
                id = parcel.room.id,
                roomId = parcel.room.roomId,
                building = parcel.room.building,
                floor = parcel.room.floor,
                roomNumber = parcel.room.roomNumber,
                roomAssignments = parcel.room.roomAssignments.map { mapToUserDto(it.user) }
            ),
            recipientDetails = UserDto(
                id = parcel.recipient.id,
                username = parcel.recipient.username,
                email = parcel.recipient.email,
                fullName = parcel.recipient.fullName,
                phone = parcel.recipient.phone,
                userRole = parcel.manager.userRole
            ),
            managerDetails = UserDto(
                id = parcel.manager.id,
                username = parcel.manager.username,
                email = parcel.manager.email,
                fullName = parcel.manager.fullName,
                phone = parcel.manager.phone,
                userRole = parcel.manager.userRole
            ),
            trackingNumber = parcel.trackingNumber,
            carrier = parcel.carrier,
            description = parcel.description,
            status = parcel.status,
            deliveredAt = parcel.deliveredAt,
            collectedAt = parcel.collectedAt,
            createdAt = parcel.createdAt
        )
    }
}