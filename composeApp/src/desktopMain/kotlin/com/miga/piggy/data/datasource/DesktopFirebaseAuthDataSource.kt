package com.miga.piggy.data.datasource

import com.miga.piggy.auth.data.datasource.AuthDataSource
import com.miga.piggy.auth.domain.model.User
import com.miga.piggy.util.FirebaseDesktopConfig
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class FirebaseAuthRequest(
    val email: String,
    val password: String,
    val returnSecureToken: Boolean = true
)

@Serializable
data class FirebaseAuthResponse(
    val localId: String,
    val email: String,
    val displayName: String? = null,
    val idToken: String,
    val refreshToken: String? = null,
    val expiresIn: String? = null
)

@Serializable
data class FirebaseErrorResponse(
    val error: FirebaseError
)

@Serializable
data class FirebaseError(
    val code: Int,
    val message: String,
    val errors: List<FirebaseErrorDetail> = emptyList()
)

@Serializable
data class FirebaseErrorDetail(
    val message: String,
    val domain: String,
    val reason: String
)

@Serializable
data class ResetPasswordRequest(
    val email: String,
    val requestType: String = "PASSWORD_RESET"
)

@Serializable
data class SendEmailVerificationRequest(
    val requestType: String,
    val idToken: String
)

@Serializable
data class GetUserInfoRequest(
    val idToken: String
)

@Serializable
data class GetUserInfoResponse(
    val users: List<UserInfo>
)

@Serializable
data class UserInfo(
    val localId: String,
    val email: String,
    val displayName: String? = null,
    val emailVerified: Boolean = false,
    val photoUrl: String? = null
)

class DesktopFirebaseAuthDataSource : AuthDataSource {
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

    private val apiKey = FirebaseDesktopConfig.apiKey
    private val signInUrl =
        "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=$apiKey"
    private val signUpUrl = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=$apiKey"
    private val resetPasswordUrl =
        "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=$apiKey"
    private val updateProfileUrl =
        "https://identitytoolkit.googleapis.com/v1/accounts:update?key=$apiKey"
    private val sendEmailVerificationUrl =
        "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=$apiKey"
    private val getUserInfoUrl =
        "https://identitytoolkit.googleapis.com/v1/accounts:lookup?key=$apiKey"

    private var currentUser: User? = null
    private var currentToken: String? = null
    private var lastRegisteredEmail: String? = null
    private var lastRegisteredPassword: String? = null

    init {
        currentToken = loadTokenLocally()
        try {
            val firebaseUser = Firebase.auth.currentUser
            if (firebaseUser != null) {
                currentUser = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName,
                    photoUrl = firebaseUser.photoURL
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun login(email: String, password: String): User {
        return try {
            val response = client.post(signInUrl) {
                contentType(ContentType.Application.Json)
                setBody(FirebaseAuthRequest(email, password))
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val authResponse = response.body<FirebaseAuthResponse>()
                    currentToken = authResponse.idToken
                    currentUser = User(
                        id = authResponse.localId,
                        email = authResponse.email,
                        displayName = authResponse.displayName,
                        photoUrl = null
                    )
                    saveTokenLocally(authResponse.idToken)

                    try {
                        Firebase.auth.signInWithEmailAndPassword(email, password)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    currentUser!!
                }

                else -> {
                    try {
                        response.body<FirebaseErrorResponse>()
                    } catch (_: Exception) {
                        throw Exception("Authentication failed")
                    }
                    throw Exception("Authentication failed")
                }
            }
        } catch (_: Exception) {
            throw Exception("Login failed")
        }
    }

    override suspend fun register(email: String, password: String, displayName: String): User {
        return try {
            lastRegisteredEmail = email
            lastRegisteredPassword = password

            val response = client.post(signUpUrl) {
                contentType(ContentType.Application.Json)
                setBody(FirebaseAuthRequest(email, password))
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val authResponse = response.body<FirebaseAuthResponse>()

                    if (displayName.isNotBlank()) {
                        try {
                            updateDisplayName(authResponse.idToken, displayName)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    try {
                        val emailResponse = client.post(sendEmailVerificationUrl) {
                            contentType(ContentType.Application.Json)
                            setBody(
                                SendEmailVerificationRequest(
                                    requestType = "VERIFY_EMAIL",
                                    idToken = authResponse.idToken,
                                )
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    val user = User(
                        id = authResponse.localId,
                        email = authResponse.email,
                        displayName = displayName.takeIf { it.isNotBlank() },
                        photoUrl = null
                    )

                    user
                }

                else -> {
                    val errorResponse = try {
                        response.body<FirebaseErrorResponse>()
                    } catch (_: Exception) {
                        throw Exception("Registration failed")
                    }
                    throw Exception("Registration failed")
                }
            }
        } catch (_: Exception) {
            throw Exception("Registration failed")
        }
    }

    override suspend fun getCurrentUser(): User? {
        val firebaseUser = Firebase.auth.currentUser
        if (firebaseUser != null && currentUser == null) {
            currentUser = User(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = firebaseUser.displayName,
                photoUrl = firebaseUser.photoURL
            )
            return currentUser
        }

        return currentUser
    }

    override suspend fun logout() {
        try {
            Firebase.auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        currentUser = null
        currentToken = null
        clearTokenLocally()
    }

    override suspend fun resetPassword(email: String) {
        try {
            val response = client.post(resetPasswordUrl) {
                contentType(ContentType.Application.Json)
                setBody(ResetPasswordRequest(email))
            }

            if (response.status != HttpStatusCode.OK) {
                throw Exception("Password reset failed")
            }
        } catch (_: Exception) {
            throw Exception("Password reset failed")
        }
    }

    override suspend fun isEmailVerified(): Boolean {
        val email = lastRegisteredEmail
        val password = lastRegisteredPassword
        if (email != null && password != null) {
            return try {
                val response = client.post(signInUrl) {
                    contentType(ContentType.Application.Json)
                    setBody(FirebaseAuthRequest(email, password))
                }

                if (response.status == HttpStatusCode.OK) {
                    val authResponse = response.body<FirebaseAuthResponse>()
                    val userInfoResponse = client.post(getUserInfoUrl) {
                        contentType(ContentType.Application.Json)
                        setBody(GetUserInfoRequest(authResponse.idToken))
                    }
                    val userInfo = userInfoResponse.body<GetUserInfoResponse>().users.first()

                    if (userInfo.emailVerified) {
                        lastRegisteredEmail = null
                        lastRegisteredPassword = null

                        currentToken = authResponse.idToken
                        currentUser = User(
                            id = authResponse.localId,
                            email = authResponse.email,
                            displayName = authResponse.displayName,
                            photoUrl = null
                        )
                        saveTokenLocally(authResponse.idToken)

                        try {
                            Firebase.auth.signInWithEmailAndPassword(email, password)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        return true
                    }

                    false

                } else {
                    false
                }
            } catch (_: Exception) {
                false
            }
        }

        val token = currentToken
        if (token == null) {
            return false
        }

        return try {
            val userInfoResponse = client.post(getUserInfoUrl) {
                contentType(ContentType.Application.Json)
                setBody(GetUserInfoRequest(token))
            }
            val userInfo = userInfoResponse.body<GetUserInfoResponse>().users.first()
            userInfo.emailVerified
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun sendEmailVerification(): Boolean {
        val token = currentToken
        if (token == null) {
            return false
        }
        try {
            client.post(sendEmailVerificationUrl) {
                contentType(ContentType.Application.Json)
                setBody(
                    SendEmailVerificationRequest(
                        requestType = "VERIFY_EMAIL",
                        idToken = token,
                    )
                )
            }
            return true
        } catch (_: Exception) {
            return false
        }
    }

    private suspend fun updateDisplayName(idToken: String, displayName: String) {
        client.post(updateProfileUrl) {
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "idToken" to idToken,
                    "displayName" to displayName,
                    "returnSecureToken" to true
                )
            )
        }
    }

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
        } catch (_: Exception) {
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
}