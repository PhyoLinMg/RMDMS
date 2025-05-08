package com.condo.manager.room

import jakarta.validation.Valid
import com.condo.manager.dto.RoomAssignmentDto
import com.condo.manager.dto.RoomCodeDto
import com.condo.manager.dto.RoomCreationDto
import com.condo.manager.dto.RoomDto
import com.condo.manager.dto.UserDto
import org.hibernate.internal.CoreLogging.logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.logging.Logger

@RestController
@RequestMapping("/api/rooms")
class RoomController(
    private val roomService: RoomService,
    private val roomUserService: RoomUserService
) {
    private val logger: Logger = Logger.getLogger(RoomController::class.java.name)

    @PostMapping("/create")
    @PreAuthorize("hasRole('MANAGER')")
    fun createRoom(@Valid @RequestBody roomCreationDto: RoomCreationDto, auth: Authentication): ResponseEntity<RoomDto> {
        logger.info("createRoom: ${auth.details}")
        val createdRoom = roomService.createRoom(roomCreationDto)
        return ResponseEntity(createdRoom, HttpStatus.CREATED)
    }

    @GetMapping
    fun getAllRooms(): ResponseEntity<List<RoomDto>> {
        val rooms = roomService.getAllRooms()
        return ResponseEntity(rooms, HttpStatus.OK)
    }

    @GetMapping("/{id}")
    fun getRoomById(@PathVariable id: Long): ResponseEntity<RoomDto> {
        val room = roomService.getRoomById(id)
        return ResponseEntity(room, HttpStatus.OK)
    }

    @GetMapping("/roomId/{roomId}")
    fun getRoomByRoomId(@PathVariable roomId: String): ResponseEntity<RoomDto> {
        val room = roomService.getRoomByRoomId(roomId)
        return ResponseEntity(room, HttpStatus.OK)
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('MANAGER')")
    fun assignUserToRoom(@Valid @RequestBody assignmentDto: RoomAssignmentDto): ResponseEntity<RoomUser> {
        val roomUser = roomUserService.assignUserToRoom(assignmentDto)
        return ResponseEntity(roomUser, HttpStatus.CREATED)
    }

    @GetMapping("/user/{userId}")
    fun getUserRooms(@PathVariable userId: Long): ResponseEntity<List<RoomDto>> {
        val rooms = roomUserService.getUserRooms(userId)
        return ResponseEntity(rooms, HttpStatus.OK)
    }

    @GetMapping("/codes")
    @PreAuthorize("hasRole('MANAGER')")
    fun getRoomCodes(): ResponseEntity<List<RoomCodeDto>>{
        val roomCodes = roomService.getRoomCodes()
        return ResponseEntity(roomCodes, HttpStatus.OK)
    }

    @GetMapping("/{roomId}/users")
    fun getRoomUsers(@PathVariable roomId: Long): ResponseEntity<List<UserDto>> {
        val users = roomUserService.getRoomUsers(roomId)
        return ResponseEntity(users, HttpStatus.OK)
    }
}