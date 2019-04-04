package com.tompee.convoy.presentation.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tompee.convoy.domain.interactor.MapInteractor
import com.tompee.convoy.presentation.base.BaseViewModel
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber

class MapViewModel(private val interactor: MapInteractor) : BaseViewModel() {

    class Factory(private val interactor: MapInteractor) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(interactor) as T
        }
    }

    val displayName = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()
    val selfMarker = MutableLiveData<MarkerOptions>()
    val moveLocation = MutableLiveData<CameraUpdate>()

    init {
        val account = interactor.getAccount().cache()
        subscriptions += account.subscribe { acc ->
            displayName.postValue(acc.displayName)
            email.postValue(acc.email)
            imageUrl.postValue(acc.imageUrl)
        }
    }

    fun getLocation() {
        subscriptions += interactor.getLocation()
            .subscribe {
                selfMarker.postValue(
                    MarkerOptions()
                        .position(LatLng(it.latitude, it.longitude))
                        .title("Me")
                )
            }
    }

    fun moveToCurrentLocation() {
        subscriptions += interactor.getLocation()
            .firstOrError()
            .subscribe(
                { moveLocation.postValue(CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude))) },
                Timber::e
            )
    }
}