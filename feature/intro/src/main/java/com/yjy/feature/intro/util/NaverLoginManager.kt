package com.yjy.feature.intro.util

import android.content.Context
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import timber.log.Timber

object NaverLoginManager {
    fun login(
        context: Context,
        onSuccess: (naverId: String) -> Unit,
        onFailure: () -> Unit,
    ) {
        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                fetchUserProfile(onSuccess, onFailure)
            }

            override fun onFailure(httpStatus: Int, message: String) {
                Timber.e(
                    "Naver Login Failed - ${NaverIdLoginSDK.getLastErrorCode().code} / " +
                        NaverIdLoginSDK.getLastErrorDescription(),
                )
                onFailure()
            }

            override fun onError(errorCode: Int, message: String) {
                Timber.e("Naver Login Error - ErrorCode: $errorCode, ErrorMessage: $message")
            }
        }

        NaverIdLoginSDK.authenticate(context, oauthLoginCallback)
    }

    private fun fetchUserProfile(
        onSuccess: (naverId: String) -> Unit,
        onFailure: () -> Unit,
    ) {
        val nidOAuthLogin = NidOAuthLogin()
        nidOAuthLogin.callProfileApi(object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(result: NidProfileResponse) {
                val id = result.profile?.id
                if (id != null) {
                    onSuccess(id)
                } else {
                    Timber.e("Naver user id is null")
                    onFailure()
                }
            }

            override fun onError(errorCode: Int, message: String) {
                Timber.e("Failed to get user profile. ErrorCode: $errorCode, ErrorMessage: $message")
                onFailure()
            }

            override fun onFailure(httpStatus: Int, message: String) {
                Timber.e("Failed to request user profile. HttpStatus: $httpStatus, ErrorMessage: $message")
                onFailure()
            }
        })
    }
}
