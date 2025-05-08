package com.condo.manager.room

import org.springframework.stereotype.Component
import java.security.SecureRandom

@Component
class RoomIdGenerator {
    private val secureRandom = SecureRandom()
    private val characters = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijklmnp!@*#23456789"

    /**
     * Generates a unique, easy-to-type room ID.
     * Format: XXXX-XXXX (where X is a character from the allowed set)
     */
    fun generate(): String {
        val firstPart = (0 until 4).map { characters[secureRandom.nextInt(characters.length)] }.joinToString("")
        val secondPart = (0 until 4).map { characters[secureRandom.nextInt(characters.length)] }.joinToString("")

        return "$firstPart-$secondPart"
    }

    /**
     * Generates a unique room ID that doesn't exist in the database.
     */
    fun generateUniqueRoomId(existsCheck: (String) -> Boolean): String {
        var roomId: String
        do {
            roomId = generate()
        } while (existsCheck(roomId))

        return roomId
    }
}