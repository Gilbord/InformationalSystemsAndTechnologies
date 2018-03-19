package com.gilbord.Controllers

import com.gilbord.Models.Chanel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.lang.Math.pow
import kotlin.math.ln

@RestController
class CalculateController {

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

        fun performanceFunction(chanel: Chanel): Float {
            return 0.125f * Math.pow((chanel.height / chanel.width).toDouble(),
                    2.0).toFloat() - 0.625f * (chanel.height / chanel.width) + 1
        }

        val fch: (Float, Float) -> Float = { w, h -> 0.125f * Math.pow((h / w).toDouble(),
                2.0).toFloat() - 0.625f * (h / w) + 1 }

        val q: (Float, Float, Float) -> Float = { w, h, vu -> (w * h * vu) / 2 * fch(w, h) }

        //FIXME: Tr or Tg??
        val t: (Float, Float, Float, Float, Float, Float) -> Float =
                { w, h, l, vu, tu, z -> tr + 1 / b * ln(kappa(tu, vu, z, w, h)) }

        val kappa: (Float, Float, Float, Float, Float) -> Float =
                { tu, vu, z, w, h -> kappa1(vu, tu, w, h) * kappa2(tu, vu, z, w, h) + kappa3(tu, z, w, h, vu) }

        //FIXME: WV(???????????)
        val kappa1: (Float, Float, Float, Float) -> Float =
                { vu, tu, w, h -> (b * qGamma(vu, h) + w * alphaU) / (b * qAlpha(tu)) }

        //FIXME: q(?????????)
        val kappa2: (Float, Float, Float, Float, Float) -> Float =
                { tu, vu, z, w, h -> 1 - Math.exp((-z * b * qAlpha(tu) / ro * c * q(w, h, vu)).toDouble()).toFloat() }

        //FIXME: tz, z, q
        val kappa3: (Float, Float, Float, Float, Float) -> Float =
                { tu, z, w, h, vu ->
                    Math.exp(b * (t0 - tz - ((z * qAlpha(tu) / (ro * c * q(w, h, vu))))).toDouble()).toFloat()
                }

        //FIXME: change mu0(????????)
        val qGamma: (Float, Float) -> Float =
                { vu, h -> w * h * mu0 * Math.pow(gamma(vu, h).toDouble(), k.toDouble()).toFloat() }

        //FIXME: alphaU(?????????????), Tr or Tg(?????????????)
        val qAlpha: (Float) -> Float =
                { tu -> w * alphaU * (1 / b * tu + tr) }

        val gamma: (Float, Float) -> Float = { vu, h -> vu / h }

    }

    @CrossOrigin
    @RequestMapping(value = ["/performance"],
            method = [RequestMethod.POST])
    public fun calculatePerformance(@RequestBody chanel: Chanel): ResponseEntity<Float> {
        return ResponseEntity(performanceFunction(chanel), HttpStatus.OK)
    }

}