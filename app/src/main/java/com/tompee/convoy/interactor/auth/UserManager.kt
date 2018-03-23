package com.tompee.convoy.interactor.auth

import com.facebook.login.LoginManager
import io.realm.SyncUser

object UserManager {

    enum class AUTH_MODE {
        PASSWORD,
        FACEBOOK,
        GOOGLE
    }

    var mode = AUTH_MODE.PASSWORD

    fun logoutActiveUser() {
        when (mode) {
            AUTH_MODE.PASSWORD -> {
            }
            AUTH_MODE.FACEBOOK -> {
                LoginManager.getInstance().logOut()
            }
            AUTH_MODE.GOOGLE -> {
            }
        }
        SyncUser.currentUser().logout()
    }
}