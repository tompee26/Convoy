package com.tompee.convoy.presentation.profile

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tompee.convoy.domain.interactor.ProfileInteractor
import com.tompee.convoy.extensions.toFlowable
import com.tompee.convoy.presentation.base.BaseViewModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class ProfileSetupViewModel(private val interactor: ProfileInteractor) : BaseViewModel() {
    class Factory(private val interactor: ProfileInteractor) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ProfileSetupViewModel(interactor) as T
        }
    }

    enum class InputState {
        FIRST_NAME_EMPTY,
        FIRST_NAME_OK,
        LAST_NAME_EMPTY,
        LAST_NAME_OK,
        DISPLAY_NAME_EMPTY,
        DISPLAY_NAME_OK,
        OK
    }

    val imageUrl = ObservableField<String>()
    val firstName = ObservableField<String>()
    val lastName = ObservableField<String>()
    val displayName = ObservableField<String>()

    val inputState = MutableLiveData<InputState>()
    val saveState = MutableLiveData<Boolean>()
    val progressVisible = MutableLiveData<Boolean>()
    val message = MutableLiveData<String>()
    val exit = MutableLiveData<Unit>()

    init {
        fun getFirstNameValidation(): Flowable<InputState> {
            return firstName.toFlowable()
                .map {
                    when {
                        it.isEmpty() -> InputState.FIRST_NAME_EMPTY
                        else -> InputState.FIRST_NAME_OK
                    }
                }
        }

        fun getLastNameValidation(): Flowable<InputState> {
            return lastName.toFlowable()
                .map {
                    when {
                        it.isEmpty() -> InputState.LAST_NAME_EMPTY
                        else -> InputState.LAST_NAME_OK
                    }
                }
        }

        fun getDisplayNameValidation(): Flowable<InputState> {
            return displayName.toFlowable()
                .map {
                    when {
                        it.isEmpty() -> InputState.DISPLAY_NAME_EMPTY
                        else -> InputState.DISPLAY_NAME_OK
                    }
                }
        }

        subscriptions += getFirstNameValidation()
            .debounce(1, TimeUnit.SECONDS)
            .subscribe(inputState::postValue)

        subscriptions += getLastNameValidation()
            .debounce(1, TimeUnit.SECONDS)
            .subscribe(inputState::postValue)

        subscriptions += getDisplayNameValidation()
            .debounce(1, TimeUnit.SECONDS)
            .subscribe(inputState::postValue)

        subscriptions += Flowables.combineLatest(
            getFirstNameValidation().startWith(InputState.FIRST_NAME_EMPTY),
            getLastNameValidation().startWith(InputState.LAST_NAME_EMPTY),
            getDisplayNameValidation().startWith(InputState.DISPLAY_NAME_EMPTY)
        ) { first, last, display ->
            when {
                first != InputState.FIRST_NAME_OK -> first
                last != InputState.LAST_NAME_OK -> last
                display != InputState.DISPLAY_NAME_OK -> display
                else -> InputState.OK
            }
        }
            .map { it == InputState.OK }
            .subscribe(saveState::postValue)
    }

    fun save() {
        subscriptions += Completable.fromAction { progressVisible.postValue(true) }
            .andThen(
                interactor.saveAccount(
                    firstName.get()!!,
                    lastName.get()!!,
                    displayName.get()!!,
                    imageUrl.get() ?: ""
                )
            )
            .doFinally { progressVisible.postValue(false) }
            .subscribeOn(Schedulers.io())
            .subscribe({ exit.postValue(Unit) }) { message.postValue(it.message) }
    }
}