package com.yjy.common.navigation

import kotlinx.serialization.Serializable

sealed interface MainTabRoute : Route {
    @Serializable
    data object Home : MainTabRoute
}

sealed interface Route {
    @Serializable
    data object Login : Route

    @Serializable
    data object SignUp {
        @Serializable
        data object EmailPassword : Route

        @Serializable
        data class Nickname(
            val kakaoId: String = "",
            val googleId: String = "",
            val naverId: String = "",
        ) : Route
    }

    @Serializable
    data object FindPassword {
        @Serializable
        data object InputEmail : Route

        @Serializable
        data object VerifyCode : Route
    }

    @Serializable
    data object ChangePassword : Route
}
