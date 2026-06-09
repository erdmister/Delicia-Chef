package com.deliciachef.data.repository

import com.deliciachef.core.common.Resource
import com.deliciachef.data.remote.firebase.PlannedRecipeDto
import com.deliciachef.domain.repository.PlannedRecipeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PlannedRecipeRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : PlannedRecipeRepository {

    private val userId: String? get() = auth.currentUser?.uid

    private fun getCollection() = userId?.let { uid ->
        firestore.collection("users").document(uid).collection("planned_recipes")
    }

    override fun getPlannedRecipes(): Flow<Resource<List<PlannedRecipeDto>>> = callbackFlow {
        val collection = getCollection()
        if (collection == null) {
            trySend(Resource.Error("Usuario no autenticado"))
            close()
            return@callbackFlow
        }

        val subscription = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Resource.Error(error.localizedMessage ?: "Error desconocido"))
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val recipes = snapshot.documents.mapNotNull { it.toObject(PlannedRecipeDto::class.java) }
                trySend(Resource.Success(recipes))
            }
        }
        awaitClose { subscription.remove() }
    }

    override suspend fun savePlannedRecipe(recipe: PlannedRecipeDto): Resource<Unit> {
        return try {
            val uniqueDocumentId = "${recipe.id}_${recipe.dayOfWeek}"
            getCollection()?.document(uniqueDocumentId)?.set(recipe)?.await()
                ?: return Resource.Error("Usuario no autenticado")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al agendar")
        }
    }

    override suspend fun deletePlannedRecipe(recipe: PlannedRecipeDto): Resource<Unit> {
        return try {
            val uniqueDocumentId = "${recipe.id}_${recipe.dayOfWeek}"
            getCollection()?.document(uniqueDocumentId)?.delete()?.await()
                ?: return Resource.Error("Usuario no autenticado")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al eliminar")
        }
    }
}