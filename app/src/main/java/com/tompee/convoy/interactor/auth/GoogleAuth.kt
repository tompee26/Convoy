package com.tompee.convoy.interactor.auth

import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.tompee.convoy.Constants.AUTH_URL
import com.tompee.convoy.R
import io.realm.ObjectServerError
import io.realm.SyncCredentials
import io.realm.SyncUser

class GoogleAuth(private val fragmentActivity: FragmentActivity) : GoogleApiClient.OnConnectionFailedListener,
        SyncUser.Callback<SyncUser> {
    companion object {
        private const val RC_SIGN_IN = 10
    }

    private lateinit var listener: AuthInteractor.OnLoginFinishedListener

    fun configureLogin(btnSignIn: SignInButton, listener: AuthInteractor.OnLoginFinishedListener) {
        this.listener = listener

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(fragmentActivity.getString(R.string.google_client_id))
                .build()

        val googleApiClient = GoogleApiClient.Builder(fragmentActivity)
                .enableAutoManage(fragmentActivity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        btnSignIn.setOnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
            fragmentActivity.startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    /**
     * Notify this class about the [FragmentActivity.onResume] event.
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        if (!connectionResult.hasResolution()) {
            listener.onError(AuthException())
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        if (result.isSuccess) {
            UserManager.mode = UserManager.AUTH_MODE.GOOGLE
            val acct = result.signInAccount
            val credentials = SyncCredentials.google(acct?.idToken!!)
            SyncUser.loginAsync(credentials, AUTH_URL, this@GoogleAuth)
        }
    }

    override fun onSuccess(result: SyncUser?) {
        listener.onSuccess()
    }

    override fun onError(error: ObjectServerError?) {
        listener.onError(AuthException())
    }

}