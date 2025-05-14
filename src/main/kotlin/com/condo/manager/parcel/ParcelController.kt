package com.condo.manager.parcel

import com.condo.manager.dto.ParcelCreationDto
import com.condo.manager.dto.ParcelDto
import com.condo.manager.dto.ParcelStatusUpdateDto
import com.condo.manager.user.User
import com.condo.manager.user.UserService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.context.SecurityContextHolder

@RestController
@RequestMapping("/api/parcels")
class ParcelController(private val parcelService: ParcelService, private val userService: UserService) {
    private val logger: Logger = LoggerFactory.getLogger(ParcelController::class.java)

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    fun createParcel(
        @Valid @RequestBody parcelCreationDto: ParcelCreationDto,
    ): ResponseEntity<ParcelDto> {
        // In a real application, you would extract the user ID from userDetails
        // For now, we'll assume there's a method to get the ID
        return try {
            val authentication = SecurityContextHolder.getContext().authentication

            val managerId = getUserIdFromUserDetails((authentication?.principal as User?)?.username ?: "")

            val createdParcel = parcelService.createParcel(parcelCreationDto, managerId)

            ResponseEntity(createdParcel, HttpStatus.CREATED)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    @PutMapping("/status")
    @PreAuthorize("hasRole('MANAGER')")
    fun updateParcelStatus(
        @Valid @RequestBody statusUpdateDto: ParcelStatusUpdateDto,
    ): ResponseEntity<ParcelDto> {
        val authentication = SecurityContextHolder.getContext().authentication

        val managerId = getUserIdFromUserDetails((authentication?.principal as User?)?.username ?: "")
        val updatedParcel = parcelService.updateParcelStatus(statusUpdateDto, managerId)
        return ResponseEntity(updatedParcel, HttpStatus.OK)
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    fun getAllParcelsWithRoomId(
        @RequestParam(required = false) page: Int = 0,
        @RequestParam roomId: Long
    ): ResponseEntity<Page<ParcelDto>> {
        // This would typically have filtering logic
        // For this example, we'll just return parcels by room
        val pageable = PageRequest.of(page, 5)
        val parcels = parcelService.getParcelsByRoom(roomId, pageable) // Example room ID
        return ResponseEntity(parcels, HttpStatus.OK)
    }

    // Page starts with 0 unlike laravel.
    @GetMapping("/all")
    @PreAuthorize("hasRole('MANAGER')")
    fun getAllParcels(
        @RequestParam(required = false) page: Int = 0,
    ): ResponseEntity<Page<ParcelDto>> {
        val pageable= PageRequest.of(page, 5)
        val parcels = parcelService.getAllParcels(pageable)
        return ResponseEntity(parcels, HttpStatus.OK)
    }

    @GetMapping("/{id}")
    fun getParcelById(@PathVariable id: Long): ResponseEntity<ParcelDto> {
        val parcel = parcelService.getParcelById(id)
        return ResponseEntity(parcel, HttpStatus.OK)

    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('MANAGER')")
    fun getParcelsByUser(
        @RequestParam userId:Long,
        @RequestParam(required = false) page: Int = 0,
    ): ResponseEntity<Page<ParcelDto>> {
        val pageable = PageRequest.of(page, 5)
        val parcels = parcelService.getParcelsByUser(userId, pageable)
        return ResponseEntity(parcels, HttpStatus.OK)
    }

    @GetMapping("/mine")
    fun getMyParcels(
        @RequestParam(required = false) page: Int = 0,
    ): ResponseEntity<Page<ParcelDto>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val pageable = PageRequest.of(page,5)
        val userId = getUserIdFromUserDetails((authentication?.principal as User?)?.username ?: "")
        val parcels = parcelService.getParcelsByUser(userId, pageable)
        return ResponseEntity(parcels, HttpStatus.OK)
    }


    // In a real application, you would implement this based on your authentication mechanism
    private fun getUserIdFromUserDetails(userName: String): Long {
        val user = userService.findByUsername(userName)
        return user.id
    }
}