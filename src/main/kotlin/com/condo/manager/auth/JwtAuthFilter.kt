package com.condo.manager.auth

import com.condo.manager.error.UnauthorizedException
import com.condo.manager.user.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.filter.OncePerRequestFilter
import kotlin.jvm.optionals.getOrNull

@Component
class JwtAuthFilter(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)

            if (jwtService.isTokenValid(token)) {
                val username = jwtService.extractUsername(token)
                val user = userRepository.findByUsername(username).getOrNull()


                if (user != null && SecurityContextHolder.getContext().authentication == null) {
                    val auth = UsernamePasswordAuthenticationToken(
                        user, null, listOf(SimpleGrantedAuthority("ROLE_${user.userRole}"))
                    )
                    auth.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = auth
                }
            }
            //else throw UnauthorizedException("Invalid token")
        }

        filterChain.doFilter(request, response)
    }
}
