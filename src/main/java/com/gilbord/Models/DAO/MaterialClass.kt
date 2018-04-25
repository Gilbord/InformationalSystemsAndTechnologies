package com.gilbord.Models.DAO

import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
@Table(name = "MATERIAL_CLASSES")
data class MaterialClass(@Id
                    @GeneratedValue(generator = "increment")
                    @GenericGenerator(name = "increment", strategy = "increment")
                    val id: Long = 0,
                    @Column(name = "description") val description: String)