package com.tompee.convoy.feature.map

import android.graphics.Bitmap
import com.google.android.gms.maps.GoogleMap
import com.tompee.convoy.base.BaseView
import com.tompee.convoy.interactor.model.User
import io.reactivex.Completable
import io.reactivex.Observable

interface MapMvpView : BaseView {
    fun getEmail(): String
    fun getGoogleMap(): Observable<GoogleMap>
    fun goToMyLocationRequest(): Observable<Any>
    fun permissionChange(): Completable

    fun setupProfile(user: User, bitmap: Bitmap)
}