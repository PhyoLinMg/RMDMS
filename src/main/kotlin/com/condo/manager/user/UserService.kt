package com.condo.manager.user


import com.condo.manager.dto.UserDto
import com.condo.manager.dto.UserRegistrationDto
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Service
class UserService(
    private val userRepository: UserRepository,
): UserDetailsService {
    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()
    @Transactional
    fun registerUser(registrationDto: UserRegistrationDto): UserDto {
        if (userRepository.existsByUsername(registrationDto.username)) {
            throw IllegalArgumentException("Username already exists")
        }

        if (userRepository.existsByEmail(registrationDto.email)) {
            throw IllegalArgumentException("Email already exists")
        }

        val user = User(
            username = registrationDto.username,
            password = passwordEncoder.encode(registrationDto.password),
            email = registrationDto.email,
            fullName = registrationDto.fullName,
            phone = registrationDto.phone,
            userRole = registrationDto.role
        )

        val savedUser = userRepository.save(user)
        return mapToUserDto(savedUser)
    }

    fun findAll(): List<UserDto> {
        return userRepository.findAll().map { mapToUserDto(it) }
    }
    fun deleteAll(){
        userRepository.deleteAll()
    }

    fun getUserById(id: Long): UserDto {
        val user = userRepository.findById(id)
            .orElseThrow { EntityNotFoundException("User not found with id: $id") }
        return mapToUserDto(user)
    }

    private fun mapToUserDto(user: User): UserDto {
        return UserDto(
            id = user.id,
            username = user.username,
            email = user.email,
            fullName = user.fullName,
            phone = user.phone,
            userRole = user.userRole
        )
    }

    override fun loadUserByUsername(username: String?): UserDetails? {
        val user= userRepository.findByUsername(username!!)
            .orElseThrow { EntityNotFoundException("User not found with username: $username") }
        return org.springframework.security.core.userdetails.User(
            user.username,
            user.password,
            listOf(SimpleGrantedAuthority("ROLE_"+ user.userRole))
        )
    }
}