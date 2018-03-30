package com.tompee.convoy.feature.map

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.location.Location
import android.util.Base64
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tompee.convoy.base.BasePresenter
import com.tompee.convoy.interactor.location.LocationInteractor
import com.tompee.convoy.interactor.user.UserInteractor
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3

class MapPresenter(private val locationInteractor: LocationInteractor,
                   private val userInteractor: UserInteractor,
                   private val io: Scheduler,
                   private val ui: Scheduler) : BasePresenter<MapMvpView>() {
    override fun onAttachView(view: MapMvpView) {
        view.permissionChange()
                .subscribe({
                    subscribeToUserChanges(view.getUserId())
                    subscribeToLocationChanges(view.getGoogleMap())
                    setupMyLocation(view.getGoogleMap(), view.goToMyLocationRequest())
                })
    }

    override fun onDetachView() {
    }

    private fun subscribeToUserChanges(userId: String) {
        addSubscription(userInteractor.getUserChanges(userId)
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
    private fun subscribeToLocationChanges(googleMap: Observable<GoogleMap>) {
        addSubscription(locationInteractor.getLocationUpdates()
                .withLatestFrom(googleMap,
                        BiFunction<Location, GoogleMap, Pair<Location, GoogleMap>> { location, map ->
                            Pair(location, map)
                        })
                .subscribeOn(io)
                .observeOn(ui)
                .subscribe({
                    it.second.addMarker(MarkerOptions()
                            .position(LatLng(it.first.latitude, it.first.longitude))
                            .title("Me"))
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