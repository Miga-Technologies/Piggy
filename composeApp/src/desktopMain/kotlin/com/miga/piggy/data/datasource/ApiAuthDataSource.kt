package com.miga.piggy.data.datasource

import com.miga.piggy.auth.data.datasource.AuthDataSource
import com.miga.piggy.auth.domain.model.AuthResponse
import com.miga.piggy.auth.domain.model.ErrorResponse
import com.miga.piggy.auth.domain.model.LoginRequest
import com.miga.piggy.auth.domain.model.RegisterRequest
import com.miga.piggy.auth.domain.model.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiAuthDataSource : AuthDataSource {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    private val baseUrl = "https://piggyintegration.onrender.com"
    private var currentToken: String? = null
    private var currentUser: User? = null

    init {
        // Carregar token salvo ao inicializar
        currentToken = loadTokenLocally()
    }

    override suspend fun login(email: String, password: String): User {
        return try {
            val response = client.post("$baseUrl/auth/login/") {
                contentType(Json)
                setBody(LoginRequest(email, password))
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val authResponse = response.body<AuthResponse>()
                    currentToken = authResponse.idToken
                    currentUser = authResponse.toUser()
                    saveTokenLocally(authResponse.idToken)
                    currentUser!!
                }
                else -> {
                    val errorResponse = try {
                        response.body<ErrorResponse>()
                    } catch (_: Exception) {
                        ErrorResponse("Login failed", "Erro desconhecido")
                    }
                    throw Exception(errorResponse.message ?: errorResponse.error)
                }
            }
        } catch (e: Exception) {
            throw Exception("Erro ao fazer login: ${e.message}")
        }
    }

    override suspend fun register(email: String, password: String, displayName: String): User {
        return try {
            val response = client.post("$baseUrl/auth/register/") {
                contentType(Json)
                setBody(RegisterRequest(email, password, displayName))
            }

            when (response.status) {
                HttpStatusCode.Created, HttpStatusCode.OK -> {
                    val authResponse = response.body<AuthResponse>()
                    currentToken = authResponse.idToken
                    currentUser = authResponse.toUser()
                    saveTokenLocally(authResponse.idToken)
                    currentUser!!
                }
                else -> {
                    val errorResponse = try {
                        response.body<ErrorResponse>()
                    } catch (_: Exception) {
                        ErrorResponse("Registration failed", "Erro desconhecido")
                    }
                    throw Exception(errorResponse.message ?: errorResponse.error)
                }
            }
        } catch (e: Exception) {
            throw Exception("Erro ao registrar: ${e.message}")
        }
    }

    override suspend fun getCurrentUser(): User? {
        if (currentUser != null) {
            return currentUser
        }

        // Se tem token salvo mas não tem usuário em memória,
        // você pode implementar uma chamada para validar/buscar o usuário
        val savedToken = loadTokenLocally()
        if (savedToken != null) {
            currentToken = savedToken
            // Opcional: fazer chamada GET /auth/me ou similar
            // currentUser = validateAndGetUser(savedToken)
        }

        return currentUser
    }

    override suspend fun logout() {
        // Opcional: chamar endpoint de logout na API
        try {
            currentToken?.let { token ->
                client.post("$baseUrl/auth/logout/") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }
            }
        } catch (_: Exception) {
            // Ignora erro do logout da API, limpa local mesmo assim
        }

        currentToken = null
        currentUser = null
        clearTokenLocally()
    }

    override suspend fun resetPassword(email: String) {
        try {
            val response = client.post("$baseUrl/auth/reset-password/") {
                contentType(Json)
                setBody(mapOf("email" to email))
            }

            if (!response.status.isSuccess()) {
                throw Exception("Erro ao solicitar reset de senha")
            }
        } catch (e: Exception) {
            throw Exception("Erro ao solicitar reset de senha: ${e.message}")
        }
    }

    override suspend fun isEmailVerified(): Boolean {
        return currentUser?.let {
            // Você pode implementar uma verificação específica aqui
            // ou fazer uma chamada para a API para verificar o status
            true // Por enquanto retorna true, ajuste conforme necessário
        } ?: false
    }

    override suspend fun sendEmailVerification(): Boolean {
        return try {
            currentToken?.let { token ->
                val response = client.post("$baseUrl/auth/resend-verification/") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }
                response.status.isSuccess()
            } ?: false
        } catch (_: Exception) {
            false
        }
    }

    // Métodos auxiliares para persistência local
    private fun saveTokenLocally(token: String?) {
        token?.let {
            try {
                val prefs = java.util.prefs.Preferences.userNodeForPackage(this::class.java)
                prefs.put("piggy_auth_token", it)
                prefs.flush()
            } catch (e: Exception) {
                println("Erro ao salvar token: ${e.message}")
            }
        }
    }

    private fun loadTokenLocally(): String? {
        return try {
            val prefs = java.util.prefs.Preferences.userNodeForPackage(this::class.java)
            prefs.get("piggy_auth_token", null)
        } catch (e: Exception) {
            println("Erro ao carregar token: ${e.message}")
            null
        }
    }

    private fun clearTokenLocally() {
        try {
            val prefs = java.util.prefs.Preferences.userNodeForPackage(this::class.java)
            prefs.remove("piggy_auth_token")
            prefs.flush()
        } catch (e: Exception) {
            println("Erro ao limpar token: ${e.message}")
        }
    }

    private fun AuthResponse.toUser(): User {
        return User(
            id = localId,
            email = email,
            displayName = displayName,
            photoUrl = null
        )
    }
}