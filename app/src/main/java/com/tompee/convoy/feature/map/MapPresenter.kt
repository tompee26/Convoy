package com.tompee.convoy.feature.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.tompee.convoy.R
import com.tompee.convoy.base.BasePresenter
import com.tompee.convoy.interactor.location.LocationInteractor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MapPresenter(private val locationInteractor: LocationInteractor,
                   private val context: Context) : BasePresenter<MapMvpView>() {
    private lateinit var googleMap: GoogleMap
    private var locationSubscription: Disposable? = null

    fun configure(googleMap: GoogleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json))
        this.googleMap = googleMap
    }

    @SuppressLint("MissingPermission")
    fun startLocationSubscription() {
        locationSubscription = locationInteractor.getLocationUpdates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ location: Location ->
                    Timber.i("new location: " + location.toString())
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(location.latitude,
                            location.longitude)))
                    googleMap.isMyLocationEnabled = true
                    googleMap.uiSettings.isMyLocationButtonEnabled = true

                })
        addSubscription(locationSubscription!!)
    }

    fun stopLocationSubscription() {
        locationSubscription?.apply {
            dispose()
        }
        locationSubscription = null
    }
}