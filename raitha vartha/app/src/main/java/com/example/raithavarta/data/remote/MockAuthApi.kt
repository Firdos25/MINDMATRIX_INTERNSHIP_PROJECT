package com.example.raithavarta.data.remote

import kotlinx.coroutines.delay

/**
 * Simulates network latency and a simple online auth API.
 * Replace with Retrofit interface in production.
 */
interface MockAuthApi {
    suspend fun login(email: String, password: String): Result<MockUser>
}

data class MockUser(val email: String, val displayName: String)

class MockAuthApiImpl : MockAuthApi {
    override suspend fun login(email: String, password: String): Result<MockUser> {
        delay(600)
        if (!email.contains("@")) {
            return Result.failure(IllegalArgumentException("Invalid email format"))
        }
        if (password.length < 4) {
            return Result.failure(IllegalArgumentException("Password too short (online mock requires 4+ chars)"))
        }
        val name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
        return Result.success(MockUser(email = email.lowercase(), displayName = name))
    }
}
