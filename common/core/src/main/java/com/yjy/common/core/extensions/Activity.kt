package com.yjy.common.core.extensions

import android.app.Activity
import android.content.Intent
import android.os.Build

inline fun <reified T : Activity> Activity.startActivityWithAnimation(
    withFinish: Boolean,
    intentBuilder: Intent.() -> Intent = { this },
) {
    startActivity(Intent(this, T::class.java).intentBuilder())
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        overrideActivityTransition(
            Activity.OVERRIDE_TRANSITION_OPEN,
            android.R.anim.fade_in,
            android.R.anim.fade_out,
        )
    } else {
        @Suppress("DEPRECATION")
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
    if (withFinish) finish()
}