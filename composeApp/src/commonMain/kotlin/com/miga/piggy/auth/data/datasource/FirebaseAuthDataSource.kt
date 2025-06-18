package com.miga.piggy.auth.data.datasource

import com.miga.piggy.auth.domain.model.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth

class FirebaseAuthDataSource : AuthDataSource {
    private val auth: FirebaseAuth = Firebase.auth

    override suspend fun login(email: String, password: String): User {
        val result = auth.signInWithEmailAndPassword(email, password)
        val firebaseUser = result.user ?: throw Exception("Usuário não encontrado")
        return firebaseUser.toUser()
    }

    override suspend fun register(email: String, password: String, displayName: String): User {
        val result = auth.createUserWithEmailAndPassword(email, password)
        val firebaseUser = result.user ?: throw Exception("Erro ao criar usuário")
        firebaseUser.updateProfile()
        return firebaseUser.toUser()
    }

    override suspend fun getCurrentUser(): User? {
        return auth.currentUser?.toUser()
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
    }
}

private fun FirebaseUser.toUser(): User {
    return User(
        id = uid,
        email = email ?: "",
        displayName = displayName,
        photoUrl = photoURL
    )
}