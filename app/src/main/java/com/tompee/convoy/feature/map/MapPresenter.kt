package com.tompee.convoy.feature.map

import com.tompee.convoy.base.BasePresenterTyped
import com.tompee.convoy.core.navigator.Navigator
import com.tompee.convoy.interactor.MapInteractor
import com.tompee.convoy.model.SchedulerPool

class MapPresenter(mapInteractor: MapInteractor,
                   schedulerPool: SchedulerPool,
                   private val navigator: Navigator) :
        BasePresenterTyped<MapView, MapInteractor>(mapInteractor, schedulerPool) {
//    private lateinit var userMarker: Marker

    override fun onAttachView(view: MapView) {
//        view.permissionChange()
//                .subscribe({
//                    setupMap(view.getGoogleMap())
//                    subscribeToUserChanges(view.getEmail())
//                    subscribeToLocationChanges()
//                    setupMyLocation(view.getGoogleMap(), view.goToMyLocationRequest())
//                })
    }

    override fun onDetachView() {
    }

//    private fun setupMap(googleMap: Observable<GoogleMap>) {
//        addSubscription(googleMap.map {
//            it.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json))
//            val markerOptions = MarkerOptions().position(LatLng(0.0, 0.0)).title("Me")
//            userMarker = it.addMarker(markerOptions)
//            return@map it
//        }.subscribe()
//        )
//    }
//
//    private fun subscribeToUserChanges(email: String) {
//        addSubscription(userInteractor.getUserChanges(email)
//                .compose { observable ->
//                    observable.map { user ->
//                        val decoded = Base64.decode(user.image, Base64.DEFAULT)
//                        val bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
//                        return@map Pair(user, bitmap)
//                    }
//                }
//                .subscribeOn(io)
//                .observeOn(ui)
//                .subscribe({ pair ->
//                    view?.setupProfile(pair.first, pair.second)
//                })
//        )
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun subscribeToLocationChanges() {
//        addSubscription(locationInteractor.getLocationUpdates()
//                .subscribeOn(io)
//                .observeOn(ui)
//                .subscribe({
//                    userMarker.position = LatLng(it.latitude, it.longitude)
//                })
//        )
//    }
//
//    private fun setupMyLocation(map: Observable<GoogleMap>, locationRequest: Observable<Any>) {
//        addSubscription(locationRequest
//                .withLatestFrom(locationInteractor.getLocationUpdates(), map,
//                        Function3<Any, Location, GoogleMap, Pair<Location, GoogleMap>> { _, b, c -> Pair(b, c) })
//                .subscribeOn(ui)
//                .observeOn(ui)
//                .subscribe({
//                    it.second.moveCamera(CameraUpdateFactory.newLatLng(LatLng(it.first.latitude, it.first.longitude)))
//                }))
//    }
}