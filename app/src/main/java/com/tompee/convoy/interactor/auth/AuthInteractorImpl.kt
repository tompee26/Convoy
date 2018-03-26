package com.tompee.convoy.interactor.auth

import android.content.Intent
import android.text.TextUtils
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tompee.convoy.Constants
import com.tompee.convoy.interactor.model.User
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.util.regex.Pattern

class AuthInteractorImpl(private val firebaseAuth: FirebaseAuth,
                         private val callbackManager: CallbackManager,
                         private val googleApiClient: GoogleApiClient) : AuthInteractor {
    override fun getUser(): Completable {
        return Completable.create({ e ->
            if (firebaseAuth.currentUser != null) {
                e.onComplete()
            } else {
                e.onError(Throwable("No user logged in"))
            }
        })
    }

    override fun signUp(email: String, password: String): Single<User> {
        return validateEmail(email)
                .andThen(validatePass(password))
                .andThen(processSignUp(email, password))
                .andThen(sendEmailVerificationEmail())
    }

    override fun login(email: String, password: String): Single<User> {
        return validateEmail(email)
                .andThen(validatePass(password))
                .andThen(processSignIn(email, password))
    }

    override fun configureFacebookLogin(loginButton: LoginButton): Single<User> {
        return Single.create<AccessToken>({ e ->
            loginButton.setReadPermissions("email", "public_profile")
            loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    e.onSuccess(loginResult.accessToken)
                }

                override fun onCancel() {
                }

                override fun onError(exception: FacebookException) {
                    e.onError(Throwable(exception.message))
                }
            })
        }).flatMap { token -> loginWithCredential(FacebookAuthProvider.getCredential(token.token)) }
    }

    override fun startGoogleLogin(): Observable<Intent> {
        return Observable.just(Auth.GoogleSignInApi.getSignInIntent(googleApiClient))
    }

    private fun loginWithCredential(authCredential: AuthCredential): Single<User> {
        return Single.create<User>({ e ->
            firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener({ task ->
                if (task.isSuccessful) {
                    e.onSuccess(User(firebaseAuth.currentUser?.email!!))
                } else {
                    e.onError(Throwable(task.exception?.message))
                }
            })
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun signInGoogle(data: Intent): Single<User> {
        return Single.create<AuthCredential>({ e ->
            val task = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (task.isSuccess) {
                e.onSuccess(GoogleAuthProvider.getCredential(task.signInAccount?.idToken, null))
            }
        }).flatMap { authCredential -> loginWithCredential(authCredential) }
    }

    private fun processSignUp(email: String, password: String): Completable {
        return Completable.create({ e ->
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    e.onComplete()
                } else {
                    e.onError(Throwable(task.exception?.message))
                }
            }
        })
    }

    private fun sendEmailVerificationEmail(): Single<User> {
        return Single.create({ e ->
            firebaseAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener({ task ->
                if (task.isSuccessful) {
                    e.onSuccess(User(firebaseAuth.currentUser?.email!!))
                } else {
                    e.onError(Throwable(task.exception?.message))
                }
            })
        })
    }

    private fun processSignIn(email: String, password: String): Single<User> {
        return Single.create({ e ->
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener({ task ->
                if (task.isSuccessful) {
                    e.onSuccess(User(firebaseAuth.currentUser?.email!!))
                } else {
                    e.onError(Throwable(task.exception?.message))
                }
            })
        })
    }

    private fun validateEmail(email: String): Completable {
        return Completable.create({ e ->
            if (TextUtils.isEmpty(email)) {
                e.onError(EmailEmptyException())
                return@create
            }
            val ptn = Pattern.compile(Constants.EMAIL_PATTERN)
            val mc = ptn.matcher(email)
            if (!mc.matches()) {
                e.onError(InvalidEmailFormatException())
                return@create
            }
            e.onComplete()
        })
    }

    private fun validatePass(password: String): Completable {
        return Completable.create({ e ->
            if (TextUtils.isEmpty(password)) {
                e.onError(PasswordEmptyException())
                return@create
            } else if (password.length < Constants.MIN_PASS_CHAR) {
                e.onError(PasswordTooShortException())
                return@create
            }
            e.onComplete()
        })
    }
}