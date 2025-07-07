package com.miga.piggy.reports.utils

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.miga.piggy.transaction.domain.entity.Transaction
import com.miga.piggy.utils.formatters.formatDouble
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

actual class PdfExporterImpl : PdfExporter {

    actual override suspend fun exportReportToPdf(
        monthlyIncome: Double,
        monthlyExpenses: Double,
        expensesByCategory: Map<String, Double>,
        incomeByCategory: Map<String, Double>,
        recentTransactions: List<Transaction>
    ): PdfExportResult = withContext(Dispatchers.IO) {

        try {
            val userHome = System.getProperty("user.home")
            val downloadsDir = File(userHome, "Downloads")

            val fileName = "relatorio_financeiro_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
            val file = File(downloadsDir, fileName)

            val pdfWriter = PdfWriter(FileOutputStream(file))
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)

            document.add(
                Paragraph("Relatório Financeiro")
                    .setFontSize(24f)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20f)
            )

            document.add(
                Paragraph("Data: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())}")
                    .setFontSize(12f)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(20f)
            )

            document.add(
                Paragraph("Resumo Financeiro")
                    .setFontSize(18f)
                    .setMarginBottom(10f)
            )

            val summaryTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f)))
                .setWidth(UnitValue.createPercentValue(100f))
                .setMarginBottom(20f)

            summaryTable.addHeaderCell(
                Cell().add(Paragraph("Tipo").setFontSize(14f))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            )
            summaryTable.addHeaderCell(
                Cell().add(Paragraph("Valor").setFontSize(14f))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            )

            summaryTable.addCell(Cell().add(Paragraph("Receitas")))
            summaryTable.addCell(Cell().add(Paragraph("R$ ${formatDouble(monthlyIncome)}")))

            summaryTable.addCell(Cell().add(Paragraph("Gastos")))
            summaryTable.addCell(Cell().add(Paragraph("R$ ${formatDouble(monthlyExpenses)}")))

            summaryTable.addCell(Cell().add(Paragraph("Saldo")))
            summaryTable.addCell(
                Cell().add(Paragraph("R$ ${formatDouble(monthlyIncome - monthlyExpenses)}"))
                    .setBackgroundColor(if (monthlyIncome - monthlyExpenses >= 0) ColorConstants.GREEN else ColorConstants.LIGHT_GRAY)
            )

            document.add(summaryTable)

            document.close()

            PdfExportResult(
                success = true,
                filePath = file.absolutePath,
                error = null
            )

        } catch (e: Exception) {
            PdfExportResult(
                success = false,
                filePath = null,
                error = "Erro ao gerar PDF: ${e.message}"
            )
        }
    }
}