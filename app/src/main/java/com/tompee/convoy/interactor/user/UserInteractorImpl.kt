package com.tompee.convoy.interactor.user

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.tompee.convoy.interactor.model.User
import io.reactivex.Observable
import io.reactivex.Single

class UserInteractorImpl(private val databaseReference: DatabaseReference) : UserInteractor {
    companion object {
        private const val PROFILE = "profile"
        private var userChangeObservable: Observable<User>? = null
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

    override fun getUserChanges(userId: String): Observable<User> {
        return Observable.create<User>({ e ->
            val reference = databaseReference.child(PROFILE).child(userId)
            reference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    e.onError(error.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    if (user != null) {
                        e.onNext(user)
                    }
                }
            })
        })
    }

    override fun saveUser(user: User): Single<User> {
        return Single.create<User> { e ->
            val key = databaseReference.child(PROFILE).push().key
            user.id = key
            databaseReference.child(PROFILE).child(key).setValue(user)
            e.onSuccess(user)
        }
    }
}