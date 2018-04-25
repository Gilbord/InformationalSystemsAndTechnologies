package com.gilbord.Models.DAO

import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
@Table(name = "PROPERTIES_TYPES")
class PropertiesType(@Id
                     @GeneratedValue(generator = "increment")
                     @GenericGenerator(name = "increment", strategy = "increment")
                     val id: Long,
                     @Column(name = "name") val name: String)