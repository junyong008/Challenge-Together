package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.auth.ChangePasswordRequest
import com.yjy.data.network.request.auth.EmailLoginRequest
import com.yjy.data.network.request.auth.EmailRequest
import com.yjy.data.network.request.auth.GoogleLoginRequest
import com.yjy.data.network.request.auth.GuestLoginRequest
import com.yjy.data.network.request.auth.KakaoLoginRequest
import com.yjy.data.network.request.auth.NaverLoginRequest
import com.yjy.data.network.request.auth.SignUpRequest
import com.yjy.data.network.request.auth.VerifyRequest

interface AuthDataSource {
    suspend fun signUp(signUpRequest: SignUpRequest): NetworkResult<Unit>
    suspend fun emailLogin(emailLoginRequest: EmailLoginRequest): NetworkResult<Unit>
    suspend fun kakaoLogin(kakaoLoginRequest: KakaoLoginRequest): NetworkResult<Unit>
    suspend fun googleLogin(googleLoginRequest: GoogleLoginRequest): NetworkResult<Unit>
    suspend fun naverLogin(naverLoginRequest: NaverLoginRequest): NetworkResult<Unit>
    suspend fun guestLogin(guestLoginRequest: GuestLoginRequest): NetworkResult<Unit>
    suspend fun checkEmailDuplicate(email: String): NetworkResult<Unit>
    suspend fun requestVerifyCode(emailRequest: EmailRequest): NetworkResult<Unit>
    suspend fun verifyCode(verifyRequest: VerifyRequest): NetworkResult<Unit>
    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest): NetworkResult<Unit>
    suspend fun deleteAccount(): NetworkResult<Unit>
}
