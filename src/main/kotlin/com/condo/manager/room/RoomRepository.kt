package com.condo.manager.room

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface RoomRepository : JpaRepository<Room, Long> {
    fun findByRoomId(roomId: String): Optional<Room>
    fun existsByRoomId(roomId: String): Boolean
    fun findByBuildingAndFloorAndRoomNumber(building: String, floor: String, roomNumber: String): Optional<Room>
}
