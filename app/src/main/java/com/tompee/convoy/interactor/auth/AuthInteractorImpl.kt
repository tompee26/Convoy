package com.tompee.convoy.interactor.auth

import android.content.Intent
import android.text.TextUtils
import com.facebook.login.widget.LoginButton
import com.google.android.gms.common.SignInButton
import com.tompee.convoy.Constants
import java.util.regex.Pattern
import javax.inject.Inject

class AuthInteractorImpl @Inject constructor(private val userPasswordAuth: UserPasswordAuth,
                                             private val facebookAuth: FacebookAuth,
                                             private val googleAuth: GoogleAuth) : AuthInteractor {
    override fun login(email: String, password: String, listener: AuthInteractor.OnLoginFinishedListener) {
        validateEmailField(email)
        validatePassField(password)
        userPasswordAuth.loginAsync(email, password, listener)
    }

    override fun register(email: String, password: String, listener: AuthInteractor.OnLoginFinishedListener) {
        validateEmailField(email)
        validatePassField(password)
        userPasswordAuth.registerAsync(email, password, listener)
    }

    override fun configureFacebookLogin(loginButton: LoginButton, listener: AuthInteractor.OnLoginFinishedListener) {
        facebookAuth.configureLogin(loginButton, listener)
    }

    override fun configureGoogleLogin(signInButton: SignInButton, listener: AuthInteractor.OnLoginFinishedListener) {
        googleAuth.configureLogin(signInButton, listener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        facebookAuth.onActivityResult(requestCode, resultCode, data)
        googleAuth.onActivityResult(requestCode, resultCode, data)
    }

    private fun validateEmailField(email: String) {
        if (TextUtils.isEmpty(email)) {
            throw EmailEmptyException()
        }
        val ptn = Pattern.compile(Constants.EMAIL_PATTERN)
        val mc = ptn.matcher(email)
        if (!mc.matches()) {
            throw InvalidEmailFormatException()
        }
    }

    private fun validatePassField(password: String) {
        if (TextUtils.isEmpty(password)) {
            throw PasswordEmptyException()
        } else if (password.length < Constants.MIN_PASS_CHAR) {
            throw PasswordTooShortException()
        }
    }
}