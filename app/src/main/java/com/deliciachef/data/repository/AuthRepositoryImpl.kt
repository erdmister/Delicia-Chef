package com.deliciachef.data.repository

import com.deliciachef.core.common.Resource
import com.deliciachef.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    override suspend fun login(email: String, pass: String): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            result.user?.let { Resource.Success(it) } ?: Resource.Error("Usuario no encontrado")
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al iniciar sesión")
        }
    }

    override suspend fun register(email: String, pass: String): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            result.user?.let { Resource.Success(it) } ?: Resource.Error("Error al crear cuenta")
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error en el registro")
        }
    }

    override suspend fun sendPasswordReset(email: String): Resource<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al enviar correo")
        }
    }
}