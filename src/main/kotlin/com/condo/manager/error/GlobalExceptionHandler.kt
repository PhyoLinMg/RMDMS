package com.condo.manager.error

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleForbiddenException(e:Exception): ErrorResponse {
        return ErrorResponse(HttpStatus.FORBIDDEN.value(), e.message ?: "Forbidden")
    }


    @ExceptionHandler(UnauthorizedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleUnauthorizedException(e:UnauthorizedException): ErrorResponse {
        return ErrorResponse(HttpStatus.UNAUTHORIZED.value(), e.message ?: "Unauthorized")
    }

    @ExceptionHandler(InvalidInputException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidInputException(e:InvalidInputException): ErrorResponse {
        return ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.message ?: "Invalid Input")
    }

}

data class ErrorResponse(
    val status: Int,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)