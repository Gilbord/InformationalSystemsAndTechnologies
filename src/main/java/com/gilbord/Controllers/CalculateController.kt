package com.gilbord.Controllers

import com.gilbord.Models.Model
import com.gilbord.Models.PDF
import com.gilbord.Services.CalculateService
import com.gilbord.Services.ValueService
import org.apache.log4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType


@RestController
class CalculateController {

    val log = Logger.getLogger(CalculateController::class.java.name)

    @Autowired
    lateinit var calculateService: CalculateService

    @Autowired
    lateinit var valueService: ValueService

    @CrossOrigin
    @RequestMapping(value = ["/pdf"],
            method = [RequestMethod.POST])
    public fun getPDF(@RequestBody model: Model): ResponseEntity<Any>{
        log.info("pdf")
        CalculateService.calculatedModel?.let{
            val values = this.valueService.getValuesByMaterialId(model.materialId)
            val pdf = PDF(it, values)
            val headers = HttpHeaders()
            headers.contentType = MediaType.parseMediaType("application/pdf")
            val filename = "output.pdf"
            headers.setContentDispositionFormData(filename, filename)
            headers.cacheControl = "must-revalidate, post-check=0, pre-check=0"
            return ResponseEntity(pdf.generate(), headers, HttpStatus.OK)
        }
        return ResponseEntity("Model didn't calculated for report", HttpStatus.BAD_REQUEST)
    }

    @CrossOrigin
    @RequestMapping(value = ["/calculate"],
            method = [RequestMethod.POST])
    public fun calculateModel(@RequestBody model: Model): ResponseEntity<Any>{
        log.info(model)
        return if (calculateService.validate(model)) ResponseEntity(calculateService.calculate(model), HttpStatus.OK)
        else ResponseEntity("Data doesn't valid", HttpStatus.BAD_REQUEST)
    }

}