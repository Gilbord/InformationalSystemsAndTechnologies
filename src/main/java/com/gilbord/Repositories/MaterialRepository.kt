package com.gilbord.Repositories

import com.gilbord.Models.DAO.Material
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository

interface MaterialRepository: JpaRepository<Material, Long>