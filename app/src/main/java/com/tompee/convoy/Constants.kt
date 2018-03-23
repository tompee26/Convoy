package com.tompee.convoy

object Constants {
    const val AUTH_URL = "http://54.95.158.255:9080/auth"
    const val CATEGORY_URL = "realm://54.95.158.255:9080/category"
    const val REALM_URL = "realm://54.95.158.255:9080/~/default"

    const val EMAIL_PATTERN = "[A-Z0-9a-z_%+-]+(\\.[A-Z0-9a-z_%+-]+)*@[A-Za-z0-9]+([.-][A-Za-z0-9]+)*\\.[A-Za-z]{2,}"
    const val MIN_PASS_CHAR = 6

    const val LOCATION_INTERVAL = 1000L

    const val ZOOM = 10f
}