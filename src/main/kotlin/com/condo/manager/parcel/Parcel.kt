package com.condo.manager.parcel
import com.condo.manager.notification.Notification
import com.condo.manager.room.Room
import com.condo.manager.user.User
import jakarta.persistence.*
import java.time.LocalDateTime
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp

@Entity
@Table(name = "parcels")
data class Parcel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    val room: Room,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    val manager: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    val recipient: User,

    @Column(name = "tracking_number")
    val trackingNumber: String? = null,

    @Column
    val carrier: String? = null,

    @Column
    val description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ParcelStatus = ParcelStatus.DELIVERED,

    @Column(name = "delivered_at")
    var deliveredAt: LocalDateTime? = null,

    @Column(name = "collected_at")
    var collectedAt: LocalDateTime? = null,

    @OneToMany(mappedBy = "parcel")
    val notifications: MutableList<Notification> = mutableListOf(),

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class ParcelStatus {
    DELIVERED,
    COLLECTED
}

