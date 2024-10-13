package com.yjy.common.navigation

sealed interface Destination {
    data object Auth: Destination
    data object Service: Destination
}
