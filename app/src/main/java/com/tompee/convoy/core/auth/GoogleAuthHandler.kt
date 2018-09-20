package com.tompee.convoy.core.auth

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.api.GoogleApiClient
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject

class GoogleAuthHandler(private val activity: Activity,
                        private val googleApiClient: GoogleApiClient) {
    companion object {
        private const val RC_SIGN_IN = 10
    }

    private val loginSubject = SingleSubject.create<GoogleSignInResult>()

    fun startLogin(): Single<GoogleSignInResult> {
        val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        activity.startActivityForResult(intent, RC_SIGN_IN)
        return loginSubject
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (RC_SIGN_IN == requestCode) {
            loginSubject.onSuccess(Auth.GoogleSignInApi.getSignInResultFromIntent(data))
        }
    }
}