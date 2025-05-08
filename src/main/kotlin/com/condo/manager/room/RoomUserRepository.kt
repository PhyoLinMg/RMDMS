package com.condo.manager.room


import com.condo.manager.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.List
import java.util.Optional

@Repository
interface RoomUserRepository : JpaRepository<RoomUser, Long> {
    fun findByUser(user: User): List<RoomUser>
    fun findByRoom(room: Room): List<RoomUser>
    fun findByUserAndRoom(user: User, room: Room): Optional<RoomUser>
}