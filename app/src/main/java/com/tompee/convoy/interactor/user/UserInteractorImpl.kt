package com.tompee.convoy.interactor.user

import com.google.firebase.firestore.FirebaseFirestore
import com.tompee.convoy.interactor.model.User
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class UserInteractorImpl(private val db: FirebaseFirestore) : UserInteractor {
    companion object {
        private const val PROFILE = "profile"
        private const val FRIENDS = "friends"
        private const val INCOMING_REQUEST = "inrequest"
        private const val OUTGOING_REQUEST = "outrequest"
        private const val DISPLAY = "display"
        private const val EMAIL = "email"
    }

    override fun getUser(email: String): Single<User> {
        return Single.create<User>({ e ->
            val docRef = db.collection(PROFILE).document(email)
            docRef.get().addOnSuccessListener { snapshot ->
                if (snapshot != null && snapshot.exists()) {
                    e.onSuccess(snapshot.toObject(User::class.java))
                } else {
                    e.onError(Throwable(email))
                }
            }
                    .addOnFailureListener({ e.onError(Throwable(email)) })
        })
    }

    override fun getUserChanges(email: String): Observable<User> {
        return Observable.create({ e ->
            val docRef = db.collection(PROFILE).document(email)
            docRef.addSnapshotListener({ snapshot, error ->
                if (error != null) {
                    e.onError(Throwable(error.message!!))
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    e.onNext(snapshot.toObject(User::class.java))
                } else {
                    e.onError(Throwable("Document does not exist"))
                }
            })
        })
    }

    override fun saveUser(user: User): Single<User> {
        return Single.create<User>({ e ->
            db.collection(PROFILE).document(user.email).set(user)
            e.onSuccess(user)
        })
    }

    override fun searchUser(user: String, email: String): Single<List<User>> {
        return Single.create<List<User>>({ e ->
            if (user.isEmpty()) {
                e.onSuccess(emptyList())
                return@create
            }
            val query = db.collection(PROFILE).orderBy(DISPLAY).startAt(user)
                    .endAt(user + "\uf8ff")
            query.get().addOnSuccessListener { snapshot ->
                val userList = mutableListOf<User>()
                snapshot.forEach {
                    if (it != null && it.exists()) {
                        userList.add(it.toObject(User::class.java))
                    }
                }
                userList.removeAll { user -> user.email == email }
                e.onSuccess(userList)
            }
        })
    }

    override fun addFriendRequest(own: String, target: String): Completable {
        return Completable.create({ e ->
            val inMap = HashMap<String, String>()
            inMap[EMAIL] = own
            db.collection(PROFILE).document(target).collection(INCOMING_REQUEST).add(inMap as Map<String, Any>)

            val outMap = HashMap<String, String>()
            outMap[EMAIL] = target
            db.collection(PROFILE).document(own).collection(OUTGOING_REQUEST).add(outMap as Map<String, Any>)
            e.onComplete()
        })
    }

    override fun findUserInOutgoingFriendRequest(own: String, target: String): Completable {
        return Completable.create({ e ->
            db.collection(PROFILE).document(own).collection(OUTGOING_REQUEST).whereEqualTo(EMAIL, target)
                    .get().addOnCompleteListener({ task ->
                        if (task.isSuccessful) {
                            task.result.forEach {
                                e.onComplete()
                                return@addOnCompleteListener
                            }
                        }
                        e.onError(Throwable())
                    })
        })
    }

    override fun findUserInIncomingFriendRequest(own: String, target: String): Completable {
        return Completable.create({ e ->
            db.collection(PROFILE).document(own).collection(INCOMING_REQUEST).whereEqualTo(EMAIL, target)
                    .get().addOnCompleteListener({ task ->
                        if (task.isSuccessful) {
                            task.result.forEach {
                                e.onComplete()
                                return@addOnCompleteListener
                            }
                        }
                        e.onError(Throwable())
                    })
        })
    }

    override fun acceptFriendRequest(own: String, target: String): Completable {
        /* add to friends list first */
        return Completable.create({ e ->
            val ownMap = HashMap<String, String>()
            ownMap[EMAIL] = target
            db.collection(PROFILE).document(own).collection(FRIENDS).add(ownMap as Map<String, Any>)

            val targetMap = HashMap<String, String>()
            targetMap[EMAIL] = own
            db.collection(PROFILE).document(target).collection(FRIENDS).add(targetMap as Map<String, Any>)
            e.onComplete()
        }).andThen { e ->
            /* and then delete from request list */
            db.collection(PROFILE).document(own).collection(INCOMING_REQUEST).whereEqualTo(EMAIL, target)
                    .get().addOnCompleteListener({ task ->
                        if (task.isSuccessful) {
                            task.result.forEach { snapshot ->
                                snapshot.reference.delete()
                                return@addOnCompleteListener
                            }
                        }
                    })
            db.collection(PROFILE).document(target).collection(OUTGOING_REQUEST).whereEqualTo(EMAIL, own)
                    .get().addOnCompleteListener({ task ->
                        if (task.isSuccessful) {
                            task.result.forEach { snapshot ->
                                snapshot.reference.delete()
                                return@addOnCompleteListener
                            }
                        }
                    })
            e.onComplete()
        }
    }
}