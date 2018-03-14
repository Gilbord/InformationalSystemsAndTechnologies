package com.gilbord.Controllers

import com.gilbord.Models.Chanel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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
        const val au = 250      //Коэффициент теплоотдачи от крышки канала к материалу

        fun performanceFunction(chanel: Chanel): Float {
            return 0.125f * Math.pow((chanel.height / chanel.width).toDouble(),
                    2.0).toFloat() - 0.625f * (chanel.height / chanel.width) + 1
        }

        //FIXME: Tr or Tg??
        val t: (Float, Float) -> Float =
                { tu, vu -> tr + 1 / b * ln(kappa(tu, vu)) }

        val kappa: (Float, Float) -> Float =
                { tu, vu -> kappa1(vu, 1.0f, tu) * kappa2(tu, 1.0f) + kappa3(tu, 1.0f) }

        //FIXME: WV(???????????)
        val kappa1: (Float, Float, Float) -> Float =
                { vu, alphaU, tu -> (b * qGamma(vu, 1.0f) + 1/*wv*/) / (b * qAlpha(tu, alphaU)) }

        //FIXME: q(?????????)
        val kappa2: (Float, Float) -> Float =
                { tu, alphaU -> 1 - Math.exp((-2 * b * qAlpha(tu, alphaU) / ro * c * 1/*q*/).toDouble()).toFloat() }

        //FIXME: tz, z, q
        val kappa3: (Float, Float) -> Float =
                { tu, alphaU ->
                    Math.exp((b * (t0 - 1/*tz*/ - ((1/*z*/ * qAlpha(tu, alphaU)) / (ro * c * 1/*q*/)))).toDouble()).toFloat()
                }

        //FIXME: change mu0(????????)
        val qGamma: (Float, Float) -> Float =
                { vu, mu0 -> w * h * mu0 * pow(gamma(vu, h), n+1) }

        //FIXME: alphaU(?????????????), Tr or Tg(?????????????)
        val qAlpha: (Float, Float) -> Float =
                { tu, alphaU -> w * alphaU * (1 / b * tu + tr) }

        val gamma: (Float, Float) -> Float = { vu, h -> vu / h }

    }

    @CrossOrigin
    @RequestMapping(value = ["/performance"],
            method = [RequestMethod.POST])
    public fun calculatePerformance(@RequestBody chanel: Chanel): ResponseEntity<Float> {
        return ResponseEntity(performanceFunction(chanel), HttpStatus.OK)
    }

}