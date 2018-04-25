package com.gilbord.Models

data class Model(val channel: Channel, val materialId: Long, val step: Float)

data class Channel(val width: Float,
                   val height: Float,
                   val length: Float,
                   val coverSpeed: Float,
                   val coverTemperature: Int)