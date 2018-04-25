package com.gilbord.Models.DAO

import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
@Table(name = "PROPERTIES")
class Property(@Id
               @GeneratedValue(generator = "increment")
               @GenericGenerator(name = "increment", strategy = "increment")
               var id: Long = 0,
               @Column(name = "property_name", nullable = false) var name: String,
               @ManyToOne(cascade = [CascadeType.ALL]) var unit: Unit? = null)