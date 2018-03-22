package com.gilbord.Controllers

import com.gilbord.Models.Chanel
import com.gilbord.Models.Point
import org.apache.log4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class CalculateController {

    val log = Logger.getLogger(CalculateController::class.java.name)

    @CrossOrigin
    @RequestMapping(value = ["/performance"],
            method = [RequestMethod.POST])
    public fun calculatePerformance(@RequestBody chanel: Chanel): ResponseEntity<Any> {
        log.info(chanel.toString())
        return if (chanel.validate()) ResponseEntity(chanel.performanceFunction(), HttpStatus.OK)
        else ResponseEntity(Error("Data doesn't valid"), HttpStatus.BAD_REQUEST)
    }

    @CrossOrigin
    @RequestMapping(value = ["/temperature"],
            method= [RequestMethod.POST])
    public fun calculateTemperature(@RequestBody chanel: Chanel): ResponseEntity<Any>{
        log.info(chanel.toString())
        return if (chanel.validate()) ResponseEntity(chanel.calculateTemperature(), HttpStatus.OK)
        else ResponseEntity("Data doesn't valid", HttpStatus.BAD_REQUEST)
    }

    @CrossOrigin
    @RequestMapping(value = ["/viscosity"],
            method= [RequestMethod.POST])
    public fun calculateViscosity(@RequestBody chanel: Chanel): ResponseEntity<Any>{
        log.info(chanel.toString())
        return if (chanel.validate()) ResponseEntity(chanel.calculateViscosity(), HttpStatus.OK)
        else ResponseEntity("Data doesn't valid", HttpStatus.BAD_REQUEST)
    }

}