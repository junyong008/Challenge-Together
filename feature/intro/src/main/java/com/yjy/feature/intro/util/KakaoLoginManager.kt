package com.yjy.feature.intro.util

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import timber.log.Timber

object KakaoLoginManager {
    fun login(
        context: Context,
        onSuccess: (kakaoId: String) -> Unit,
        onFailure: () -> Unit,
    ) {
        val handleLoginResult: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            when {
                error != null -> handleLoginError(error, onFailure)
                token != null -> fetchUserInfo(onSuccess, onFailure)
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            loginWithKakaoApp(context, handleLoginResult, onSuccess, onFailure)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = handleLoginResult)
        }
    }

    private fun loginWithKakaoApp(
        context: Context,
        handleLoginResult: (OAuthToken?, Throwable?) -> Unit,
        onSuccess: (String) -> Unit,
        onFailure: () -> Unit,
    ) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                Timber.e(error, "Failed to login with kakao app")
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    return@loginWithKakaoTalk
                }
                UserApiClient.instance.loginWithKakaoAccount(context, callback = handleLoginResult)
            } else if (token != null) {
                fetchUserInfo(onSuccess, onFailure)
            }
        }
    }

    private fun handleLoginError(error: Throwable, onFailure: () -> Unit) {
        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
            Timber.d("User cancelled the login")
        } else {
            Timber.e(error, "Failed to login with kakao account")
            onFailure()
        }
    }

    private fun fetchUserInfo(
        onSuccess: (kakaoId: String) -> Unit,
        onFailure: () -> Unit,
    ) {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Timber.e(error, "Failed to get user info")
                onFailure()
            } else if (user != null) {
                onSuccess(user.id.toString())
            }
        }
    }
}
