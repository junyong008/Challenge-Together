package com.yjy.model.common

data class Version(val version: String) : Comparable<Version> {
    private val majorVersion = version.split(".").firstOrNull()?.toIntOrNull() ?: 0

    override fun compareTo(other: Version): Int {
        return majorVersion.compareTo(other.majorVersion)
    }
}
