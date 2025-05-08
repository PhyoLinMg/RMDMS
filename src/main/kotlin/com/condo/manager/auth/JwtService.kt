package com.condo.manager.auth
import com.condo.manager.user.UserRole
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService {
    private val secretKey: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    private val accessTokenExpiration = 1000 * 60 * 15  // 15 minutes
    private val refreshTokenExpiration = 1000 * 60 * 60 * 24 * 7  // 7 days

    fun generateAccessToken(userId: String,userRole: UserRole): String =
        Jwts.builder()
            .setSubject(userId)
            .setExpiration(Date(System.currentTimeMillis() + accessTokenExpiration))
            .signWith(secretKey)
            .claim("roles", listOf("ROLE_$userRole"))
            .compact()

    fun generateRefreshToken(userId: String,userRole: UserRole): String =
        Jwts.builder()
            .setSubject(userId)
            .setExpiration(Date(System.currentTimeMillis() + refreshTokenExpiration))
            .signWith(secretKey)
            .claim("roles", listOf("ROLE_$userRole"))
            .compact()

    fun extractUsername(token: String): String =
        Jwts.parserBuilder().setSigningKey(secretKey).build()
            .parseClaimsJws(token).body.subject

    fun isTokenValid(token: String): Boolean =
        try {
            val claims = Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token)
            !claims.body.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
}
