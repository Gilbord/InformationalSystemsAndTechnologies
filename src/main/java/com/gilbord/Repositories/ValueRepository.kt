package com.gilbord.Repositories

import com.gilbord.Models.DAO.Material
import com.gilbord.Models.DAO.Property
import com.gilbord.Models.DAO.Value
import org.springframework.data.repository.CrudRepository

interface ValueRepository: CrudRepository<Value, Long> {
    fun getByMaterialAndProperty(material: Material, property: Property): List<Value>
}