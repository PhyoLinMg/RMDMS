package com.condo.manager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@SpringBootApplication
@EnableMethodSecurity
@EnableWebSecurity
class ManagerApplication

fun main(args: Array<String>) {
	runApplication<ManagerApplication>(*args)
}
//docker run --name postgres-spring -e POSTGRES_PASSWORD=linmaung -e POSTGRES_USER=postgres -e POSTGRES_DB=room_management -p 5432:5432 -d postgres
//docker exec -it postgres-spring psql -U postgres -d room_management