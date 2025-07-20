data class User(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val isEmailVerified: Boolean = false,
    val profileImageBase64: String? = null // Campo para imagem de perfil personalizada
)