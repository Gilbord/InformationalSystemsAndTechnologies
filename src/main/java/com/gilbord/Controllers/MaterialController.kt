package com.gilbord.Controllers

import com.gilbord.Models.DAO.Material
import com.gilbord.Models.DAO.Value
import com.gilbord.Services.MaterialData
import com.gilbord.Services.MaterialService
import com.gilbord.Services.ValueService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class MaterialController {

    @Autowired
    lateinit var materialService: MaterialService

    @Autowired
    lateinit var valueService: ValueService

    @CrossOrigin
    @RequestMapping(value = ["/material"],
            method = [RequestMethod.POST])
    public fun addMaterial(@RequestBody material: MaterialData): ResponseEntity<Any> {
        materialService.save(material)
        return ResponseEntity("Ok", HttpStatus.OK)
    }

    @CrossOrigin
    @RequestMapping(value = ["/value"],
            method = [RequestMethod.POST])
    public fun addtMaterial(@RequestBody values: List<Value>): ResponseEntity<Any> {
        valueService.save(values)
        return ResponseEntity("Ok", HttpStatus.OK)
    }

    @CrossOrigin
    @RequestMapping(value = ["/materials"],
            method = [RequestMethod.GET])
    public fun getOneMaterial() = ResponseEntity(materialService.getAll(), HttpStatus.OK)


    @CrossOrigin
    @RequestMapping(value = ["/material/{material_id}"],
            method = [RequestMethod.GET])
    public fun getAllMaterials(@PathVariable material_id: Long) =
            ResponseEntity(materialService.getOne(material_id), HttpStatus.OK)

    @CrossOrigin
    @RequestMapping(value = ["/material/{material_id}"],
            method = [RequestMethod.PUT])
    public fun changeMaterial(@RequestBody material: Material, @PathVariable material_id: Long): ResponseEntity<Any> {
        return ResponseEntity("Ok", HttpStatus.OK)
    }



}