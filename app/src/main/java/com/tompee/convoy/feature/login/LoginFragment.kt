package com.tompee.convoy.feature.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.tompee.convoy.ConvoyApplication
import com.tompee.convoy.R
import com.tompee.convoy.base.BaseFragment
import com.tompee.convoy.dependency.component.DaggerLoginComponent
import com.tompee.convoy.dependency.component.LoginComponent
import com.tompee.convoy.dependency.module.LoginModule
import com.tompee.convoy.feature.login.page.LoginPageFragment
import com.tompee.convoy.feature.login.page.PageSwitchListener
import com.tompee.convoy.feature.navhost.NavigationHostActivity
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

class LoginFragment : BaseFragment(), ViewPager.PageTransformer,
        ViewPager.OnPageChangeListener, PageSwitchListener {

    @Inject
    lateinit var loginPagerAdapter: LoginPagerAdapter
    lateinit var component: LoginComponent

    //region companion object
    companion object {
        operator fun get(fragment: Fragment): LoginFragment {
            return fragment as LoginFragment
        }
    }
    //endregion

    // region LoginActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewpager.addOnPageChangeListener(this)
        viewpager.setPageTransformer(false, this)
        viewpager.adapter = loginPagerAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = loginPagerAdapter.getItem(viewpager.currentItem)
        fragment.onActivityResult(requestCode, resultCode, data)
    }
    //endregion

    //region BaseFragment
    override fun layoutId(): Int = R.layout.fragment_login

    override fun setupComponent() {
        component = DaggerLoginComponent.builder()
                .loginModule(LoginModule(this, childFragmentManager))
                .appComponent(ConvoyApplication[activity!!].component)
                .navigatorComponent(NavigationHostActivity[activity!!].component)
                .build()
        component.inject(this)
    }
    //endregion

    //region PageTransformer
    override fun transformPage(view: View, position: Float) {
        val pageWidth = view.width
        when {
            position < -1 -> view.alpha = 0f
            position <= 1 -> {
                view.findViewById<View>(R.id.tv_app_name).translationX = -(pageWidth * position)
                view.findViewById<View>(R.id.tv_app_subtitle).translationX = -(pageWidth * position)

                view.findViewById<View>(R.id.userView).translationX = pageWidth * position
                view.findViewById<View>(R.id.tv_user_label).translationX = pageWidth * position
                view.findViewById<View>(R.id.view_user_underline).translationX = pageWidth * position
                view.findViewById<View>(R.id.profileImage).translationX = pageWidth * position

                view.findViewById<View>(R.id.passView).translationX = pageWidth * position
                view.findViewById<View>(R.id.tv_pass_label).translationX = pageWidth * position
                view.findViewById<View>(R.id.view_pass_underline).translationX = pageWidth * position
                view.findViewById<View>(R.id.iv_pass_icon).translationX = pageWidth * position

                view.findViewById<View>(R.id.commandButton).translationX = -(pageWidth * position)
            }
            else -> view.alpha = 0f
        }
    }

    private fun computeFactor(): Float {
        return (imageView.width / 2 - viewpager.width) / (viewpager.width *
                loginPagerAdapter.count - 1).toFloat()
    }
    //endregion

    //region PageChangeListener
    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        val x = ((viewpager.width * position + positionOffsetPixels) * computeFactor())
        scrollView.scrollTo(x.toInt() + imageView.width / 3, 0)
    }

    override fun onPageSelected(position: Int) {
    }
    //endregion

    //region PageSwitchListener
    override fun onPageSwitch(type: Int) {
        if (type == LoginPageFragment.LOGIN) {
            viewpager.currentItem = 1
        } else {
            viewpager.currentItem = 0
        }
    }
    //endregion
}