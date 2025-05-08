package com.condo.manager.parcel

import com.condo.manager.room.Room
import com.condo.manager.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.Optional

@Repository
interface ParcelRepository : JpaRepository<Parcel, Long> {
    fun findByRoom(room: Room, pageable: Pageable): Page<Parcel>
    fun findByRecipient(recipient: User, pageable: Pageable): Page<Parcel>
    fun findByManager(manager: User, pageable: Pageable): Page<Parcel>
    fun findByStatus(status: ParcelStatus, pageable: Pageable): Page<Parcel>
    fun findByRoomAndStatus(room: Room, status: ParcelStatus, pageable: Pageable): Page<Parcel>
    fun findByRecipientAndStatus(recipient: User, status: ParcelStatus, pageable: Pageable): Page<Parcel>
    fun findByCreatedAtBetween(start: LocalDateTime, end: LocalDateTime, pageable: Pageable): Page<Parcel>
    fun findByTrackingNumber(trackingNumber: String): Optional<Parcel>
}