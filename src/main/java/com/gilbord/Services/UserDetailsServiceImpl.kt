package com.gilbord.Services

import com.gilbord.Models.UserDetailsImpl
import com.gilbord.Repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl: UserDetailsService {

    @Autowired
    private lateinit var userRepository: UserRepository

    override fun loadUserByUsername(username: String?): UserDetails {
        val user = this.userRepository.findByUsername(username) ?: throw UsernameNotFoundException(username)
        return UserDetailsImpl(user)
    }
}