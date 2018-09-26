package com.tompee.convoy.feature.map

import com.google.android.gms.maps.GoogleMap
import com.tompee.convoy.base.BaseView
import io.reactivex.Observable

interface MapView : BaseView {
    fun getGoogleMap(): Observable<GoogleMap>
    fun locationRequest(): Observable<Any>
}