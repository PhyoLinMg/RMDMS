package com.condo.manager.notification


import com.condo.manager.dto.NotificationDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notifications")
class NotificationController(private val notificationService: NotificationService) {

    @GetMapping
    fun getMyNotifications(
        @AuthenticationPrincipal userDetails: UserDetails,
        pageable: Pageable
    ): ResponseEntity<Page<NotificationDto>> {
        val userId = getUserIdFromUserDetails(userDetails)
        val notifications = notificationService.getUserNotifications(userId, pageable)
        return ResponseEntity(notifications, HttpStatus.OK)
    }

    @GetMapping("/unread-count")
    fun getUnreadNotificationsCount(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<Long> {
        val userId = getUserIdFromUserDetails(userDetails)
        val count = notificationService.getUnreadNotificationsCount(userId)
        return ResponseEntity(count, HttpStatus.OK)
    }

    @PutMapping("/{id}/read")
    fun markNotificationAsRead(
        @PathVariable id: Long
    ): ResponseEntity<NotificationDto> {
        val notification = notificationService.markNotificationAsRead(id)
        return ResponseEntity(notification, HttpStatus.OK)
    }

    // Helper method to extract user ID from UserDetails
    // In a real application, you would implement this based on your authentication mechanism
    private fun getUserIdFromUserDetails(userDetails: UserDetails): Long {
        // This is a placeholder. In a real app, you would extract the user ID
        // from your custom implementation of UserDetails
        return 1L // Example user ID
    }
}