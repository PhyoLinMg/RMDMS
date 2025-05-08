package com.condo.manager.notification


import com.condo.manager.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findByUser(user: User, pageable: Pageable): Page<Notification>
    fun findByUserAndIsRead(user: User, isRead: Boolean, pageable: Pageable): Page<Notification>
    fun countByUserAndIsRead(user: User, isRead: Boolean): Long
}