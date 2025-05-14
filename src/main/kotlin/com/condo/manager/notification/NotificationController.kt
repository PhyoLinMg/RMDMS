package com.condo.manager.notification


import com.condo.manager.dto.NotificationDto
import com.condo.manager.user.User
import com.condo.manager.user.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notifications")
class NotificationController(private val notificationService: NotificationService,private val userService: UserService) {

    @GetMapping
    fun getMyNotifications(
        @RequestParam(required = false) page: Int = 0,
        @AuthenticationPrincipal(errorOnInvalidType = true) userDetails: User
    ): ResponseEntity<Page<NotificationDto>> {
        val pageable = Pageable.ofSize(5).withPage(page)
        val notifications = notificationService.getUserNotifications(userDetails.id, pageable)
        return ResponseEntity(notifications, HttpStatus.OK)
    }

    @GetMapping("/unread-count")
    fun getUnreadNotificationsCount(
        @AuthenticationPrincipal userDetails: User
    ): ResponseEntity<Long> {

        val count = notificationService.getUnreadNotificationsCount(userDetails.id)
        return ResponseEntity(count, HttpStatus.OK)
    }

    @PutMapping("/{id}/read")
    fun markNotificationAsRead(
        @PathVariable id: Long
    ): ResponseEntity<NotificationDto> {
        val notification = notificationService.markNotificationAsRead(id)
        return ResponseEntity(notification, HttpStatus.OK)
    }

}