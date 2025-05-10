package com.condo.manager.error

class UnauthorizedException(message: String) : RuntimeException(message)
class InvalidInputException(message: String) : RuntimeException(message)