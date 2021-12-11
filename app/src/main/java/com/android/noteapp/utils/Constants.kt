package com.android.noteapp.utils

import com.android.noteapp.data.remote.models.Role

object Constants {
    const val JWT_TOKEN_KEY = "JWT_TOKEN_KEY"
    const val NAME_KEY = "NAME_KEY"
    const val EMAIL_KEY = "EMAIL_KEY"
    const val ROLE_KEY = "ROLE_KEY"
    const val API_VERSION = "/v1"
    const val BASE_URL = "https://immense-badlands-06517.herokuapp.com"
    const val MINIMUM_PASSWORD_LENGTH = 4
    const val MAXIMUM_PASSWORD_LENGTH = 8
    var ROLE: Role?= null
}