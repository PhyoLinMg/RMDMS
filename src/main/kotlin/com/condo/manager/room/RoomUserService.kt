package com.condo.manager.room

import com.condo.manager.dto.RoomAssignmentDto
import com.condo.manager.dto.RoomDto
import com.condo.manager.dto.UserDto
import com.condo.manager.dto.mapToUserDto
import com.condo.manager.user.UserRepository
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class RoomUserService(
    private val roomUserRepository: RoomUserRepository,
    private val userRepository: UserRepository,
    private val roomRepository: RoomRepository
) {

    @Transactional
    fun assignUserToRoom(userId:Long, room: Room): RoomUser {
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found with id: $userId") }

        // Check if the assignment already exists
        roomUserRepository.findByUserAndRoom(user, room).ifPresent {
            throw IllegalArgumentException("User is already assigned to this room")
        }

        val roomUser = RoomUser(
            user = user,
            room = room
        )

        return roomUserRepository.save(roomUser)
    }

    fun getUserRooms(userId: Long): List<RoomDto> {
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found with id: $userId") }

        return roomUserRepository.findByUser(user).map {
            RoomDto(
                id = it.room.id,
                roomId = it.room.roomId,
                building = it.room.building,
                floor = it.room.floor,
                roomNumber = it.room.roomNumber,
                //roomAssignments = it.room.roomAssignments.map { room-> mapToUserDto(room.user) }
            )
        }
    }

    fun getRoomUsers(roomId: Long): List<UserDto> {
        val room = roomRepository.findById(roomId)
            .orElseThrow { EntityNotFoundException("Room not found with id: $roomId") }

        return roomUserRepository.findByRoom(room).map {
            UserDto(
                id = it.user.id,
                username = it.user.username,
                email = it.user.email,
                fullName = it.user.fullName,
                phone = it.user.phone,
                userRole = it.user.userRole
            )
        }
    }
}