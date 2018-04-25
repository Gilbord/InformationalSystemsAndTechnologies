package com.gilbord.Repositories

import com.gilbord.Models.DAO.Material
import org.springframework.data.repository.CrudRepository

interface MaterialRepository: CrudRepository<Material, Long>