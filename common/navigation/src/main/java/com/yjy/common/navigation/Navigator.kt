package com.yjy.common.navigation

import android.content.Intent

interface Navigator {
    fun createIntent(destination: Destination): Intent
}
