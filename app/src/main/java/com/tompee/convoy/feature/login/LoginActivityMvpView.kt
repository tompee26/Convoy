package com.tompee.convoy.feature.login

import com.tompee.convoy.base.MvpView
import com.tompee.convoy.interactor.model.User
import io.reactivex.Observable

interface LoginActivityMvpView : MvpView {
    fun loginEmail(): Observable<String>
    fun saveUser(): Observable<User>

    fun showLoginScreen()
    fun moveToNextActivity(userId: String)
    fun showProfileSetupScreen(email: String)
}