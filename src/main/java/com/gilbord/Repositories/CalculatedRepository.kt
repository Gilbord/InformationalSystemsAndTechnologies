package com.gilbord.Repositories

import com.gilbord.Models.DAO.Calculated
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CalculatedRepository: CrudRepository<Calculated, Long>