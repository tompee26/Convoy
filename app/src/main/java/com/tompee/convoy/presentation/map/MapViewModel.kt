package com.tompee.convoy.presentation.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tompee.convoy.domain.interactor.MapInteractor
import com.tompee.convoy.presentation.base.BaseViewModel
import io.reactivex.rxkotlin.plusAssign

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

    init {
        val account = interactor.getAccount().cache()
        subscriptions += account.subscribe { acc ->
            displayName.postValue(acc.displayName)
            email.postValue(acc.email)
            imageUrl.postValue(acc.imageUrl)
        }
    }
}