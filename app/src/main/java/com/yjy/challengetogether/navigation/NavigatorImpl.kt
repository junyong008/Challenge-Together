package com.yjy.challengetogether.navigation

import android.content.Context
import android.content.Intent
import com.yjy.common.navigation.Destination
import com.yjy.common.navigation.Navigator
import com.yjy.navigation.auth.AuthActivity
import com.yjy.navigation.service.ServiceActivity
import javax.inject.Inject

class NavigatorImpl @Inject constructor(
    private val context: Context,
) : Navigator {

    override fun createIntent(destination: Destination): Intent {
        return when (destination) {
            Destination.Auth -> Intent(context, AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            Destination.Service -> Intent(context, ServiceActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
        }
    }
}
