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
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

expect val platformModule: Module

val appModule = module {
    single<AuthDataSource> { FirebaseAuthDataSource() }

    single<AuthRepository> { AuthRepositoryImpl(get()) }

    single<LoginUseCase> { LoginUseCaseImpl(get()) }
    single<RegisterUseCase> { RegisterUseCaseImpl(get()) }
    single<GetCurrentUserUseCase> { GetCurrentUserUseCaseImpl(get()) }
    single<LogoutUseCase> { LogoutUseCaseImpl(get()) }
    single<EmailVerificationUseCase> { EmailVerificationUseCaseImpl(get()) }

    viewModelOf(::AuthViewModel)
}