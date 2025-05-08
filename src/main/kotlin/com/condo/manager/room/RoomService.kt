package com.condo.manager.room

import com.condo.manager.dto.RoomCodeDto
import com.condo.manager.dto.RoomCreationDto
import com.condo.manager.dto.RoomDto
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class RoomService(
    private val roomRepository: RoomRepository,
    private val roomIdGenerator: RoomIdGenerator
) {

    @Transactional
    @PreAuthorize("hasRole('MANAGER')")
    fun createRoom(roomCreationDto: RoomCreationDto): RoomDto {
        // Check if room with same building, floor and room number already exists
        roomRepository.findByBuildingAndFloorAndRoomNumber(
            roomCreationDto.building,
            roomCreationDto.floor,
            roomCreationDto.roomNumber
        ).ifPresent {
            throw IllegalArgumentException("Room already exists in this location")
        }

        // Generate a unique room ID
        val uniqueRoomId = roomIdGenerator.generateUniqueRoomId { roomId ->
            roomRepository.existsByRoomId(roomId)
        }

        val room = Room(
            roomId = uniqueRoomId,
            building = roomCreationDto.building,
            floor = roomCreationDto.floor,
            roomNumber = roomCreationDto.roomNumber
        )

        val savedRoom = roomRepository.save(room)
        return mapToRoomDto(savedRoom)
    }

    fun getRoomByRoomId(roomId: String): RoomDto {
        val room = roomRepository.findByRoomId(roomId)
            .orElseThrow { EntityNotFoundException("Room not found with roomId: $roomId") }
        return mapToRoomDto(room)
    }

    fun getRoomById(id: Long): RoomDto {
        val room = roomRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Room not found with id: $id") }
        return mapToRoomDto(room)
    }

    fun getRoomCodes(): List<RoomCodeDto>{
        return roomRepository.findAll().map { RoomCodeDto(it.roomNumber, it.roomId) }
    }
    fun getAllRooms(): List<RoomDto> {
        return roomRepository.findAll().map { mapToRoomDto(it) }
    }

    private fun mapToRoomDto(room: Room): RoomDto {
        return RoomDto(
            id = room.id,
            roomId = room.roomId,
            building = room.building,
            floor = room.floor,
            roomNumber = room.roomNumber
        )
    }
}
