package com.gilbord.Models

import kotlin.math.exp
import kotlin.math.ln

data class Chanel(val width: Float,
                  val height: Float,
                  val length: Float,
                  val coverSpeed: Float,
                  val coverTemperature: Int,
                  val step: Float) {

    companion object {

        //Геометрические параметры канала
        const val w = 0.17f      //Ширина
        const val h = 0.005f    //Глубина
        const val l = 7.0f      //Длина

        //Параметры свойств материала
        const val ro = 890      //Плотность
        const val c = 2300      //Удельная теплоемкость
        const val t0 = 175      //Температура плавления

        //Режимные параметры процесса
        const val vu = 1.2f     //Скорость крышки
        const val tu = 150      //Температура крышки

        //Эмпирические коэффициенты математической модели
        const val mu0 = 1550f    //Коэффициент консистенции материала при температуре приведения
        const val b = 0.015f     //Температурный коэффициент вязкости материала
        const val tr = 180      //Температура приведения
        const val n = 0.4f     //Индекс течения материала
        const val alphaU = 2000f //Коэффициент теплоотдачи от крышки канала к материалу

    }

    val fch: (Float, Float) -> Float = { w, h ->
        0.125f * Math.pow((h / w).toDouble(),
                2.0).toFloat() - 0.625f * (h / w) + 1
    }

    private val performance: (Float, Float, Float) -> Float =
            { w, h, vu -> ro * q(w, h, vu) * 3600 }

    private val q: (Float, Float, Float) -> Float = { w, h, vu -> (w * h * vu) / 2 * fch(w, h) }

    private val t: (Float, Float, Float, Float, Float, Float) -> Float =
            { w, h, l, vu, tu, z -> tr + 1 / b * ln(kappa(tu, vu, z, w, h)) }

    private val kappa: (Float, Float, Float, Float, Float) -> Float =
            { tu, vu, z, w, h -> kappa1(vu, tu, w, h) * kappa2(tu, vu, z, w, h) + kappa3(tu, z, w, h, vu) }

    private val kappa1: (Float, Float, Float, Float) -> Float =
            { vu, tu, w, h -> (b * qGamma(vu, h, w) * w * alphaU) / (b * qAlpha(tu, w)) }

    private val kappa2: (Float, Float, Float, Float, Float) -> Float =
            { tu, vu, z, w, h -> 1 - Math.exp((-z * b * qAlpha(tu, w) / (ro * c * q(w, h, vu))).toDouble()).toFloat() }

    private val kappa3: (Float, Float, Float, Float, Float) -> Float =
            { tu, z, w, h, vu ->
                Math.exp(b * (t0 - tr - ((z * qAlpha(tu, w) / (ro * c * q(w, h, vu))))).toDouble()).toFloat()
            }

    private val qGamma: (Float, Float, Float) -> Float =
            { vu, h, w -> w * h * mu0 * Math.pow(gamma(vu, h).toDouble(), (n + 1).toDouble()).toFloat() }

    private val qAlpha: (Float, Float) -> Float =
            { tu, w -> w * alphaU * (1 / b - tu + tr) }

    private val gamma: (Float, Float) -> Float = { vu, h -> vu / h }

    private val mu: (Float, Float, Float, Float, Float, Float) -> Float =
            { w, h, l, vu, tu, z -> mu0 * exp(-b * (t(w, h, l, vu, tu, z) - tr)) }

    private val nu: (Float, Float, Float, Float, Float, Float) -> Float =
            { w, h, l, vu, tu, z ->
                mu(w, h, l, vu, tu, z) * Math.pow(gamma(vu, h).toDouble(), (n - 1).toDouble()).toFloat() }

    public fun performanceFunction(): Float {
        return performance(this.width, this.height, this.coverSpeed)
    }

    public fun calculateTemperature(): ArrayList<Point> {
        val result = ArrayList<Point>()
        var currentValue = 0f
        while (currentValue <= this.length) {
            result.add(Point(currentValue, t(this.width,
                    this.height,
                    this.length,
                    this.coverSpeed,
                    this.coverTemperature.toFloat(),
                    currentValue)))
            currentValue += this.step
        }
        return result
    }

    public fun calculateViscosity(): ArrayList<Point> {
        val result = ArrayList<Point>()
        var currentValue = 0f
        while (currentValue <= this.length) {
            result.add(Point(currentValue, nu(this.width,
                    this.height,
                    this.length,
                    this.coverSpeed,
                    this.coverTemperature.toFloat(),
                    currentValue)))
            currentValue += this.step
        }
        return result
    }

    public fun validate(): Boolean {
        return !(this.width <= 0 || this.height <= 0 || this.coverSpeed <= 0 || this.coverTemperature <= 0 || this.step <= 0)
    }

}