package com.condo.manager.parcel

import com.condo.manager.dto.ParcelCreationDto
import com.condo.manager.dto.ParcelResponseDTO
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
import org.springframework.data.domain.Sort
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder

@RestController
@RequestMapping("/api/parcels")
class ParcelController(private val parcelService: ParcelService, private val userService: UserService) {

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    fun createParcel(
        @Valid @RequestBody parcelCreationDto: ParcelCreationDto,
        @AuthenticationPrincipal(errorOnInvalidType = true) userDetails: User
    ): ResponseEntity<ParcelResponseDTO> {
        return try {
            val createdParcel = parcelService.createParcel(parcelCreationDto, userDetails.id)

            ResponseEntity(createdParcel, HttpStatus.CREATED)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    @PutMapping("/status")
    @PreAuthorize("hasRole('MANAGER')")
    fun updateParcelStatus(
        @Valid @RequestBody statusUpdateDto: ParcelStatusUpdateDto,
        @AuthenticationPrincipal(errorOnInvalidType = true) userDetails: User
    ): ResponseEntity<ParcelResponseDTO> {

        val updatedParcel = parcelService.updateParcelStatus(statusUpdateDto, userDetails.id)
        return ResponseEntity(updatedParcel, HttpStatus.OK)
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    fun getAllParcelsWithRoomId(
        @RequestParam(required = false) page: Int = 0,
        @RequestParam roomId: Long
    ): ResponseEntity<Page<ParcelResponseDTO>> {
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
    ): ResponseEntity<Page<ParcelResponseDTO>> {
        val pageable= PageRequest.of(page, 5)
        val parcels = parcelService.getAllParcels(pageable)
        return ResponseEntity(parcels, HttpStatus.OK)
    }

    @GetMapping("/{id}")
    fun getParcelById(@PathVariable id: Long): ResponseEntity<ParcelResponseDTO> {
        val parcel = parcelService.getParcelById(id)
        return ResponseEntity(parcel, HttpStatus.OK)

    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('MANAGER')")
    fun getParcelsByUser(
        @RequestParam userId:Long,
        @RequestParam(required = false) page: Int = 0,
    ): ResponseEntity<Page<ParcelResponseDTO>> {
        val pageable = PageRequest.of(page, 5)
        val parcels = parcelService.getParcelsByUser(userId, pageable)
        return ResponseEntity(parcels, HttpStatus.OK)
    }

    @GetMapping("/mine")
    fun getMyParcels(
        @RequestParam(required = false) page: Int = 0,
        @AuthenticationPrincipal(errorOnInvalidType = true) user: User
    ): ResponseEntity<Page<ParcelResponseDTO>> {

        val pageable = PageRequest.of(page,5)

        val parcels = parcelService.getParcelsByUser(user.id, pageable)
        return ResponseEntity(parcels, HttpStatus.OK)
    }

    @GetMapping("/search")
    fun searchParcelsByRoom(
        @RequestParam roomNumber: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "5") size: Int,
    ): ResponseEntity<Page<ParcelResponseDTO>> {
        val pageable = PageRequest.of(
            page,
            size
        )

        return ResponseEntity.ok(
            parcelService.findParcelsByRoomNumber(roomNumber, pageable)
        )
    }

}