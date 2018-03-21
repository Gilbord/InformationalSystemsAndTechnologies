package com.gilbord.Models

import kotlin.math.ln

data class Chanel(val width: Float,
                  val height: Float,
                  val length: Float,
                  val coverSpeed: Float,
                  val coverTemperature: Int,
                  val step: Int) {

    companion object {

        //Геометрические параметры канала
        const val w = 0.2f      //Ширина
        const val h = 0.005f    //Глубина
        const val l = 7.0f      //Длина

        //Параметры свойств материала
        const val ro = 920      //Плотность
        const val c = 2300      //Удельная теплоемкость
        const val t0 = 120      //Температура плавления

        //Режимные параметры процесса
        const val vu = 1.2f     //Скорость крышки
        const val tu = 150      //Температура крышки

        //Эмпирические коэффициенты математической модели
        const val mu = 50000    //Коэффициент консистенции материала при температуре приведения
        const val b = 0.03f     //Температурный коэффициент вязкости материала
        const val tr = 120      //Температура приведения
        const val n = 0.35f     //Индекс течения материала
        const val alphaU = 250f //Коэффициент теплоотдачи от крышки канала к материалу
        const val tz = 0f       //Темература приведения
        const val mu0 = 0f
        const val k = 0         //Степень k = n + 1

    }

    val fch: (Float, Float) -> Float = { w, h ->
        0.125f * Math.pow((h / w).toDouble(),
                2.0).toFloat() - 0.625f * (h / w) + 1
    }

    private val q: (Float, Float, Float) -> Float = { w, h, vu -> (w * h * vu) / 2 * fch(w, h) }

    private val t: (Float, Float, Float, Float, Float, Float) -> Float =
            { w, h, l, vu, tu, z -> tr + 1 / b * ln(kappa(tu, vu, z, w, h)) }

    private val kappa: (Float, Float, Float, Float, Float) -> Float =
            { tu, vu, z, w, h -> kappa1(vu, tu, w, h) * kappa2(tu, vu, z, w, h) + kappa3(tu, z, w, h, vu) }

    private val kappa1: (Float, Float, Float, Float) -> Float =
            { vu, tu, w, h -> (b * qGamma(vu, h) + w * alphaU) / (b * qAlpha(tu)) }

    private val kappa2: (Float, Float, Float, Float, Float) -> Float =
            { tu, vu, z, w, h -> 1 - Math.exp((-z * b * qAlpha(tu) / ro * c * q(w, h, vu)).toDouble()).toFloat() }

    private val kappa3: (Float, Float, Float, Float, Float) -> Float =
            { tu, z, w, h, vu ->
                Math.exp(b * (t0 - tz - ((z * qAlpha(tu) / (ro * c * q(w, h, vu))))).toDouble()).toFloat()
            }

    private val qGamma: (Float, Float) -> Float =
            { vu, h -> w * h * mu0 * Math.pow(gamma(vu, h).toDouble(), k.toDouble()).toFloat() }

    private val qAlpha: (Float) -> Float =
            { tu -> w * alphaU * (1 / b * tu + tr) }

    private val gamma: (Float, Float) -> Float = { vu, h -> vu / h }

    public fun performanceFunction(): Float {
        return 0.125f * Math.pow((this.height / this.width).toDouble(),
                2.0).toFloat() - 0.625f * (this.height / this.width) + 1
    }

    public fun calculateTemperature(): ArrayList<Float> {
        val result = ArrayList<Float>()
        var currentValue = 0
        while (currentValue < this.length) {
            result.add(t(this.width,
                    this.height,
                    this.length,
                    this.coverSpeed,
                    this.coverTemperature.toFloat(),
                    currentValue.toFloat()))
            currentValue += this.step
        }
        return result
    }

}