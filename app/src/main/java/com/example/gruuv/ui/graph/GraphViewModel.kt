package com.example.gruuv.ui.graph

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class GraphViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _labels = MutableStateFlow(emptyList<String>())
    val labels: StateFlow<List<String>> = _labels

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _chartData = MutableStateFlow(emptyList<Entry>())
    val chartData: StateFlow<List<Entry>> = _chartData

    private val _timeFilter = MutableStateFlow("Day")
    val timeFilter: StateFlow<String> = _timeFilter

    init {
        loadChartData("Day")
    }

    fun setTimeFilter(filter: String) {
        _timeFilter.value = filter
        loadChartData(filter)
    }

     fun loadChartData(filter: String) {
        if (userId.isEmpty()) return
        _loading.value = true

        val historyCollection = firestore.collection("users")
            .document(userId)
            .collection("history")

        historyCollection.get().addOnSuccessListener { snapshot ->
            val filteredData = snapshot.documents.mapIndexed { index, document ->
                val achievements = document.get("achievements") as? List<Map<String, Any>>
                val completedCount = achievements?.count { it["completed"] == true } ?: 0
                Entry(index.toFloat(), completedCount.toFloat())
            }.reversed()

            _chartData.value = filteredData
            _loading.value = false
            _labels.value = generateLabels(filter, _chartData.value.size)
        }.addOnFailureListener { exception ->
            _loading.value = false
            Log.e("GraphViewModel", "Error loading chart data", exception)
        }
    }

    private fun generateLabels(filter: String, dataSize: Int): List<String> {
        val currentDate = Calendar.getInstance()
        return when (filter) {
            "Day" -> listOf(SimpleDateFormat("MMM dd", Locale.getDefault()).format(currentDate.time))
            "Week" -> (0 until dataSize).map {
                currentDate.add(Calendar.DAY_OF_YEAR, -1) // Go backwards one day at a time
                SimpleDateFormat("EEE", Locale.getDefault()).format(currentDate.time) // e.g., "Mon"
            }.reversed() // Ensure the labels are ordered from past to present
            "Month" -> (0 until dataSize).map {
                currentDate.add(Calendar.DAY_OF_YEAR, -1) // Go backwards one day at a time
                SimpleDateFormat("MMM dd", Locale.getDefault()).format(currentDate.time) // e.g., "Dec 01"
            }.reversed() // Ensure labels are ordered correctly
            else -> emptyList()
        }
    }
}
