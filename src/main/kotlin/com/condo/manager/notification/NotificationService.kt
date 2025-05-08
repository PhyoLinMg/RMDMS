package com.condo.manager.notification

import com.condo.manager.dto.NotificationDto
import com.condo.manager.parcel.ParcelRepository
import com.condo.manager.user.UserRepository
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
    private val parcelRepository: ParcelRepository
) {

    @Transactional
    fun createNotification(userId: Long, parcelId: Long, message: String): NotificationDto {
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found with id: $userId") }

        val parcel = parcelRepository.findById(parcelId)
            .orElseThrow { EntityNotFoundException("Parcel not found with id: $parcelId") }

        val notification = Notification(
            user = user,
            parcel = parcel,
            message = message
        )

        val savedNotification = notificationRepository.save(notification)
        return mapToNotificationDto(savedNotification)
    }

    fun getUserNotifications(userId: Long, pageable: Pageable): Page<NotificationDto> {
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found with id: $userId") }

        return notificationRepository.findByUser(user, pageable).map { mapToNotificationDto(it) }
    }

    fun getUnreadNotificationsCount(userId: Long): Long {
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found with id: $userId") }

        return notificationRepository.countByUserAndIsRead(user, false)
    }

    @Transactional
    fun markNotificationAsRead(id: Long): NotificationDto {
        val notification = notificationRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Notification not found with id: $id") }

        notification.isRead = true
        val updatedNotification = notificationRepository.save(notification)

        return mapToNotificationDto(updatedNotification)
    }

    private fun mapToNotificationDto(notification: Notification): NotificationDto {
        return NotificationDto(
            id = notification.id,
            message = notification.message,
            parcelId = notification.parcel.id,
            isRead = notification.isRead,
            createdAt = notification.createdAt
        )
    }
}