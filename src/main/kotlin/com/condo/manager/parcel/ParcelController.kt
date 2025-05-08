package com.condo.manager.parcel

import com.condo.manager.dto.ParcelCreationDto
import com.condo.manager.dto.ParcelDto
import com.condo.manager.dto.ParcelStatusUpdateDto
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/parcels")
class ParcelController(private val parcelService: ParcelService) {

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    fun createParcel(
        @Valid @RequestBody parcelCreationDto: ParcelCreationDto,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ParcelDto> {
        // In a real application, you would extract the user ID from userDetails
        // For now, we'll assume there's a method to get the ID
        val managerId = getUserIdFromUserDetails(userDetails)
        val createdParcel = parcelService.createParcel(parcelCreationDto, managerId)
        return ResponseEntity(createdParcel, HttpStatus.CREATED)
    }

    @PutMapping("/status")
    @PreAuthorize("hasRole('MANAGER')")
    fun updateParcelStatus(
        @Valid @RequestBody statusUpdateDto: ParcelStatusUpdateDto,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ParcelDto> {
        val managerId = getUserIdFromUserDetails(userDetails)
        val updatedParcel = parcelService.updateParcelStatus(statusUpdateDto, managerId)
        return ResponseEntity(updatedParcel, HttpStatus.OK)
    }

    @GetMapping
    fun getAllParcels(pageable: Pageable): ResponseEntity<Page<ParcelDto>> {
        // This would typically have filtering logic
        // For this example, we'll just return parcels by room
        val parcels = parcelService.getParcelsByRoom(1, pageable) // Example room ID
        return ResponseEntity(parcels, HttpStatus.OK)
    }

    @GetMapping("/{id}")
    fun getParcelById(@PathVariable id: Long): ResponseEntity<ParcelDto> {
        val parcel = parcelService.getParcelById(id)
        return ResponseEntity(parcel, HttpStatus.OK)
    }

    @GetMapping("/room/{roomId}")
    fun getParcelsByRoom(
        @PathVariable roomId: Long,
        pageable: Pageable
    ): ResponseEntity<Page<ParcelDto>> {
        val parcels = parcelService.getParcelsByRoom(roomId, pageable)
        return ResponseEntity(parcels, HttpStatus.OK)
    }

    @GetMapping("/user/{userId}")
    fun getParcelsByUser(
        @PathVariable userId: Long,
        pageable: Pageable
    ): ResponseEntity<Page<ParcelDto>> {
        val parcels = parcelService.getParcelsByUser(userId, pageable)
        return ResponseEntity(parcels, HttpStatus.OK)
    }

    @GetMapping("/my-parcels")
    fun getMyParcels(
        @AuthenticationPrincipal userDetails: UserDetails,
        pageable: Pageable
    ): ResponseEntity<Page<ParcelDto>> {
        val userId = getUserIdFromUserDetails(userDetails)
        val parcels = parcelService.getParcelsByUser(userId, pageable)
        return ResponseEntity(parcels, HttpStatus.OK)
    }

    // Helper method to extract user ID from UserDetails
    // In a real application, you would implement this based on your authentication mechanism
    private fun getUserIdFromUserDetails(userDetails: UserDetails): Long {
        // This is a placeholder. In a real app, you would extract the user ID
        // from your custom implementation of UserDetails
        return 1L // Example user ID
    }
}