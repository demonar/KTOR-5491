package com.example.ktor5491

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform