package com.tompee.convoy.domain.entities

data class Account(
    val email: String,
    val isAuthenticated: Boolean,
    val firstName: String,
    val lastName: String,
    val displayName: String,
    val imageUrl: String
)