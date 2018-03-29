package com.tompee.convoy.interactor.user

import android.text.TextUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.tompee.convoy.interactor.model.User
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.SingleSource

class UserInteractorImpl(private val databaseReference: DatabaseReference) : UserInteractor {
    companion object {
        private const val PROFILE = "profile"
    }

    override fun getUser(email: String): Single<User> {
        return Single.create<User>({ e ->
            val reference = databaseReference.child(PROFILE)
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    e.onError(error.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.children.forEach { it ->
                        val user = it.getValue(User::class.java)
                        if (user != null && user.email == email) {
                            e.onSuccess(user)
                            return
                        }
                    }
                    e.onError(Throwable(email))
                }
            })
        })
    }

    override fun saveUser(firstName: String, lastName: String, displayName: String, email: String): Single<User> {
        return validateFirstName(firstName)
                .andThen(validateLastName(lastName))
                .andThen(validateDisplayName(displayName))
                .andThen(SingleSource<User> { e ->
                    val user = User(email, displayName, firstName, lastName)
                    val key = databaseReference.child(PROFILE).push().key
                    user.uuid = key
                    databaseReference.child(PROFILE).child(key).setValue(user)
                    e.onSuccess(user)
                })
    }

    private fun validateFirstName(firstName: String): Completable {
        return Completable.create({ e ->
            if (TextUtils.isEmpty(firstName)) {
                e.onError(EmptyFirstNameException())
                return@create
            }
            e.onComplete()
        })
    }

    private fun validateLastName(lastName: String): Completable {
        return Completable.create({ e ->
            if (TextUtils.isEmpty(lastName)) {
                e.onError(EmptyLastNameException())
                return@create
            }
            e.onComplete()
        })
    }

    private fun validateDisplayName(displayName: String): Completable {
        return Completable.create({ e ->
            if (TextUtils.isEmpty(displayName)) {
                e.onError(EmptyDisplayNameException())
                return@create
            }
            e.onComplete()
        })
    }
}