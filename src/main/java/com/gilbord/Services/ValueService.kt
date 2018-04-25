package com.gilbord.Services

import com.gilbord.Models.DAO.Value
import com.gilbord.Repositories.ValueRepository
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ValueService {

    val log = Logger.getLogger(ValueService::class.java.name)

    @Autowired
    lateinit var valueRepository: ValueRepository

    fun save(value: List<Value>){
        valueRepository.save(value)
    }


}