package com.miga.piggy.di.module

import com.miga.piggy.auth.data.datasource.AuthDataSource
import com.miga.piggy.auth.data.repository.AuthRepositoryImpl
import com.miga.piggy.auth.domain.repository.AuthRepository
import com.miga.piggy.data.datasource.DesktopFirebaseAuthDataSource
import com.miga.piggy.reports.utils.PdfExporter
import com.miga.piggy.reports.utils.PdfExporterImpl
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<AuthDataSource> { DesktopFirebaseAuthDataSource() }
        single<AuthRepository> { AuthRepositoryImpl(get()) }
        single<PdfExporter> { PdfExporterImpl() }
    }