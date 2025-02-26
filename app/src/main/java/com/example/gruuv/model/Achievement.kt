package com.example.gruuv.model

data class Achievement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val completed: Boolean = false,
    val effort: Int = 0,
    val date: Long = 0L,
    val effortHistory: Map<String, Int> = emptyMap()
)
