package com.gilbord.Models

data class CalculatedModel(val temperaturePoints: ArrayList<TemperaturePoint>,
                           val viscosityPoints: ArrayList<ViscosityPoint>,
                           val performance: Float,
                           val calculatedTime: Long)

data class TemperaturePoint(val x: Double, val y: Double)

data class ViscosityPoint(val x: Double, val y: Double)