package com.yjy.common.navigation

import com.yjy.model.challenge.core.Category
import kotlinx.serialization.Serializable

sealed interface Route

sealed interface ServiceRoute : Route {

    sealed interface MainTab : ServiceRoute {
        @Serializable
        data object Home : MainTab

        @Serializable
        data object Together : MainTab {
            @Serializable
            data object List : MainTab

            @Serializable
            data class Detail(val category: Category) : MainTab
        }

        @Serializable
        data object Community : MainTab

        @Serializable
        data object My : MainTab
    }

    @Serializable
    data object Community : ServiceRoute {
        @Serializable
        data class Post(val postId: Int) : ServiceRoute

        @Serializable
        data class EditPost(val postId: Int, val content: String) : ServiceRoute

        @Serializable
        data object AddPost : ServiceRoute

        @Serializable
        data object BookmarkedPosts : ServiceRoute

        @Serializable
        data object CommentedPosts : ServiceRoute

        @Serializable
        data object AuthoredPosts : ServiceRoute
    }

    @Serializable
    data object AppLock {
        @Serializable
        data object Setting : ServiceRoute

        @Serializable
        data object PinSetting : ServiceRoute

        @Serializable
        data object PinValidation : ServiceRoute
    }

    @Serializable
    data object ThemeSetting : ServiceRoute

    @Serializable
    data object Notification : ServiceRoute

    @Serializable
    data object NotificationSetting : ServiceRoute

    @Serializable
    data object Premium : ServiceRoute

    @Serializable
    data object ChangeName : ServiceRoute

    @Serializable
    data object LinkAccount : ServiceRoute

    @Serializable
    data object DeleteAccount : ServiceRoute

    @Serializable
    data object CompletedChallenges : ServiceRoute

    @Serializable
    data class WaitingChallenge(val challengeId: Int) : ServiceRoute

    @Serializable
    data class StartedChallenge(val challengeId: Int) : ServiceRoute

    @Serializable
    data class ResetRecord(val challengeId: Int) : ServiceRoute

    @Serializable
    data class ChallengeBoard(val challengeId: Int, val isEditable: Boolean) : ServiceRoute

    @Serializable
    data class ChallengeRanking(val challengeId: Int) : ServiceRoute

    @Serializable
    data class ChallengeProgress(val challengeId: Int) : ServiceRoute

    @Serializable
    data class ChallengeReward(val challengeId: Int) : ServiceRoute

    @Serializable
    data class EditCategory(
        val challengeId: Int,
        val category: Category,
    ) : ServiceRoute

    @Serializable
    data class EditTitleDescription(
        val challengeId: Int,
        val title: String,
        val description: String,
    ) : ServiceRoute

    @Serializable
    data class EditTargetDays(
        val challengeId: Int,
        val targetDays: String,
        val currentDays: Int,
    ) : ServiceRoute

    @Serializable
    data object AddChallenge {
        @Serializable
        data object SetMode : ServiceRoute

        @Serializable
        data object SetCategory : ServiceRoute

        @Serializable
        data object SetTitle : ServiceRoute

        @Serializable
        data object SetTargetDay : ServiceRoute

        @Serializable
        data object SetStartDate : ServiceRoute

        @Serializable
        data object SetTogether : ServiceRoute

        @Serializable
        data object Confirm : ServiceRoute
    }
}

sealed interface AuthRoute : Route {
    @Serializable
    data object Intro : AuthRoute

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
            val guestId: String = "",
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
