package com.yjy.feature.findpassword.model

sealed interface FindPasswordUiEvent {
    data object VerifyCodeSent : FindPasswordUiEvent
    data object VerifySuccess : FindPasswordUiEvent

    sealed class SendVerifyCodeFailure : FindPasswordUiEvent {
        data object UnregisteredEmail : SendVerifyCodeFailure()
        data object InvalidEmail : SendVerifyCodeFailure()
    }

    sealed class VerifyFailure : FindPasswordUiEvent {
        data object Timeout : VerifyFailure()
        data object UnMatchedCode : VerifyFailure()
    }

    sealed class Failure : FindPasswordUiEvent {
        data object NetworkError : Failure()
        data object UnknownError : Failure()
    }
}
