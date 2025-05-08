package com.condo.manager.auth

import com.condo.manager.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository: JpaRepository<RefreshToken, Long> {
    fun findByToken(token: String): RefreshToken?
    fun findByUser(user: User): RefreshToken?
}