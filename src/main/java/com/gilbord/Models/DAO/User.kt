package com.gilbord.Models.DAO

import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import javax.persistence.*


@Entity
@Table(name = "USERS")
data class User(@Id
                @GeneratedValue(strategy = GenerationType.AUTO)
                private val id: Long,
                @Column(name = "username", nullable = false, unique = true)
                val username: String,
                @Column(name = "password", nullable = false)
                val password: String,
                @Column(name = "isAdmin")
                val isAdmin: Boolean = false) {
    //private val passwordInBase = this.getSHA512Password(this.password)

    private fun getSHA512Password(password: String): String {
        val digest = MessageDigest.getInstance("SHA-512")
        digest.reset()
        digest.update(password.toByteArray(Charsets.UTF_8))
        return String.format("%040x", BigInteger(1, digest.digest()))
    }

    public fun getBaseToken(): String {
        return Base64.getEncoder().encodeToString("${this.username}:${this.password}".toByteArray())
    }
}