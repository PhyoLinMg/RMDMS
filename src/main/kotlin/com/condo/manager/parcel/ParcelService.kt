package com.condo.manager.parcel

import com.condo.manager.dto.ParcelCreationDto
import com.condo.manager.dto.ParcelResponseDTO
import com.condo.manager.dto.ParcelStatusUpdateDto
import com.condo.manager.dto.RoomDto
import com.condo.manager.dto.UserDto

import com.condo.manager.notification.NotificationService
import com.condo.manager.room.RoomRepository
import com.condo.manager.user.UserRepository
import com.condo.manager.user.UserRole

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.cache.annotation.Cacheable
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
    fun createParcel(parcelCreationDto: ParcelCreationDto, managerId: Long): ParcelResponseDTO {
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
        )

        val savedParcel = parcelRepository.save(parcel)

        // Create a notification for the recipient
        notificationService.createNotification(
            recipient.id,
            savedParcel.id,
            "A new parcel has been registered for you"
        )

        return ParcelResponseDTO.from(savedParcel)
    }

    @Transactional
    fun updateParcelStatus(statusUpdateDto: ParcelStatusUpdateDto, managerId: Long): ParcelResponseDTO {
        val parcel = parcelRepository.findById(statusUpdateDto.id)
            .orElseThrow { EntityNotFoundException("Parcel not found with id: ${statusUpdateDto.id}") }

        val manager = userRepository.findById(managerId)
            .orElseThrow { EntityNotFoundException("Manager not found with id: $managerId") }

        if (manager.userRole!= UserRole.MANAGER) {
            throw IllegalArgumentException("Only management users can update parcel status")
        }

        val oldStatus = parcel.status
        parcel.status = ParcelStatus.valueOf(statusUpdateDto.status)

        // Update timestamps based on status
        when (ParcelStatus.valueOf(statusUpdateDto.status)) {
            ParcelStatus.DELIVERED -> parcel.deliveredAt = LocalDateTime.now()
            ParcelStatus.COLLECTED -> parcel.collectedAt = LocalDateTime.now()
        }

        val updatedParcel = parcelRepository.save(parcel)

        // Create a notification for the recipient about a status change
        if (oldStatus != ParcelStatus.valueOf(statusUpdateDto.status)) {
            val message = when (ParcelStatus.valueOf(statusUpdateDto.status)) {
                ParcelStatus.DELIVERED -> "Your parcel has been delivered to your condo"
                ParcelStatus.COLLECTED -> "Your parcel has been marked as collected"
            }

            notificationService.createNotification(
                updatedParcel.recipient.id,
                updatedParcel.id,
                message
            )
        }

        return ParcelResponseDTO.from(updatedParcel)
    }

    fun getAllParcels(pageable: Pageable): Page<ParcelResponseDTO> {
        return parcelRepository.findAll(pageable).map { ParcelResponseDTO.from(it) }
    }

    fun getParcelsByRoom(roomId: Long, pageable: Pageable): Page<ParcelResponseDTO> {
        val room = roomRepository.findById(roomId)
            .orElseThrow { EntityNotFoundException("Room not found with id: $roomId") }

        return parcelRepository.findByRoom(room, pageable).map { ParcelResponseDTO.from(it) }
    }

    fun getParcelsByUser(userId: Long, pageable: Pageable): Page<ParcelResponseDTO> {
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found with id: $userId") }

        return parcelRepository.findByRecipient(user, pageable).map { ParcelResponseDTO.from(it) }
    }

    fun getParcelById(id: Long): ParcelResponseDTO {
        val parcel = parcelRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Parcel not found with id: $id") }
        return ParcelResponseDTO.from(parcel)
    }


    @Cacheable(value = ["parcelsByRoom"], key = "#roomNumber + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    fun findParcelsByRoomNumber(
        roomNumber: String,
        pageable: Pageable
    ): Page<ParcelResponseDTO> {
        return parcelRepository
            .findParcelsByRoomNumber(roomNumber, pageable)
            .map { ParcelResponseDTO.from(it) }
    }

}