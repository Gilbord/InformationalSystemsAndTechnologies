package com.gilbord.Models

import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfWriter
import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.QuickChart
import java.io.ByteArrayOutputStream

class PDF(val calculatedModel: CalculatedModel) {

    fun generate(): ByteArray {
        val document = Document()
        val resultBytes = ByteArrayOutputStream()
        PdfWriter.getInstance(document, resultBytes)
        document.open()
        val font = FontFactory.getFont(FontFactory.COURIER, 16f, BaseColor.BLACK)
        val chunk = Chunk("Отчет о расчете моедли", font)
        document.add(chunk)
        val xData = ArrayList<Double>()
        val yData = ArrayList<Double>()
        calculatedModel.temperaturePoints.forEach { point ->
            xData.add(point.x)
            yData.add(point.y)
        }
        var plot = getPlot(xData.toDoubleArray(),
                yData.toDoubleArray(),
                "Температура",
                "Координата по длине, м",
                "Температура C",
                "Температура от длины")
        var scaler = ((document.pageSize.width - document.leftMargin()
                - document.rightMargin() - 0) / plot.width) * 90
        plot.scalePercent(scaler)
        document.add(plot)
        xData.clear()
        yData.clear()
        calculatedModel.viscosityPoints.forEach{ point ->
            xData.add(point.x)
            yData.add(point.y)
        }
        plot = getPlot(xData.toDoubleArray(),
                yData.toDoubleArray(),
                "Вязкость",
                "Координата по длине, м",
                "Вязкость, Па * с",
                "Вязкость от длины от длины")
        scaler = ((document.pageSize.width - document.leftMargin()
                - document.rightMargin() - 0) / plot.width) * 90
        plot.scalePercent(scaler)
        document.add(plot)
        document.close()
        return resultBytes.toByteArray()
    }

    fun getPlot(xData: DoubleArray,
                yData: DoubleArray,
                chartTitle: String,
                xSign: String,
                ySign: String,
                seriesName: String): Image {
        val chart = QuickChart.getChart(chartTitle, xSign, ySign, null, xData, yData)
        return Image.getInstance(BitmapEncoder.getBitmapBytes(chart, BitmapEncoder.BitmapFormat.PNG))
    }

}