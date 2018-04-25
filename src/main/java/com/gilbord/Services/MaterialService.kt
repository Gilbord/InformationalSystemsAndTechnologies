package com.gilbord.Services

import com.gilbord.Controllers.CalculateController
import com.gilbord.Models.DAO.*
import com.gilbord.Models.DAO.Unit
import com.gilbord.Repositories.MaterialRepository
import com.gilbord.Repositories.PropertyRepository
import com.gilbord.Repositories.ValueRepository
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MaterialService{

    val log = Logger.getLogger(CalculateController::class.java.name)

    @Autowired
    lateinit var materialRepository: MaterialRepository

    @Autowired
    lateinit var propertyRepository: PropertyRepository

    @Autowired
    lateinit var valueRepository: ValueRepository

    fun save(materialData: MaterialData) {
        materialData.propertiesData.forEach { pd ->
            val property = propertyRepository.getPropertyByName(pd.property.name) ?: pd.property
            valueRepository.save(Value(value = pd.value, property = property, material = materialData.material))
        }
    }

    fun getAll() = materialRepository.findAll()

    fun getOne(material_id: Long): Material = materialRepository.findOne(material_id)


}

data class MaterialData(val material: Material, val propertiesData: List<PropertyData>)

data class PropertyData(val property: Property, val value: Float)