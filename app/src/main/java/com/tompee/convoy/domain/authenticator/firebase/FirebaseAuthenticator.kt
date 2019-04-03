package com.tompee.convoy.domain.authenticator.firebase

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tompee.convoy.domain.authenticator.Authenticator
import io.reactivex.Completable
import io.reactivex.Single

class FirebaseAuthenticator(private val firebaseAuth: FirebaseAuth) : Authenticator {

    override fun getCurrentUserEmail(): Single<String> {
        return Single.create<String> { emitter ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null && currentUser.email != null) {
                emitter.onSuccess(currentUser.email!!)
            } else {
                emitter.onError(Throwable("No user logged in"))
            }
        }
    }

    override fun login(email: String, password: String): Single<String> {
        return Single.create<String> { emitter ->
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    emitter.onSuccess(firebaseAuth.currentUser?.email!!)
                } else {
                    emitter.onError(Throwable(it.exception?.message))
                }
            }
        }
    }

    override fun login(token: AccessToken): Single<String> {
        return Single.create<String> { emitter ->
            val credential = FacebookAuthProvider.getCredential(token.token)
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    emitter.onSuccess(firebaseAuth.currentUser?.email!!)
                } else {
                    emitter.onError(Throwable(task.exception?.message))
                }
            }
        }
    }

    override fun login(result: GoogleSignInResult): Single<String> {
        return Single.create<String> { emitter ->
            if (result.isSuccess) {
                val credential = GoogleAuthProvider.getCredential(result.signInAccount?.idToken, null)
                firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        emitter.onSuccess(firebaseAuth.currentUser?.email!!)
                    } else {
                        emitter.onError(Throwable(task.exception?.message))
                    }
                }
            } else {
                emitter.onError(Throwable("Login failed"))
            }
        }
    }

    override fun logout(): Completable {
        return Completable.create { emitter ->
            firebaseAuth.signOut()
            emitter.onComplete()
        }
    }

    override fun register(email: String, password: String): Completable {
        return Completable.create { emitter ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        emitter.onComplete()
                    } else {
                        emitter.onError(Throwable(it.exception?.message))
                    }
                }
        }.andThen(Completable.create { emitter ->
            firebaseAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
                if (it.isSuccessful) {
                    emitter.onComplete()
                } else {
                    emitter.onError(Throwable(it.exception?.message))
                }
            }
        }).andThen(Completable.fromAction { firebaseAuth.signOut() })
    }
}