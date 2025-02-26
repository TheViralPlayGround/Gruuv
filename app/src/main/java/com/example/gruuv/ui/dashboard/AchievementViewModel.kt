package com.example.gruuv.ui.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.gruuv.model.Achievement
import com.example.gruuv.repository.AchievementRepository
import com.example.gruuv.utils.getCurrentDateKey
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AchievementViewModel(private val achievementRepository: AchievementRepository) : ViewModel() {

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements

    init {
        loadAchievements()
    }

    fun loadAchievements() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            achievementRepository.getAchievements(currentUserId) { newAchievements ->
                Log.d("AchievementViewModel", "Updated state: $newAchievements")
                _achievements.value = newAchievements
            }
        } else {
            Log.e("AchievementViewModel", "No logged-in user found. Cannot load achievements.")
        }
    }

    fun addAchievement(id: String, title: String, description: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            val newAchievement = Achievement(
                id = id,
                title = title,
                description = description,
                completed = false,
                effort = 0,
                date = System.currentTimeMillis()
            )

            achievementRepository.addAchievement(newAchievement, currentUserId) { success ->
                if (success) loadAchievements() // Reload achievements if addition is successful
            }
        } else {
            Log.e("AchievementViewModel", "No logged-in user found. Cannot add achievement.")
        }
    }

    fun updateAchievement(id: String, title: String, description: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            achievementRepository.updateAchievement(id, title, description, currentUserId) { success ->
                if (success) loadAchievements()
            }
        } else {
            Log.e("AchievementViewModel", "No logged-in user found. Cannot update achievement.")
        }
    }

    fun deleteAchievement(id: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            achievementRepository.deleteAchievement(id, currentUserId) { success ->
                if (success) loadAchievements()
            }
        } else {
            Log.e("AchievementViewModel", "No logged-in user found. Cannot delete achievement.")
        }
    }

    fun getAchievementById(id: String): Achievement? {
        Log.d("AchievementViewModel", "Current achievements: ${_achievements.value}")
        val achievement = _achievements.value.find { it.id == id }
        Log.d("AchievementViewModel", "Fetched achievement: $achievement")
        return achievement
    }

    fun updateAchievementStatus(id: String, completed: Boolean) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            Log.d("AchievementViewModel", "Updating achievement $id to completed=$completed")
            achievementRepository.updateAchievementStatus(id, completed, currentUserId) { success ->
                if (success) {
                    Log.d("AchievementViewModel", "Successfully updated $id in Firestore")
                    loadAchievements() // Reload the achievements
                } else {
                    Log.e("AchievementViewModel", "Failed to update achievement $id")
                }
            }
        } else {
            Log.e("AchievementViewModel", "No logged-in user found. Cannot update achievement status.")
        }
    }

    fun updateAchievementEffort(id: String, effort: Int) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            achievementRepository.updateAchievementEffort(id, effort, currentUserId) { success ->
                if (success) {
                    _achievements.value = _achievements.value.map { achievement ->
                        if (achievement.id == id) {
                            val updatedEffortHistory = achievement.effortHistory.toMutableMap()
                            updatedEffortHistory[getCurrentDateKey()] = effort
                            achievement.copy(effort = effort, effortHistory = updatedEffortHistory)
                        } else {
                            achievement
                        }
                    }
                } else {
                    Log.e("AchievementViewModel", "Failed to update effort for achievement $id")
                }
            }
        } else {
            Log.e("AchievementViewModel", "No logged-in user found. Cannot update achievement effort.")
        }
    }

    fun getAverageEffortPerDay(): Map<String, Float> {
        val aggregatedEfforts = mutableMapOf<String, MutableList<Int>>()

        // Aggregate efforts across all achievements by date
        achievements.value.forEach { achievement ->
            achievement.effortHistory.forEach { (date, effort) ->
                aggregatedEfforts.getOrPut(date) { mutableListOf() }.add(effort)
            }
        }

        // Calculate the average effort per day
        return aggregatedEfforts.mapValues { (_, efforts) ->
            efforts.average().toFloat()
        }
    }

    fun getWeeklyEffortTrend(effortHistory: Map<String, Int>): List<Pair<String, Int>> {
        // Convert the map to a list of sorted entries, then take the last 7
        return effortHistory.toSortedMap().entries.toList().takeLast(7).map { Pair(it.key, it.value) }
    }

    fun getMonthlyEffortTrend(effortHistory: Map<String, Int>): List<Pair<String, Int>> {
        // Convert the map to a list of sorted entries, then take the last 30
        return effortHistory.toSortedMap().entries.toList().takeLast(30).map { Pair(it.key, it.value) }
    }

}
