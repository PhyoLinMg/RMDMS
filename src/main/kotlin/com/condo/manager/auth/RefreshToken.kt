package com.condo.manager.auth

import com.condo.manager.user.User
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "refresh_tokens")
open class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(unique = false)
    var token: String = ""

    @OneToOne
    @JoinColumn(name = "user_id")
    lateinit var user: User

    var expiryDate: Date = Date()
}
