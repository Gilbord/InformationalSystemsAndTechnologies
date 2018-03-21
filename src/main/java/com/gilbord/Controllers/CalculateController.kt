package com.gilbord.Controllers

import com.gilbord.Models.Chanel
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
    public fun calculatePerformance(@RequestBody chanel: Chanel): ResponseEntity<Float> {
        log.info(chanel.toString())
        return ResponseEntity(chanel.performanceFunction(), HttpStatus.OK)
    }

    @CrossOrigin
    @RequestMapping(value = ["/temperature"],
            method= [RequestMethod.POST])
    public fun calculateTemperature(@RequestBody chanel: Chanel): ResponseEntity<ArrayList<Float>>{
        log.info(chanel.toString())
        return ResponseEntity(chanel.calculateTemperature(), HttpStatus.OK)
    }

}