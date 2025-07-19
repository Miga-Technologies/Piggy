package com.miga.piggy.di.module

import com.miga.piggy.reports.utils.PdfExporter
import com.miga.piggy.reports.utils.PdfExporterImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<PdfExporter> { PdfExporterImpl(androidContext()) }
    }
