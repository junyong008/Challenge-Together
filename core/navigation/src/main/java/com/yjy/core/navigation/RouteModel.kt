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
            val kakaoId: String? = null,
            val googleId: String? = null,
            val naverId: String? = null,
        ) : Route
    }
}
