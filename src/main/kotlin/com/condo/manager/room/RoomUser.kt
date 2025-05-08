package com.condo.manager.room

import com.condo.manager.user.User
import jakarta.persistence.*
import java.time.LocalDateTime
import org.hibernate.annotations.CreationTimestamp

@Entity
@Table(name = "room_users")
data class RoomUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    val room: Room,

    @CreationTimestamp
    @Column(name = "assigned_at", nullable = false, updatable = false)
    val assignedAt: LocalDateTime = LocalDateTime.now()
)
