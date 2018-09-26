package com.tompee.convoy.feature.splash

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import com.tompee.convoy.ConvoyApplication
import com.tompee.convoy.R
import com.tompee.convoy.base.BaseFragment
import com.tompee.convoy.dependency.component.DaggerSplashComponent
import com.tompee.convoy.feature.navhost.NavigationHostActivity
import kotlinx.android.synthetic.main.fragment_splash.*
import javax.inject.Inject

class SplashFragment : BaseFragment(), SplashView {

    @Inject
    lateinit var splashPresenter: SplashPresenter

    //region SplashFragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progress.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
        splashPresenter.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        splashPresenter.detachView()
    }
    //endregion

    // region BaseFragment
    override fun layoutId() = R.layout.fragment_splash

    override fun setupComponent() {
        DaggerSplashComponent.builder()
                .appComponent(ConvoyApplication[activity!!].component)
                .navigatorComponent(NavigationHostActivity[activity!!].component)
                .build()
                .inject(this)
    }
    //endregion
}