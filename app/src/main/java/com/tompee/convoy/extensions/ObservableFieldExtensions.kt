package com.tompee.convoy.extensions

import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.databinding.ObservableField
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable

fun <T> ObservableField<T>.toObservable(): Observable<T> {
    return Observable.create { emitter ->
        val callback = object : OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: androidx.databinding.Observable?, propertyId: Int) {
                if (sender == this@toObservable) {
                    emitter.onNext(this@toObservable.get()!!)
                }
            }
        }
        this.addOnPropertyChangedCallback(callback)
        emitter.setCancellable { this.removeOnPropertyChangedCallback(callback) }
    }
}

fun <T> ObservableField<T>.toFlowable(): Flowable<T> {
    return Flowable.create({ emitter ->
        val callback = object : OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: androidx.databinding.Observable?, propertyId: Int) {
                if (sender == this@toFlowable) {
                    emitter.onNext(this@toFlowable.get()!!)
                }
            }
        }
        this.addOnPropertyChangedCallback(callback)
        emitter.setCancellable { this.removeOnPropertyChangedCallback(callback) }
    }, BackpressureStrategy.LATEST)
}