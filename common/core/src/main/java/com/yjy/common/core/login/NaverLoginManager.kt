package com.yjy.common.core.login

import android.content.Context
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.yjy.common.core.BuildConfig.NAVER_CLIENT_ID
import com.yjy.common.core.BuildConfig.NAVER_CLIENT_SECRET
import timber.log.Timber

object NaverLoginManager {

    fun init(
        context: Context,
        appName: String,
    ) {
        NaverIdLoginSDK.initialize(context, NAVER_CLIENT_ID, NAVER_CLIENT_SECRET, appName)
    }

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

    fun logout() {
        NaverIdLoginSDK.logout()
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
