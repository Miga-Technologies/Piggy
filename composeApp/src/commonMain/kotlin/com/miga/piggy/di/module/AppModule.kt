package com.miga.piggy.di.module

import com.miga.piggy.auth.data.datasource.AuthDataSource
import com.miga.piggy.auth.data.datasource.FirebaseAuthDataSource
import com.miga.piggy.auth.data.repository.AuthRepositoryImpl
import com.miga.piggy.auth.domain.repository.AuthRepository
import com.miga.piggy.auth.domain.usecase.EmailVerificationUseCase
import com.miga.piggy.auth.domain.usecase.EmailVerificationUseCaseImpl
import com.miga.piggy.auth.domain.usecase.GetCurrentUserUseCase
import com.miga.piggy.auth.domain.usecase.GetCurrentUserUseCaseImpl
import com.miga.piggy.auth.domain.usecase.LoginUseCase
import com.miga.piggy.auth.domain.usecase.LoginUseCaseImpl
import com.miga.piggy.auth.domain.usecase.LogoutUseCase
import com.miga.piggy.auth.domain.usecase.LogoutUseCaseImpl
import com.miga.piggy.auth.domain.usecase.RegisterUseCase
import com.miga.piggy.auth.domain.usecase.RegisterUseCaseImpl
import com.miga.piggy.auth.presentation.viewmodel.AuthViewModel
import com.miga.piggy.home.presentation.viewmodel.HomeViewModel
import com.miga.piggy.balance.presentation.viewmodel.EditBalanceViewModel
import com.miga.piggy.home.data.datasource.FinancialRemoteDataSource
import com.miga.piggy.home.data.datasource.FinancialRemoteDataSourceImpl
import com.miga.piggy.home.data.repository.FinancialRepositoryImpl
import com.miga.piggy.home.domain.repository.FinancialRepository
import com.miga.piggy.balance.domain.usecases.GetBalanceUseCase
import com.miga.piggy.balance.domain.usecases.UpdateBalanceUseCase
import com.miga.piggy.category.presentation.viewmodel.CategoryViewModel
import com.miga.piggy.home.domain.usecase.AddCategoryUseCase
import com.miga.piggy.home.domain.usecase.DeleteCategoryUseCase
import com.miga.piggy.home.domain.usecase.GetCategoriesUseCase
import com.miga.piggy.home.domain.usecase.UpdateCategoryUseCase
import com.miga.piggy.home.domain.usecases.category.GetCategoriesByTypeUseCase
import com.miga.piggy.home.domain.usecases.financial.GetFinancialSummaryUseCase
import com.miga.piggy.transaction.domain.usecases.AddTransactionUseCase
import com.miga.piggy.transaction.domain.usecases.DeleteTransactionUseCase
import com.miga.piggy.transaction.domain.usecases.GetTransactionsByTypeUseCase
import com.miga.piggy.transaction.domain.usecases.GetTransactionsUseCase
import com.miga.piggy.transaction.domain.usecases.UpdateTransactionUseCase
import com.miga.piggy.transaction.presentation.viewmodel.AddTransactionViewModel
import com.miga.piggy.transaction.presentation.viewmodel.TransactionListViewModel
import com.miga.piggy.reports.presentation.viewmodel.ReportsViewModel
import com.miga.piggy.reports.utils.PdfExporter
import com.miga.piggy.reports.utils.PdfExporterImpl
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

expect val platformModule: Module

val appModule = module {
    single<FirebaseFirestore> { Firebase.firestore }

    single<AuthDataSource> { FirebaseAuthDataSource() }

    single<AuthRepository> { AuthRepositoryImpl(get()) }

    single<LoginUseCase> { LoginUseCaseImpl(get()) }
    single<RegisterUseCase> { RegisterUseCaseImpl(get()) }
    single<GetCurrentUserUseCase> { GetCurrentUserUseCaseImpl(get()) }
    single<LogoutUseCase> { LogoutUseCaseImpl(get()) }
    single<EmailVerificationUseCase> { EmailVerificationUseCaseImpl(get()) }

    single<FinancialRemoteDataSource> { FinancialRemoteDataSourceImpl(get()) }

    single<FinancialRepository> { FinancialRepositoryImpl(get()) }

    single<PdfExporter> { PdfExporterImpl() }

    factory { GetBalanceUseCase(get()) }
    factory { UpdateBalanceUseCase(get()) }
    factory { GetBalanceUseCase(get()) }
    factory { UpdateBalanceUseCase(get()) }
    factory { GetCategoriesUseCase(get()) }
    factory { AddCategoryUseCase(get()) }
    factory { DeleteCategoryUseCase(get()) }
    factory { UpdateCategoryUseCase(get()) }
    factory { GetCategoriesByTypeUseCase(get()) }
    factory { GetTransactionsUseCase(get()) }
    factory { AddTransactionUseCase(get()) }
    factory { DeleteTransactionUseCase(get()) }
    factory { UpdateTransactionUseCase(get()) }
    factory { GetTransactionsByTypeUseCase(get()) }
    factory { GetFinancialSummaryUseCase(get(), get()) }

    viewModelOf(::AuthViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::EditBalanceViewModel)
    viewModelOf(::AddTransactionViewModel)
    viewModelOf(::CategoryViewModel)
    viewModelOf(::TransactionListViewModel)
    viewModelOf(::ReportsViewModel)
}