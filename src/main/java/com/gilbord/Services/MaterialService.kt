package com.gilbord.Services

import com.gilbord.Controllers.CalculateController
import com.gilbord.Models.DAO.Material
import com.gilbord.Models.DAO.Property
import com.gilbord.Models.DAO.Value
import com.gilbord.Repositories.MaterialRepository
import com.gilbord.Repositories.PropertyRepository
import com.gilbord.Repositories.ValueRepository
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.Serializable

@Service
class MaterialService {

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

    fun getAll(): List<MaterialData> {
        val result = ArrayList<MaterialData>()
        result.addAll(materialRepository.findAll().map { material ->
            val resultProperty = ArrayList<PropertyData>()
            resultProperty.addAll(valueRepository.getByMaterial(material).map { value ->
                PropertyData(value.property!!, value.value)
            })
            MaterialData(material, resultProperty)
        })
        return result
    }

    fun getOne(material_id: Long): Material = materialRepository.findOne(material_id)
    fun changeMaterial(material: Material, material_id: Long) {
        materialRepository.save(materialRepository.getOne(material_id).apply {
            this.materialClass = material.materialClass
            this.name = material.name
            this.type = material.type
        })
    }
    fun deleteMaterial(material_id: Long) = materialRepository.delete(material_id)


}

data class MaterialData(val material: Material, val propertiesData: List<PropertyData>) : Serializable

data class PropertyData(val property: Property, val value: Float) : Serializable