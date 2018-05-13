package com.gilbord.Models

data class CalculatedModel(val temperaturePoints: ArrayList<Point>,
                           val viscosityPoints: ArrayList<Point>,
                           val performance: Float,
                           val calculatedTime: Long)

data class Point(val x: Double, val y: Double)
