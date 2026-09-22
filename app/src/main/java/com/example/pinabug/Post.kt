package com.example.pinabug

data class Post(
    val name: String,
    val imageUri: String?,
    val timestamp: Long,
    val latitude: Double?,
    val longitude: Double?
)
