package com.condo.manager.room

import com.condo.manager.parcel.Parcel
import jakarta.persistence.*
import java.time.LocalDateTime
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp

@Entity
@Table(name = "rooms")
data class Room(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "room_id", unique = true, nullable = false)
    val roomId: String,

    @Column(nullable = false)
    val building: String,

    @Column(nullable = false)
    val floor: String,

    @Column(name = "room_number", nullable = false)
    val roomNumber: String,

    @OneToMany(mappedBy = "room")
    val roomAssignments: MutableList<RoomUser> = mutableListOf(),

    @OneToMany(mappedBy = "room")
    val parcels: MutableList<Parcel> = mutableListOf(),

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)