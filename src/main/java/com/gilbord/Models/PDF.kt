package com.gilbord.Models

import com.gilbord.Models.DAO.Material
import com.gilbord.Models.DAO.Value
import com.itextpdf.text.*
import com.itextpdf.text.pdf.*
import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.QuickChart
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset

class PDF(val calculatedModel: CalculatedModel, val values: List<Value>) {
    
    companion object {
        const val ENCODING = "UTF-8"
    }

    private val baseFont = BaseFont.createFont("c:/windows/fonts/times.ttf","cp1251",BaseFont.EMBEDDED)

    fun generate(): ByteArray {
        val document = Document()
        val resultBytes = ByteArrayOutputStream()
        PdfWriter.getInstance(document, resultBytes)
        document.open()
        val title = Paragraph(Phrase("Отчет о расчете модели", Font(baseFont, 20f)))
        title.alignment = Element.ALIGN_CENTER
        document.add(title)
        document.addEmptyLine(1)
        document.generateTableWithProperties()
        document.addEmptyLine(1)
        document.generateTableWithResult()
        document.newPage()
        document.addPlot(this.calculatedModel.temperaturePoints, "Рис. 1 - Зависимость температуры от координаты по длине", "Температура, С")
        document.addEmptyLine(2)
        document.addPlot(this.calculatedModel.viscosityPoints, "Рис. 2 - Зависимость вязкости от координаты по длине", "Вязкость, Па * с")
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

    private fun Document.addPlot(points: List<Point>, descr: String, unit: String){
        val xData = ArrayList<Double>()
        val yData = ArrayList<Double>()
        points.forEach { point ->
            xData.add(point.x)
            yData.add(point.y)
        }
        val plot = getPlot(xData.toDoubleArray(),
                yData.toDoubleArray(),
                "",
                "Координата по длине, м",
                unit,
                "${unit.split(", ")[0]} от длины")
        val scaler = ((this.pageSize.width - this.leftMargin()
                - this.rightMargin() - 0) / plot.width) * 90
        plot.scalePercent(scaler)
        this.add(plot)
        this.addDescriptionOfElement(descr)

    }

    private fun Document.generateTableWithResult(){
        val table = PdfPTable(2)
        var cell = PdfPCell(Phrase("Properties", Font(baseFont, 16f)))
        cell.horizontalAlignment = Element.ALIGN_CENTER
        table.addCell(cell)
        cell = PdfPCell(Phrase("Values", Font(baseFont, 16f)))
        cell.horizontalAlignment = Element.ALIGN_CENTER
        table.addCell(cell)
        cell = PdfPCell(Phrase("Время расчета", Font(baseFont, 14f)))
        cell.horizontalAlignment = Element.ALIGN_CENTER
        table.addCell(cell)
        cell = PdfPCell(Phrase(calculatedModel.calculatedTime.toString(), Font(baseFont, 14f)))
        cell.horizontalAlignment = Element.ALIGN_CENTER
        table.addCell(cell)
        cell = PdfPCell(Phrase("Производительность", Font(baseFont, 14f)))
        cell.horizontalAlignment = Element.ALIGN_CENTER
        table.addCell(cell)
        cell = PdfPCell(Phrase(calculatedModel.performance.toString(), Font(baseFont, 14f)))
        cell.horizontalAlignment = Element.ALIGN_CENTER
        table.addCell(cell)
        this.add(table)
        this.addDescriptionOfElement("Табл. 2 - Результат расчета")
    }

    private fun Document.generateTableWithProperties() {
        val table = PdfPTable(2)
        var cell = PdfPCell(Phrase("Properties", Font(baseFont, 16f)))
        cell.horizontalAlignment = Element.ALIGN_CENTER
        table.addCell(cell)
        cell = PdfPCell(Phrase("Values", Font(baseFont, 16f)))
        cell.horizontalAlignment = Element.ALIGN_CENTER
        table.addCell(cell) 
        this@PDF.values.forEach { value ->
            cell = PdfPCell(Phrase("${value.property!!.name}, ${value.property!!.unit!!.name}", Font(baseFont, 14f)))
            cell.horizontalAlignment = Element.ALIGN_LEFT
            table.addCell(cell)
            cell = PdfPCell(Phrase(value.value.toString(), Font(baseFont, 16f)))
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.verticalAlignment = Element.ALIGN_CENTER
            table.addCell(cell)
        }
        this.add(table)
        this.addDescriptionOfElement("Табл. 1 - Исходные данные")

    }

    private fun Document.addDescriptionOfElement(descr: String){
        val description = Paragraph(Phrase(descr, Font(baseFont, 12f)))
        description.alignment = Element.ALIGN_CENTER
        this.add(description)
    }

    private fun Document.addEmptyLine(num: Int){
        for(i in 0..num){
            this.add(Paragraph(" "))
        }
    }

}