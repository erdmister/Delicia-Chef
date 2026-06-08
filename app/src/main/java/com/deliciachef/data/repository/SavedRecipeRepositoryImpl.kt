package com.deliciachef.data.repository

import com.deliciachef.core.common.Resource
import com.deliciachef.data.remote.firebase.SavedRecipeDto
import com.deliciachef.domain.repository.SavedRecipeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SavedRecipeRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : SavedRecipeRepository {

    // Obtenemos el ID del usuario actualmente logueado
    private val userId: String?
        get() = auth.currentUser?.uid

    private fun getCollection() = userId?.let { uid ->
        firestore.collection("users").document(uid).collection("saved_recipes")
    }

    override fun getSavedRecipes(): Flow<Resource<List<SavedRecipeDto>>> = callbackFlow {
        val collection = getCollection()
        if (collection == null) {
            trySend(Resource.Error("Usuario no autenticado"))
            close()
            return@callbackFlow
        }

        // Acrualizacion en tiempo real de las recetas guardadas
        val subscription = collection.orderBy("savedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.localizedMessage ?: "Error desconocido"))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // convertir los documentos de Firebase a SavedRecipeDto
                    val recipes = snapshot.documents.mapNotNull { it.toObject(SavedRecipeDto::class.java) }
                    trySend(Resource.Success(recipes))
                }
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun saveRecipe(recipe: SavedRecipeDto): Resource<Unit> {
        return try {
            // Usamos el ID de la receta como nombre del documento para evitar duplicados
            getCollection()?.document(recipe.id.toString())?.set(recipe)?.await()
                ?: return Resource.Error("Usuario no autenticado")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al guardar la receta")
        }
    }

    override suspend fun deleteRecipe(recipeId: Int): Resource<Unit> {
        return try {
            getCollection()?.document(recipeId.toString())?.delete()?.await()
                ?: return Resource.Error("Usuario no autenticado")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al eliminar la receta")
        }
    }

    override suspend fun isRecipeSaved(recipeId: Int): Boolean {
        return try {
            val document = getCollection()?.document(recipeId.toString())?.get()?.await()
            document?.exists() ?: false
        } catch (e: Exception) {
            false
        }
    }
}