package com.condo.manager.auth

import com.condo.manager.user.User
import com.condo.manager.user.UserRepository
import com.condo.manager.user.UserRole
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import kotlin.apply
import kotlin.jvm.optionals.getOrNull

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtService: JwtService
) {
    private val passwordEncoder = BCryptPasswordEncoder()

    fun register(username: String, password: String, role: UserRole, email: String): Map<String, String> {
        if (userRepository.findByUsername(username).getOrNull() != null)
            throw IllegalArgumentException("Username exists")

        val user = User().copy(
            username = username,
            userRole = role,
            email = email,
            password = passwordEncoder.encode(password),
        )

        val savedUser = userRepository.save(user)

        return generateTokenPair(savedUser)
    }


    fun login(username: String, password: String): Map<String, String> {
        val user = userRepository.findByUsername(username).getOrNull()
            ?: throw IllegalArgumentException("Invalid credentials")

        if (!passwordEncoder.matches(password, user.password))
            throw IllegalArgumentException("Invalid credentials")

        return generateTokenPair(user)
    }

    fun refresh(refreshToken: String): Map<String, String> {
        val token = refreshTokenRepository.findByToken(refreshToken)
            ?: throw IllegalArgumentException("Invalid refresh token")

        if (token.expiryDate.before(Date()))
            throw IllegalArgumentException("Refresh token expired")

        return generateTokenPair(token.user)
    }

    private fun generateTokenPair(user: User): Map<String, String> {
        println("generate token pair here")
        val accessTokenNew = jwtService.generateAccessToken(user.username, userRole = user.userRole)
        val refreshTokenNew = jwtService.generateRefreshToken(user.username, userRole = user.userRole)

        val existingToken = refreshTokenRepository.findByUser(user)
        val refreshToken = existingToken?.apply {
            this.token = refreshTokenNew
            this.expiryDate = Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)
        } ?: RefreshToken().apply {
            this.token = refreshTokenNew
            this.user = user
            this.expiryDate = Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)
        }

        refreshTokenRepository.save(refreshToken)

        return mapOf(
            "accessToken" to accessTokenNew,
            "refreshToken" to refreshTokenNew
        )
    }
}
