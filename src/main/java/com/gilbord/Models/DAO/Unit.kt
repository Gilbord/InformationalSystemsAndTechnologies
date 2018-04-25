package com.gilbord.Models.DAO

import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
@Table(name = "UNITS")
data class Unit(@Id
                @GeneratedValue(generator = "increment")
                @GenericGenerator(name = "increment", strategy = "increment")
                val id: Long = 0,
                @Column(name = "name") var name: String,
                @OneToMany(mappedBy = "unit") var properties: List<Property>? = null)