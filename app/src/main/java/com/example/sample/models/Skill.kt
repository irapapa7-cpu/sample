package com.example.sample.models

data class Skill(
    val id: String,
    val name: String,
    val description: String,
    val isUnlocked: Boolean = false,
    val dependencies: List<String> = emptyList() // IDs of skills required to unlock this one
)
