package com.tompee.convoy.presentation.login

import android.content.Intent
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import io.reactivex.Single

class FacebookAuthHandler(private val callbackManager: CallbackManager) {

    fun configureLogin(loginButton: LoginButton): Single<AccessToken> {
        return Single.create<AccessToken> { emitter ->
            loginButton.setReadPermissions("email", "public_profile")
            loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    emitter.onSuccess(loginResult.accessToken)
                }

                override fun onCancel() {
                    emitter.onError(Throwable("Cancelled"))
                }

                override fun onError(exception: FacebookException) {
                    emitter.onError(Throwable(exception.message))
                }
            })
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}