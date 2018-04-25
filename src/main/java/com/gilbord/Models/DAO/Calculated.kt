package com.gilbord.Models.DAO

import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
@Table(name = "CALCULATED")
class Calculated(@Id
                 @GeneratedValue(generator = "increment")
                 @GenericGenerator(name = "increment", strategy = "increment")
                 val id: Long,
                 @Column(name = "XARRAYTEMPERATURE") val xArrayTemperature: Array<Float>,
                 @Column(name = "YARRAYTEMPERATURE") val yArrayTemperature: Array<Float>,
                 @Column(name = "XARRAYVISCOSITY") val xArrayViscosity: Array<Float>,
                 @Column(name = "YARRAYVISCOSITY") val yArrayViscosity: Array<Float>)