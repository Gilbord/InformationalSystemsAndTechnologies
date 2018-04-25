package com.gilbord.Repositories

import com.gilbord.Models.DAO.Property
import org.springframework.data.repository.CrudRepository

interface PropertyRepository: CrudRepository<Property, Long> {
    fun getPropertyByName(name: String): Property?
}