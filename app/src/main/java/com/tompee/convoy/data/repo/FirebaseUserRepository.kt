package com.tompee.convoy.data.repo

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.tompee.convoy.data.entities.UserEntity
import com.tompee.convoy.domain.entities.User
import com.tompee.convoy.domain.repo.UserRepository
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class FirebaseUserRepository(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : UserRepository {

    companion object {
        private const val PROFILE = "profile"
        private const val FRIENDS = "friends"
        private const val FILENAME = "profile.jpg"
        private const val DISPLAY = "displayName"
        private const val EMAIL = "email"
        private const val OUTGOING_REQUEST = "outrequest"
        private const val INCOMING_REQUEST = "inrequest"
    }

    override fun getUser(email: String): Single<User> {
        return Single.create<UserEntity> { emitter ->
            val docRef = db.collection(PROFILE).document(email)
            docRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    emitter.onSuccess(documentSnapshot.toObject(UserEntity::class.java)!!)
                } else {
                    emitter.onError(Throwable("User not found"))
                }
            }.addOnFailureListener { emitter.onError(Throwable(it.message)) }
        }.map { User(it.email, it.firstName, it.lastName, it.displayName, it.image) }
    }

    override fun saveUser(user: User): Completable {
        return Single.just(user)
            .flatMap {
                if (it.image.isNotEmpty()) {
                    uploadProfileImage(it.email, Uri.parse(it.image))
                        .map { url -> UserEntity(it.email, it.firstName, it.lastName, it.displayName, url) }
                } else {
                    Single.just(UserEntity(it.email, it.firstName, it.lastName, it.displayName, ""))
                }
            }
            .flatMapCompletable(::saveUser)
    }

    private fun uploadProfileImage(email: String, uri: Uri): Single<String> {
        return Single.create<String> { emitter ->
            val storageRef = storage.getReference(PROFILE).child(email).child(FILENAME)
            storageRef.putFile(uri).continueWithTask { storageRef.downloadUrl }
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        emitter.onSuccess(it.result.toString())
                    } else {
                        emitter.onError(Throwable(it.exception?.message))
                    }
                }
        }
    }

    private fun saveUser(userEntity: UserEntity): Completable {
        return Completable.create { emitter ->
            db.collection(PROFILE).document(userEntity.email).set(userEntity)
            /* Offline does not trigger completion event.
            That's why we will assume that all writes are successful */
            emitter.onComplete()
        }
    }

    override fun searchUser(text: String, exclusionList: List<String>): Single<List<User>> {
        return Single.create<List<User>> { e ->
            if (text.isEmpty()) {
                e.onSuccess(emptyList())
                return@create
            }
            val query = db.collection(PROFILE)
                .orderBy(DISPLAY)
                .startAt(text)
                .endAt(text + "\uf8ff")
            query.get().addOnSuccessListener { snapshot ->
                val userList = mutableListOf<User>()
                snapshot.forEach {
                    if (it != null && it.exists()) {
                        val entity = it.toObject(UserEntity::class.java)
                        userList.add(
                            User(
                                entity.email,
                                entity.firstName,
                                entity.lastName,
                                entity.displayName,
                                entity.image
                            )
                        )
                    }
                }
                exclusionList.forEach {
                    userList.removeAll { user -> user.email == it }
                }
                e.onSuccess(userList)
            }
        }
    }

    override fun searchInFriends(email: String, friendEmail: String): Completable {
        return Completable.create { e ->
            db.collection(PROFILE).document(email).collection(FRIENDS).whereEqualTo(EMAIL, friendEmail)
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.forEach { _ ->
                            e.onComplete()
                            return@addOnCompleteListener
                        }
                    }
                    e.onError(Throwable())
                }
        }
    }

    override fun searchInSentRequest(email: String, friendEmail: String): Completable {
        return Completable.create { e ->
            db.collection(PROFILE).document(email).collection(OUTGOING_REQUEST).whereEqualTo(EMAIL, friendEmail)
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.forEach { _ ->
                            e.onComplete()
                            return@addOnCompleteListener
                        }
                    }
                    e.onError(Throwable())
                }
        }
    }

    override fun searchInReceivedRequest(email: String, friendEmail: String): Completable {
        return Completable.create { e ->
            db.collection(PROFILE).document(email).collection(INCOMING_REQUEST).whereEqualTo(EMAIL, friendEmail)
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.forEach { _ ->
                            e.onComplete()
                            return@addOnCompleteListener
                        }
                    }
                    e.onError(Throwable())
                }
        }
    }

    override fun sendFriendRequest(email: String, friendEmail: String): Completable {
        return Completable.create { e ->
            val inMap = HashMap<String, String>()
            inMap[EMAIL] = email
            db.collection(PROFILE).document(friendEmail).collection(INCOMING_REQUEST).add(inMap as Map<String, Any>)

            val outMap = HashMap<String, String>()
            outMap[EMAIL] = friendEmail
            db.collection(PROFILE).document(email).collection(OUTGOING_REQUEST).add(outMap as Map<String, Any>)
            e.onComplete()
        }
    }

    override fun acceptFriendRequest(email: String, friendEmail: String): Completable {
        /* add to friends list first */
        return Completable.create { e ->
            val ownMap = HashMap<String, String>()
            ownMap[EMAIL] = friendEmail
            db.collection(PROFILE).document(email).collection(FRIENDS).add(ownMap as Map<String, Any>)

            val targetMap = HashMap<String, String>()
            targetMap[EMAIL] = email
            db.collection(PROFILE).document(friendEmail).collection(FRIENDS).add(targetMap as Map<String, Any>)
            e.onComplete()
        }.andThen { e ->
            /* and then delete from request list */
            db.collection(PROFILE).document(email).collection(INCOMING_REQUEST).whereEqualTo(EMAIL, friendEmail)
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.forEach { snapshot ->
                            snapshot.reference.delete()
                            return@addOnCompleteListener
                        }
                    }
                }
            db.collection(PROFILE).document(friendEmail).collection(OUTGOING_REQUEST).whereEqualTo(EMAIL, email)
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.forEach { snapshot ->
                            snapshot.reference.delete()
                            return@addOnCompleteListener
                        }
                    }
                }
            e.onComplete()
        }
    }

    override fun rejectFriendRequest(email: String, friendEmail: String): Completable {
        return Completable.create { e ->
            db.collection(PROFILE).document(email).collection(INCOMING_REQUEST).whereEqualTo(EMAIL, friendEmail)
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.forEach { snapshot ->
                            snapshot.reference.delete()
                            return@addOnCompleteListener
                        }
                    }
                }
            db.collection(PROFILE).document(friendEmail).collection(OUTGOING_REQUEST).whereEqualTo(EMAIL, email)
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.forEach { snapshot ->
                            snapshot.reference.delete()
                            return@addOnCompleteListener
                        }
                    }
                }
            e.onComplete()
        }
    }

    override fun getFriendsList(email: String): Flowable<List<User>> {
        return Flowable.create<List<String>>({ e ->
            db.collection(PROFILE).document(email).collection(FRIENDS)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        e.onNext(emptyList())
                        return@addSnapshotListener
                    }

                    val list = mutableListOf<String>()
                    snapshot?.forEach {
                        list.add(it.data[EMAIL].toString())
                    }
                    e.onNext(list)
                }
        }, BackpressureStrategy.LATEST)
            .concatMap { list ->
                Flowable.fromIterable(list)
                    .flatMapSingle { getUser(it) }
                    .toList()
                    .toFlowable()
            }
    }

    override fun getFriendRequests(email: String): Flowable<List<User>> {
        return Flowable.create<List<String>>({ e ->
            db.collection(PROFILE).document(email).collection(OUTGOING_REQUEST)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        e.onNext(emptyList())
                        return@addSnapshotListener
                    }

                    val list = mutableListOf<String>()
                    snapshot?.forEach {
                        list.add(it.data[EMAIL].toString())
                    }
                    e.onNext(list)
                }
        }, BackpressureStrategy.LATEST)
            .concatMap { list ->
                Flowable.fromIterable(list)
                    .flatMapSingle { getUser(it) }
                    .toList()
                    .toFlowable()
            }
    }

    override fun getIncomingFriendRequests(email: String): Flowable<List<User>> {
        return Flowable.create<List<String>>({ e ->
            db.collection(PROFILE).document(email).collection(INCOMING_REQUEST)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        e.onNext(emptyList())
                        return@addSnapshotListener
                    }

                    val list = mutableListOf<String>()
                    snapshot?.forEach {
                        list.add(it.data[EMAIL].toString())
                    }
                    e.onNext(list)
                }
        }, BackpressureStrategy.LATEST)
            .flatMap { list ->
                Flowable.fromIterable(list)
                    .flatMapSingle { getUser(it) }
                    .toList()
                    .toFlowable()
            }
    }

}