package com.condo.manager.config

import com.condo.manager.auth.JwtAuthFilter
import com.condo.manager.error.ErrorResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.awt.PageAttributes

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter,
    private val userDetailService: UserDetailsService
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers("/auth/**", "/api/users/**").permitAll().anyRequest().authenticated()
            }
            .exceptionHandling {
                it.authenticationEntryPoint { request, response, exception ->
                    response.contentType = MediaType.APPLICATION_JSON_VALUE
                    response.status = HttpStatus.UNAUTHORIZED.value()
                    val writer = response.writer
                    val error = ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Unauthorized: Authentication token was either missing or invalid.")
                    writer.println(ObjectMapper().writeValueAsString(error))
                }
                    .accessDeniedHandler { request, response, exception ->
                        response.contentType = MediaType.APPLICATION_JSON_VALUE
                        response.status = HttpStatus.FORBIDDEN.value()
                        val writer = response.writer
                        val error = ErrorResponse(HttpStatus.FORBIDDEN.value(), "Forbidden: Access denied for this resource.")
                        writer.println(ObjectMapper().writeValueAsString(error))
                    }

            }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .userDetailsService(userDetailService)
        return http.build()
    }

    @Bean
    fun userDetailService(): UserDetailsService {
        return userDetailService
    }
}
