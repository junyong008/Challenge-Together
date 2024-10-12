package com.yjy.navigation.auth.route

import kotlinx.serialization.Serializable

sealed interface AuthRoute {
    @Serializable
    data object Login : AuthRoute

    @Serializable
    data object SignUp {
        @Serializable
        data object EmailPassword : AuthRoute

        @Serializable
        data class Nickname(
            val kakaoId: String = "",
            val googleId: String = "",
            val naverId: String = "",
        ) : AuthRoute
    }

    @Serializable
    data object FindPassword {
        @Serializable
        data object InputEmail : AuthRoute

        @Serializable
        data object VerifyCode : AuthRoute
    }

    @Serializable
    data object ChangePassword : AuthRoute
}