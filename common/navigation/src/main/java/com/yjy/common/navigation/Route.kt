package com.yjy.common.navigation

import kotlinx.serialization.Serializable

sealed interface Route

sealed interface ServiceRoute : Route {

    sealed class MainTab : ServiceRoute {
        @Serializable
        data object Home : MainTab()
    }
}

sealed interface AuthRoute : Route {
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
}

sealed interface CommonRoute : Route {

    @Serializable
    data object ChangePassword : CommonRoute
}
