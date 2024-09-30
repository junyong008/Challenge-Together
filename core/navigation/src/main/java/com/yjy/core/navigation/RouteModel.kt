package com.yjy.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Login : Route

    @Serializable
    data object SignUp {
        @Serializable
        data object EmailPassword : Route

        @Serializable
        data class Nickname(
            val email: String,
            val password: String,
            val kakaoId: String,
            val googleId: String,
            val naverId: String,
        ) : Route
    }
}
