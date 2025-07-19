package com.miga.piggy.reports.utils

import com.miga.piggy.transaction.domain.entity.Transaction
import com.miga.piggy.transaction.domain.entity.TransactionType
import com.miga.piggy.utils.formatters.formatDouble
import kotlinx.cinterop.*
import platform.CoreGraphics.*
import platform.Foundation.*
import platform.UIKit.*
import platform.darwin.*
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

actual class PdfExporterImpl : PdfExporter {

    @OptIn(ExperimentalForeignApi::class)
    actual override suspend fun exportReportToPdf(
        monthlyIncome: Double,
        monthlyExpenses: Double,
        expensesByCategory: Map<String, Double>,
        incomeByCategory: Map<String, Double>,
        recentTransactions: List<Transaction>
    ): PdfExportResult = withContext(Dispatchers.Default) {

        try {
            val documentsPath = NSSearchPathForDirectoriesInDomains(
                NSDocumentDirectory,
                NSUserDomainMask,
                true
            ).first() as String

            val fileName = "relatorio_financeiro_${NSDate().timeIntervalSince1970.toLong()}.pdf"
            val filePath = "$documentsPath/$fileName"

            // Criar contexto PDF
            val pdfInfo = mapOf<Any?, Any?>(
                kCGPDFContextTitle to "Relatório Financeiro",
                kCGPDFContextAuthor to "Piggy App"
            )
            
            val pageRect = CGRectMake(0.0, 0.0, 612.0, 792.0)
            UIGraphicsBeginPDFContextToFile(filePath, pageRect, pdfInfo)
            UIGraphicsBeginPDFPage()

            var yPosition = 50.0
            val leftMargin = 50.0
            val rightMargin = 562.0

            // Título principal
            drawText("Relatório Financeiro", leftMargin, yPosition, 24.0, true, UIColor.blackColor)
            yPosition += 40.0

            // Data
            val dateFormatter = NSDateFormatter()
            dateFormatter.dateFormat = "dd/MM/yyyy"
            val currentDate = dateFormatter.stringFromDate(NSDate())
            drawText("Data: $currentDate", rightMargin - 100.0, yPosition, 12.0, false, UIColor.grayColor)
            yPosition += 40.0

            // Resumo Financeiro
            drawText("Resumo Financeiro", leftMargin, yPosition, 18.0, true, UIColor.blackColor)
            yPosition += 30.0

            // Criar tabela de resumo
            drawRect(leftMargin, yPosition, rightMargin - leftMargin, 90.0, UIColor.lightGrayColor.colorWithAlphaComponent(0.1))
            
            yPosition += 10.0
            drawText("Receitas", leftMargin + 10.0, yPosition, 14.0, false, UIColor.blackColor)
            drawText("R$ ${formatDouble(monthlyIncome)}", rightMargin - 150.0, yPosition, 14.0, false, UIColor.greenColor)
            yPosition += 25.0

            drawText("Gastos", leftMargin + 10.0, yPosition, 14.0, false, UIColor.blackColor)
            drawText("R$ ${formatDouble(monthlyExpenses)}", rightMargin - 150.0, yPosition, 14.0, false, UIColor.redColor)
            yPosition += 25.0

            val balance = monthlyIncome - monthlyExpenses
            drawText("Saldo", leftMargin + 10.0, yPosition, 14.0, true, UIColor.blackColor)
            drawText("R$ ${formatDouble(balance)}", rightMargin - 150.0, yPosition, 14.0, true, 
                if (balance >= 0) UIColor.greenColor else UIColor.redColor)
            yPosition += 40.0

            // Gastos por categoria
            if (expensesByCategory.isNotEmpty()) {
                drawText("Gastos por Categoria", leftMargin, yPosition, 18.0, true, UIColor.blackColor)
                yPosition += 30.0

                expensesByCategory.entries.sortedByDescending { it.value }.forEach { (category, amount) ->
                    drawText(category, leftMargin + 20.0, yPosition, 12.0, false, UIColor.blackColor)
                    drawText("R$ ${formatDouble(amount)}", rightMargin - 150.0, yPosition, 12.0, false, UIColor.redColor)
                    yPosition += 20.0
                    
                    // Verificar se precisa de nova página
                    if (yPosition > 700.0) {
                        UIGraphicsBeginPDFPage()
                        yPosition = 50.0
                    }
                }
                yPosition += 20.0
            }

            // Receitas por categoria
            if (incomeByCategory.isNotEmpty()) {
                drawText("Receitas por Categoria", leftMargin, yPosition, 18.0, true, UIColor.blackColor)
                yPosition += 30.0

                incomeByCategory.entries.sortedByDescending { it.value }.forEach { (category, amount) ->
                    drawText(category, leftMargin + 20.0, yPosition, 12.0, false, UIColor.blackColor)
                    drawText("R$ ${formatDouble(amount)}", rightMargin - 150.0, yPosition, 12.0, false, UIColor.greenColor)
                    yPosition += 20.0
                    
                    // Verificar se precisa de nova página
                    if (yPosition > 700.0) {
                        UIGraphicsBeginPDFPage()
                        yPosition = 50.0
                    }
                }
                yPosition += 20.0
            }

            // Transações recentes
            if (recentTransactions.isNotEmpty() && yPosition < 600.0) {
                drawText("Transações Recentes", leftMargin, yPosition, 18.0, true, UIColor.blackColor)
                yPosition += 30.0

                recentTransactions.take(10).forEach { transaction ->
                    // Verificar se precisa de nova página
                    if (yPosition > 700.0) {
                        UIGraphicsBeginPDFPage()
                        yPosition = 50.0
                    }
                    
                    val dateFormatter = NSDateFormatter()
                    dateFormatter.dateFormat = "dd/MM/yyyy"
                    val transactionDate =
                        NSDate.dateWithTimeIntervalSince1970(transaction.date / 1000.0)
                    val dateString = dateFormatter.stringFromDate(transactionDate)
                    
                    drawText(transaction.description.ifEmpty { transaction.category }, leftMargin + 20.0, yPosition, 10.0, false, UIColor.blackColor)
                    drawText(dateString, leftMargin + 250.0, yPosition, 10.0, false, UIColor.grayColor)
                    drawText(
                        "R$ ${formatDouble(transaction.amount)}", 
                        rightMargin - 100.0, 
                        yPosition, 
                        10.0, 
                        false, 
                        if (transaction.type == TransactionType.INCOME) UIColor.greenColor else UIColor.redColor
                    )
                    yPosition += 18.0
                }
            }

            UIGraphicsEndPDFContext()

            // Compartilhar o PDF usando o menu nativo do iOS
            sharePdf(filePath)

            PdfExportResult(
                success = true,
                filePath = filePath,
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

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun drawText(text: String, x: Double, y: Double, fontSize: Double, bold: Boolean = false, color: UIColor) {
        val font = if (bold) {
            UIFont.boldSystemFontOfSize(fontSize)
        } else {
            UIFont.systemFontOfSize(fontSize)
        }
        
        val paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.setAlignment(NSTextAlignmentLeft)

        val attributes = mapOf<Any?, Any?>(
            NSFontAttributeName to font,
            NSForegroundColorAttributeName to color,
            NSParagraphStyleAttributeName to paragraphStyle
        )

        (text as NSString).drawAtPoint(
            CGPointMake(x, y),
            withAttributes = attributes
        )
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun drawRect(x: Double, y: Double, width: Double, height: Double, color: UIColor) {
        val context = UIGraphicsGetCurrentContext()
        CGContextSetFillColorWithColor(context, color.CGColor)
        CGContextFillRect(context, CGRectMake(x, y, width, height))
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun sharePdf(filePath: String) {
        try {
            dispatch_async(dispatch_get_main_queue()) {
                try {
                    val fileUrl = NSURL.fileURLWithPath(filePath)

                    val activityViewController = UIActivityViewController(
                        activityItems = listOf(fileUrl),
                        applicationActivities = null
                    )

                    // Obter o rootViewController de forma mais simples
                    val application = UIApplication.sharedApplication
                    val keyWindow = application.keyWindow
                    var rootViewController = keyWindow?.rootViewController

                    // Se rootViewController é uma UINavigationController ou UITabBarController, 
                    // obter o controller visível
                    if (rootViewController is UINavigationController) {
                        rootViewController = rootViewController.visibleViewController
                    } else if (rootViewController is UITabBarController) {
                        rootViewController = rootViewController.selectedViewController
                    }

                    if (rootViewController != null) {
                        // Configurar popover para iPad
                        val popover = activityViewController.popoverPresentationController
                        if (popover != null && UIDevice.currentDevice.userInterfaceIdiom == UIUserInterfaceIdiomPad) {
                            // Usar a view do controller como source
                            popover.sourceView = rootViewController.view
                            val viewFrame = rootViewController.view.frame
                            popover.sourceRect = CGRectMake(
                                viewFrame.useContents { size.width } / 2.0,
                                viewFrame.useContents { size.height } / 2.0,
                                0.0, 0.0
                            )
                        }

                        rootViewController.presentViewController(
                            activityViewController,
                            animated = true,
                            completion = null
                        )
                    }
                } catch (e: Exception) {
                    // Log do erro, mas não crash o app
                    println("Erro ao compartilhar PDF: ${e.message}")
                }
            }
        } catch (e: Exception) {
            // Log do erro, mas não crash o app
            println("Erro ao preparar compartilhamento do PDF: ${e.message}")
        }
    }
}