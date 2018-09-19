package com.tompee.convoy.core.auth.firebase

import com.google.firebase.auth.FirebaseAuth
import com.tompee.convoy.core.auth.Authenticator
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

    override fun signup(email: String, password: String): Completable {
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

    override fun logout(): Completable {
        return Completable.create { emitter ->
            firebaseAuth.signOut()
            emitter.onComplete()
        }
    }

}