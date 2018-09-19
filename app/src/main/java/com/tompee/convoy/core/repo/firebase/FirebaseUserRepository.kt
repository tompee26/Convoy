package com.tompee.convoy.core.repo.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.tompee.convoy.core.repo.UserEntity
import com.tompee.convoy.core.repo.UserRepository
import io.reactivex.Completable
import io.reactivex.Single

class FirebaseUserRepository(private val db: FirebaseFirestore) : UserRepository {
    companion object {
        private const val PROFILE = "profile"
    }

    override fun getUser(email: String): Single<UserEntity> {
        return Single.create<UserEntity> { emitter ->
            val docRef = db.collection(PROFILE).document(email)
            docRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    emitter.onSuccess(documentSnapshot.toObject(UserEntity::class.java)!!)
                } else {
                    emitter.onError(Throwable("User not found"))
                }
            }.addOnFailureListener { emitter.onError(Throwable(it.message)) }
        }
    }

    override fun getUserImage(email: String): Single<String> {
        return getUser(email)
                .map { it.image }
    }

    override fun saveUser(userEntity: UserEntity): Completable {
        return Completable.create { emitter ->
            db.collection(PROFILE).document(userEntity.email).set(userEntity)
            /* Offline does not trigger completion event.
            That's why we will assume that all writes are successful */
            emitter.onComplete()
        }
    }
}