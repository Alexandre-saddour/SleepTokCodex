package com.example.data

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform