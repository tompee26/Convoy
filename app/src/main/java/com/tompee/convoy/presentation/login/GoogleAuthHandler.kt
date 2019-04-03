package com.tompee.convoy.presentation.login

import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.api.GoogleApiClient
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject

class GoogleAuthHandler(
    private val fragment: Fragment,
    private val googleApiClient: GoogleApiClient
) {
    companion object {
        private const val RC_SIGN_IN = 10
    }

    private val loginSubject = SingleSubject.create<GoogleSignInResult>()

    fun startLogin(): Single<GoogleSignInResult> {
        val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        fragment.startActivityForResult(intent, RC_SIGN_IN)
        return loginSubject
    }

    fun onActivityResult(requestCode: Int, @Suppress("UNUSED_PARAMETER") resultCode: Int, data: Intent?) {
        if (RC_SIGN_IN == requestCode) {
            loginSubject.onSuccess(Auth.GoogleSignInApi.getSignInResultFromIntent(data))
        }
    }
}