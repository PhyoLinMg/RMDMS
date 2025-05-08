package com.condo.manager.user

import com.condo.manager.dto.UserDto
import com.condo.manager.dto.UserRegistrationDto
import com.condo.manager.room.Room
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

    @DeleteMapping("/all")
    fun deleteAllUser(){
        userService.deleteAll()
    }
}
