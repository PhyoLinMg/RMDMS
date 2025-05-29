package com.condo.manager.user

import com.condo.manager.dto.UserDto
import com.condo.manager.dto.UserRegistrationDto
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/management")
    fun createUser(@RequestBody userRegistrationDto: UserRegistrationDto): UserDto = userService.registerUser(userRegistrationDto)

    @GetMapping("/all")
    fun getAllUser(): List<UserDto> = userService.findAll()

    @GetMapping("/all/roles")
    fun getUserByRole(@RequestParam role: String): List<UserDto> = userService.findUserByRole(role)

    @DeleteMapping("/all")
    fun deleteAllUser(){
        userService.deleteAll()
    }
}
