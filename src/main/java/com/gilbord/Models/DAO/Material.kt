package com.gilbord.Models.DAO

import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
@Table(name = "MATERIALS")
class Material(@Id
               @GeneratedValue(generator = "increment")
               @GenericGenerator(name = "increment", strategy = "increment")
               val id: Long = 0,
               @Column(name = "material_type", nullable = false) var type: String,
               @ManyToOne(cascade = [CascadeType.ALL]) var materialClass: MaterialClass,
               @Column(name = "material_name", nullable = false) var name: String)