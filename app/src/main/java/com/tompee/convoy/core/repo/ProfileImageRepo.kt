package com.tompee.convoy.core.repo

import android.net.Uri
import io.reactivex.Single

interface ProfileImageRepo {
    fun uploadProfileImage(email: String, uri: Uri): Single<String>
}