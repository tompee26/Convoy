package com.tompee.convoy.core.repo.firebase

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.tompee.convoy.core.repo.ProfileImageRepo
import io.reactivex.Single

class FirebaseProfileImageRepo(private val storage: FirebaseStorage) : ProfileImageRepo {

    companion object {
        private const val PROFILE = "profile"
        private const val FILENAME = "profile.jpg"
    }

    override fun uploadProfileImage(email: String, uri: Uri): Single<String> {
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
}