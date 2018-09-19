package com.tompee.convoy.feature.login

import com.tompee.convoy.base.BaseView
import com.tompee.convoy.interactor.model.User
import io.reactivex.Observable

interface LoginView : BaseView {
    fun loginEmail(): Observable<String>
    fun saveUser(): Observable<User>

    fun showLoginScreen()
    fun moveToNextActivity(email: String)
    fun showProfileSetupScreen(email: String)
}