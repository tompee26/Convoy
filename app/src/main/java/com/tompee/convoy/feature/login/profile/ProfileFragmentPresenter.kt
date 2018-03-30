package com.tompee.convoy.feature.login.profile

import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Base64
import com.tompee.convoy.base.BasePresenter
import com.tompee.convoy.interactor.model.User
import com.tompee.convoy.interactor.user.UserInteractor
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.functions.Function4
import timber.log.Timber
import java.io.ByteArrayOutputStream


class ProfileFragmentPresenter(private val context: Context,
                               private val userInteractor: UserInteractor,
                               private val io: Scheduler,
                               private val ui: Scheduler) : BasePresenter<ProfileFragmentMvpView>() {
    override fun onAttachView(view: ProfileFragmentMvpView) {
        setupTextFieldsValidation(view)
        setupSaveButtonRequestHandling(view)
    }

    override fun onDetachView() {
    }

    private fun setupTextFieldsValidation(view: ProfileFragmentMvpView) {
        addSubscription(getTextValidation(view.getFirstName()
                .subscribeOn(ui)
                .observeOn(ui))
                .subscribe({
                    if (!it) {
                        view.showFirstNameError()
                    }
                }))
        addSubscription(getTextValidation(view.getLastName()
                .subscribeOn(ui)
                .observeOn(ui))
                .subscribe({
                    if (!it) {
                        view.showLastNameError()
                    }
                }))
        addSubscription(getTextValidation(view.getDisplayName()
                .subscribeOn(ui)
                .observeOn(ui))
                .subscribe({
                    if (!it) {
                        view.showDisplayNameError()
                    }
                }))
        addSubscription(getInputValidation(view.getFirstName(), view.getLastName(), view.getDisplayName())
                .subscribeOn(ui)
                .observeOn(ui)
                .subscribe({
                    view.setSaveButtonState(it)
                })
        )
    }

    private fun setupSaveButtonRequestHandling(view: ProfileFragmentMvpView) {
        addSubscription(view.saveRequest()
                .withLatestFrom(getInputObservable(view.getFirstName(),
                        view.getLastName(),
                        view.getDisplayName(),
                        getEncodedImage(view)),
                        BiFunction<Any, User, User> { _, triple -> triple })
                .subscribeOn(ui)
                .observeOn(ui)
                .subscribe({
                    it.email = view.getEmail()
                    saveProfile(it)
                })
        )
    }

    private fun getEncodedImage(view: ProfileFragmentMvpView): Observable<String> {
        return view.getImage()
                .map { MediaStore.Images.Media.getBitmap(context.contentResolver, it); }
                .map {
                    val bao = ByteArrayOutputStream()
                    it.compress(Bitmap.CompressFormat.PNG, 100, bao) // bmp is bitmap from user image file
                    it.recycle()
                    return@map bao.toByteArray()
                }
                .map { Base64.encodeToString(it, Base64.DEFAULT) }
    }

    private fun getTextValidation(textObservable: Observable<String>): Observable<Boolean> {
        return textObservable
                .map { email ->
                    if (email.isEmpty()) {
                        return@map false
                    }
                    return@map true
                }
    }

    private fun getInputValidation(firstNameObservable: Observable<String>,
                                   lastNameObservable: Observable<String>,
                                   displayNameObservable: Observable<String>): Observable<Boolean> {
        return Observable.combineLatest(getTextValidation(firstNameObservable),
                getTextValidation(lastNameObservable),
                getTextValidation(displayNameObservable),
                Function3<Boolean, Boolean, Boolean, Boolean> { first: Boolean, last: Boolean, display: Boolean ->
                    first && last && display
                })
    }

    private fun getInputObservable(first: Observable<String>,
                                   last: Observable<String>,
                                   display: Observable<String>,
                                   image: Observable<String>): Observable<User> {
        return Observable.combineLatest(first, last, display, image,
                Function4 { a: String, b: String, c: String, d: String -> User(first = a, last = b, display = c, image = d) })
    }

    private fun saveProfile(user: User) {
        userInteractor.saveUser(user)
                .subscribeOn(io)
                .observeOn(ui)
                .subscribe({ it ->
                    view?.saveSuccessful(it)
                }, {
                    Timber.e(it.message)
                })
    }
}