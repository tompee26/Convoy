package com.tompee.convoy.feature.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.location.Location
import android.util.Base64
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.tompee.convoy.R
import com.tompee.convoy.base.BasePresenter
import com.tompee.convoy.interactor.location.LocationInteractor
import com.tompee.convoy.interactor.user.UserInteractor
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.functions.Function3

class MapPresenter(private val context: Context,
                   private val locationInteractor: LocationInteractor,
                   private val userInteractor: UserInteractor,
                   private val io: Scheduler,
                   private val ui: Scheduler) : BasePresenter<MapMvpView>() {
    private lateinit var userMarker: Marker

    override fun onAttachView(view: MapMvpView) {
        view.permissionChange()
                .subscribe({
                    setupMap(view.getGoogleMap())
                    subscribeToUserChanges(view.getEmail())
                    subscribeToLocationChanges()
                    setupMyLocation(view.getGoogleMap(), view.goToMyLocationRequest())
                })
    }

    override fun onDetachView() {
    }

    private fun setupMap(googleMap: Observable<GoogleMap>) {
        addSubscription(googleMap.map {
            it.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json))
            val markerOptions = MarkerOptions().position(LatLng(0.0, 0.0)).title("Me")
            userMarker = it.addMarker(markerOptions)
            return@map it
        }.subscribe()
        )
    }

    private fun subscribeToUserChanges(email: String) {
        addSubscription(userInteractor.getUserChanges(email)
                .compose { observable ->
                    observable.map { user ->
                        val decoded = Base64.decode(user.image, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                        return@map Pair(user, bitmap)
                    }
                }
                .subscribeOn(io)
                .observeOn(ui)
                .subscribe({ pair ->
                    view?.setupProfile(pair.first, pair.second)
                })
        )
    }

    @SuppressLint("MissingPermission")
    private fun subscribeToLocationChanges() {
        addSubscription(locationInteractor.getLocationUpdates()
                .subscribeOn(io)
                .observeOn(ui)
                .subscribe({
                    userMarker.position = LatLng(it.latitude, it.longitude)
                })
        )
    }

    private fun setupMyLocation(map: Observable<GoogleMap>, locationRequest: Observable<Any>) {
        addSubscription(locationRequest
                .withLatestFrom(locationInteractor.getLocationUpdates(), map,
                        Function3<Any, Location, GoogleMap, Pair<Location, GoogleMap>> { _, b, c -> Pair(b, c) })
                .subscribeOn(ui)
                .observeOn(ui)
                .subscribe({
                    it.second.moveCamera(CameraUpdateFactory.newLatLng(LatLng(it.first.latitude, it.first.longitude)))
                }))
    }
}