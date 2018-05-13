package com.gilbord.Controllers

import com.gilbord.Models.DAO.User
import com.gilbord.Models.UserDetailsImpl
import com.gilbord.Repositories.UserRepository
import com.gilbord.Services.ValueService
import jdk.nashorn.internal.parser.JSONParser
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest


@RestController
class UserController {

    val log = Logger.getLogger(ValueService::class.java.name)

    @Autowired
    private lateinit var userRepository: UserRepository

//    @CrossOrigin
//    @RequestMapping(value = ["/login"], method = [(RequestMethod.POST)])
//    fun login(@RequestBody user: User): ResponseEntity<Any> {
//        val userInBase = userRepository.findByUsernameAndPassword(user.username, user.password)
//        return if(userInBase != null) ResponseEntity(user.getBaseToken(), HttpStatus.OK)
//        else ResponseEntity(HttpStatus.BAD_REQUEST)
//    }

    @CrossOrigin
    @RequestMapping(value = ["/signin"], method = [RequestMethod.POST])
    fun login2(): ResponseEntity<Any> {
        val auth = SecurityContextHolder.getContext().authentication
        val isAdmin = (auth.principal as UserDetailsImpl).user.isAdmin
        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body("{\"isAdmin\":$isAdmin}")
    }

    @CrossOrigin
    @RequestMapping(value = ["/signout"], method = [RequestMethod.GET])
    fun logout(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Any> {
        log.info("signout")
        val auth = SecurityContextHolder.getContext().authentication
        if (auth != null) {
            SecurityContextLogoutHandler().logout(request, response, auth)
        }
        return ResponseEntity(HttpStatus.OK)
    }

    @RequestMapping(value = ["/registration"], method = [RequestMethod.POST])
    fun register(@RequestBody user: User): ResponseEntity<Any> {
        userRepository.save(user)
        return ResponseEntity(HttpStatus.OK)
    }

}

data class IsAdmin(val isAdmin: Boolean)