package com.example.gruuv.repository

import android.util.Log
import com.example.gruuv.model.Achievement
import com.example.gruuv.utils.getCurrentDateKey
import com.google.firebase.firestore.FirebaseFirestore

class AchievementRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun getAchievements(userId: String, onComplete: (List<Achievement>) -> Unit) {
        val achievementCollection = firestore.collection("users").document(userId).collection("achievements")

        achievementCollection.get().addOnSuccessListener { snapshot ->
            val achievements = snapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Achievement::class.java) // Ensure it maps to the updated model
                } catch (e: Exception) {
                    Log.e("AchievementRepository", "Error deserializing achievement: ${document.id}", e)
                    null
                }
            }
            onComplete(achievements)
        }.addOnFailureListener { exception ->
            Log.e("AchievementRepository", "Error fetching achievements", exception)
            onComplete(emptyList())
        }
    }

    fun addAchievement(achievement: Achievement, userId: String, callback: (Boolean) -> Unit) {
        val userAchievementsCollection = firestore.collection("users")
            .document(userId)
            .collection("achievements")

        userAchievementsCollection.document(achievement.id)
            .set(achievement)
            .addOnSuccessListener {
                Log.d("AchievementRepository", "Achievement added successfully for user $userId: ${achievement.id}")
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.e("AchievementRepository", "Error adding achievement for user $userId", exception)
                callback(false)
            }
    }

    fun updateAchievementStatus(id: String, completed: Boolean, userId: String, callback: (Boolean) -> Unit) {
        val userAchievementsCollection = firestore.collection("users")
            .document(userId)
            .collection("achievements")

        userAchievementsCollection.document(id)
            .update("completed", completed)
            .addOnSuccessListener {
                Log.d("AchievementRepository", "Updated 'completed' for achievement $id to $completed for user $userId")
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.e("AchievementRepository", "Error updating 'completed' for achievement $id for user $userId", exception)
                callback(false)
            }
    }

    fun updateAchievement(id: String, title: String, description: String, userId: String, callback: (Boolean) -> Unit) {
        val userAchievementsCollection = firestore.collection("users")
            .document(userId)
            .collection("achievements")

        val updatedAchievement = mapOf(
            "title" to title,
            "description" to description
        )
        userAchievementsCollection.document(id).update(updatedAchievement)
            .addOnSuccessListener {
                Log.d("AchievementRepository", "Achievement $id updated successfully for user $userId")
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.e("AchievementRepository", "Error updating achievement $id for user $userId", exception)
                callback(false)
            }
    }

    fun deleteAchievement(id: String, userId: String, callback: (Boolean) -> Unit) {
        val userAchievementsCollection = firestore.collection("users")
            .document(userId)
            .collection("achievements")

        userAchievementsCollection.document(id).delete()
            .addOnSuccessListener {
                Log.d("AchievementRepository", "Achievement $id deleted successfully for user $userId")
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.e("AchievementRepository", "Error deleting achievement $id for user $userId", exception)
                callback(false)
            }
    }


    fun updateAchievementEffort(id: String, effort: Int, userId: String, onComplete: (Boolean) -> Unit) {
        val achievementRef = firestore.collection("users").document(userId).collection("achievements").document(id)
        val currentDateKey = getCurrentDateKey()

        achievementRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Get the current effortHistory as a map
                val effortHistory = (document["effortHistory"] as? Map<String, Int>)?.toMutableMap() ?: mutableMapOf()

                // Add or update today's effort
                effortHistory[currentDateKey] = effort

                // Update Firestore
                achievementRef.update(
                    mapOf(
                        "effort" to effort,
                        "effortHistory" to effortHistory
                    )
                ).addOnSuccessListener { onComplete(true) }
                    .addOnFailureListener { onComplete(false) }
            } else {
                onComplete(false)
            }
        }.addOnFailureListener { onComplete(false) }
    }
}
