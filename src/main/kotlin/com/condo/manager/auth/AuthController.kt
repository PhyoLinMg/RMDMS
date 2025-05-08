package com.condo.manager.auth

import com.condo.manager.user.UserRole
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {
    data class AuthRequest(val username: String, val password: String, val isTenant: Boolean = true)
    data class RefreshRequest(val refreshToken: String)

    @PostMapping("/register")
    fun register(@RequestBody request: AuthRequest): Map<String, String> = authService.register(
        request.username, request.password, role = if (request.isTenant) UserRole.TENANT else UserRole.OWNER
    )

    @PostMapping("/login")
    fun login(@RequestBody request: AuthRequest): Map<String, String> =
        authService.login(request.username, request.password)

    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshRequest): Map<String, String> = authService.refresh(request.refreshToken)
}
