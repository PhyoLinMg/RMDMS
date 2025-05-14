package com.condo.manager.user

import com.condo.manager.notification.Notification
import com.condo.manager.parcel.Parcel
import com.condo.manager.room.RoomUser
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val username: String = "",

    @Column(nullable = false)
    val password: String = "",

    @Column(unique = true, nullable = false)
    val email: String = "",

    @Column(name = "full_name", nullable = false)
    val fullName: String = "",

    @Column
    val phone: String? = null,

    @Column(name = "user_role", nullable = false)
    val userRole: UserRole = UserRole.TENANT,

    @OneToMany(mappedBy = "user")
    val roomAssignments: MutableList<RoomUser> = mutableListOf(),

    @OneToMany(mappedBy = "manager")
    val managedParcels: MutableList<Parcel> = mutableListOf(),

    @OneToMany(mappedBy = "recipient")
    val receivedParcels: MutableList<Parcel> = mutableListOf(),

    @OneToMany(mappedBy = "user")
    val notifications: MutableList<Notification> = mutableListOf(),

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    override fun toString(): String {
        return "User(id=$id, username='$username', userRole=$userRole)"
        // Don't include roomAssignments in toString()
    }
}

enum class UserRole {
    TENANT,
    OWNER,
    MANAGER
}