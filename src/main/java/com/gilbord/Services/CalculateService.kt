package com.gilbord.Services

import com.gilbord.Models.*
import com.gilbord.Repositories.MaterialRepository
import com.gilbord.Repositories.PropertyRepository
import com.gilbord.Repositories.ValueRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.ArrayList
import kotlin.math.exp
import kotlin.math.ln

@Service
class CalculateService {

    @Autowired
    lateinit var valueRepository: ValueRepository

    @Autowired
    lateinit var propertyRepository: PropertyRepository

    @Autowired
    lateinit var materialRepository: MaterialRepository

    private val neededAccuracy: (Float) -> Int = { step -> step.toString().split(".")[1].length }

    private fun Float.round(places: Int): Double {
        val scale = Math.pow(10.0, places.toDouble())
        return Math.round(this * scale) / scale
    }

    companion object {
        var calculatedModel: CalculatedModel? = null
    }

     private val ro: (Long) -> Float = {materialId -> getValue(materialId,
             "Плотность")}
     private val c : (Long) -> Float = {materialId -> getValue(materialId,
             "Удельная теплоемкость")}
     private val t0: (Long) -> Float = {materialId -> getValue(materialId,
             "Температура плавления")}
     private val mu0: (Long) -> Float = {materialId -> getValue(materialId,
             "Коэффициент консистенции материала при температуре приведения")}
     private val b : (Long) -> Float = {materialId -> getValue(materialId,
             "Температурный коэффициент вязкости материала")}
     private val tr: (Long) -> Float = {materialId -> getValue(materialId,
             "Температура приведения")}
     private val n : (Long) -> Float = {materialId -> getValue(materialId,
             "Индекс течения материала")}
     private val alphaU: (Long) -> Float = {materialId -> getValue(materialId,
             "Коэффициент теплоотдачи от крышки канала к материалу")}

    private val getValue: (Long, String) -> Float = { materialId, propertyName ->
        val property = propertyRepository.getPropertyByName(propertyName)!!
        val material = materialRepository.findOne(materialId)
        valueRepository.getByMaterialAndProperty(material, property)[0].value
    }

    private val fch: (Float, Float) -> Float = { w, h ->
        0.125f * Math.pow((h / w).toDouble(),
                2.0).toFloat() - 0.625f * (h / w) + 1
    }

    private val performance: (Float, Float, Float, Long) -> Float =
            { w, h, vu, materialId -> ro(materialId) * q(w, h, vu) * 3600 }

    private val q: (Float, Float, Float) -> Float = { w, h, vu -> (w * h * vu) / 2 * fch(w, h) }

    private val t: (Float, Float, Float, Float, Float, Float, Long) -> Float =
            { w, h, l, vu, tu, z, materialId -> tr(materialId) + 1 / b(materialId) * ln(kappa(tu, vu, z, w, h, materialId)) }

    private val kappa: (Float, Float, Float, Float, Float, Long) -> Float =
            { tu, vu, z, w, h, materialId -> kappa1(vu, tu, w, h, materialId) * kappa2(tu, vu, z, w, h, materialId) + kappa3(tu, z, w, h, vu, materialId) }

    private val kappa1: (Float, Float, Float, Float, Long) -> Float =
            { vu, tu, w, h, materialId -> (b(materialId) * qGamma(vu, h, w, materialId) * w * alphaU(materialId)) / (b(materialId) * qAlpha(tu, w, materialId)) }

    private val kappa2: (Float, Float, Float, Float, Float, Long) -> Float =
            { tu, vu, z, w, h, materialId -> 1 - Math.exp((-z * b(materialId) * qAlpha(tu, w, materialId) / (ro(materialId) * c(materialId) * q(w, h, vu))).toDouble()).toFloat() }

    private val kappa3: (Float, Float, Float, Float, Float, Long) -> Float =
            { tu, z, w, h, vu, materialId ->
                Math.exp(b(materialId) * (t0(materialId) - tr(materialId) - ((z * qAlpha(tu, w, materialId) / (ro(materialId) * c(materialId) * q(w, h, vu))))).toDouble()).toFloat()
            }

    private val qGamma: (Float, Float, Float, Long) -> Float =
            { vu, h, w, materialId -> w * h * mu0(materialId) * Math.pow(gamma(vu, h).toDouble(), (n(materialId) + 1).toDouble()).toFloat() }

    private val qAlpha: (Float, Float, Long) -> Float =
            { tu, w, materialId -> w * alphaU(materialId) * (1 / b(materialId) - tu + tr(materialId)) }

    private val gamma: (Float, Float) -> Float = { vu, h -> vu / h }

    private val mu: (Float, Float, Float, Float, Float, Float, Long) -> Float =
            { w, h, l, vu, tu, z, materialId -> mu0(materialId) * exp(-b(materialId) * (t(w, h, l, vu, tu, z, materialId) - tr(materialId))) }

    private val nu: (Float, Float, Float, Float, Float, Float, Long) -> Float =
            { w, h, l, vu, tu, z, materialId ->
                mu(w, h, l, vu, tu, z, materialId) * Math.pow(gamma(vu, h).toDouble(), (n(materialId) - 1).toDouble()).toFloat()
            }

    public fun performanceFunction(model: Model): Float {
        return performance(model.channel.width, model.channel.height, model.channel.coverSpeed, model.materialId)
    }

    public fun calculateTemperature(model: Model): ArrayList<TemperaturePoint> {
        val result = ArrayList<TemperaturePoint>()
        var currentValue = 0f
        while (currentValue <= model.channel.length) {
            result.add(TemperaturePoint(currentValue.round(neededAccuracy(model.step)),
                    t(model.channel.width,
                            model.channel.height,
                            model.channel.length,
                            model.channel.coverSpeed,
                            model.channel.coverTemperature.toFloat(),
                            currentValue, model.materialId).round(neededAccuracy(model.step))))
            currentValue += model.step
        }
        return result
    }

    public fun calculateViscosity(model: Model): ArrayList<ViscosityPoint> {
        val result = ArrayList<ViscosityPoint>()
        var currentValue = 0f
        while (currentValue <= model.channel.length) {
            result.add(ViscosityPoint(currentValue.round(neededAccuracy(model.step)), nu(model.channel.width,
                    model.channel.height,
                    model.channel.length,
                    model.channel.coverSpeed,
                    model.channel.coverTemperature.toFloat(),
                    currentValue, model.materialId).round(neededAccuracy(model.step))))
            currentValue += model.step
        }
        return result
    }

    public fun calculate(model: Model): CalculatedModel {
        val start = System.currentTimeMillis()
        val temperature = calculateTemperature(model)
        val viscosity = calculateViscosity(model)
        val performance = performanceFunction(model).round(neededAccuracy(model.step)).toFloat()
        val finish = System.currentTimeMillis()
        val result = CalculatedModel(temperature, viscosity, performance, finish - start)
        calculatedModel = result
        return result
    }

    public fun validate(model: Model): Boolean {
        return !(model.channel.width <= 0 ||
                model.channel.height <= 0 ||
                model.channel.coverSpeed <= 0 ||
                model.channel.coverTemperature <= 0 ||
                model.step <= 0)
    }

}