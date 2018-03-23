package com.tompee.convoy.interactor.auth

import com.google.firebase.auth.FirebaseAuth
import com.tompee.convoy.interactor.auth.model.User
import io.reactivex.Completable
import io.reactivex.Single

class AuthInteractorImpl(private val firebaseAuth: FirebaseAuth) : AuthInteractor {
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
        return Single.create<User>({ e ->
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    e.onSuccess(User(firebaseAuth.currentUser?.email!!))
                } else {
                    e.onError(Throwable("Registration failed"))
                }
            }
        })
    }
}