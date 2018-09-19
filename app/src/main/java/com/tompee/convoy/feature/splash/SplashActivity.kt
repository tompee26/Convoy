package com.tompee.convoy.feature.splash

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import com.tompee.convoy.ConvoyApplication
import com.tompee.convoy.R
import com.tompee.convoy.base.BaseActivity
import com.tompee.convoy.dependency.component.DaggerNavigatorComponent
import com.tompee.convoy.dependency.component.DaggerSplashComponent
import com.tompee.convoy.dependency.module.NavigatorModule
import kotlinx.android.synthetic.main.activity_splash.*
import javax.inject.Inject

class SplashActivity : BaseActivity(), SplashView {

    @Inject
    lateinit var splashPresenter: SplashPresenter

    //region SplashActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progress.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
        splashPresenter.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        splashPresenter.detachView()
    }
    //endregion

    // region BaseActivity
    override fun layoutId() = R.layout.activity_splash

    override fun setupComponent() {
        val navigatorComponent = DaggerNavigatorComponent.builder()
                .navigatorModule(NavigatorModule(this))
                .build()
        DaggerSplashComponent.builder()
                .appComponent(ConvoyApplication[this].component)
                .navigatorComponent(navigatorComponent)
                .build()
                .inject(this)
    }
    //endregion
}