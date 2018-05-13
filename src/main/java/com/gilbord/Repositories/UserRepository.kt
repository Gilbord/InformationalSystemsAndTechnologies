package com.gilbord.Repositories

import com.gilbord.Models.DAO.User
import org.springframework.data.repository.CrudRepository

interface UserRepository: CrudRepository<User, Long> {
    fun findByUsername(username: String?): User?
    fun findByUsernameAndPassword(username: String, password: String): User?
}