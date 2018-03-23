package com.tompee.convoy.interactor.auth

import android.content.Intent
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.tompee.convoy.Constants.AUTH_URL
import io.realm.ObjectServerError
import io.realm.SyncCredentials
import io.realm.SyncUser

class FacebookAuth(private val callbackManager: CallbackManager) : SyncUser.Callback<SyncUser> {
    private lateinit var listener: AuthInteractor.OnLoginFinishedListener

    fun configureLogin(loginButton: LoginButton, listener: AuthInteractor.OnLoginFinishedListener) {
        this.listener = listener
        loginButton.setReadPermissions("email")
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                UserManager.mode = UserManager.AUTH_MODE.FACEBOOK
                val credentials = SyncCredentials.facebook(loginResult.accessToken.token)
                SyncUser.loginAsync(credentials, AUTH_URL, this@FacebookAuth)
            }

            override fun onCancel() {
            }

            override fun onError(exception: FacebookException) {
                listener.onError(AuthException())
            }
        })
    }

    override fun onSuccess(result: SyncUser?) {
        listener.onSuccess()
    }

    override fun onError(error: ObjectServerError?) {
        listener.onError(AuthException())
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
