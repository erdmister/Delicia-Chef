package com.deliciachef.domain.repository

import com.deliciachef.core.common.Resource
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun login(email: String, pass: String): Resource<FirebaseUser>
    suspend fun register(email: String, pass: String): Resource<FirebaseUser>
    suspend fun sendPasswordReset(email: String): Resource<Unit>
    fun getCurrentUser(): FirebaseUser?
}