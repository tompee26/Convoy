package com.tompee.convoy.feature.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import android.view.View
import com.tompee.convoy.ConvoyApplication
import com.tompee.convoy.R
import com.tompee.convoy.base.BaseActivity
import com.tompee.convoy.dependency.component.DaggerLoginComponent
import com.tompee.convoy.dependency.component.DaggerNavigatorComponent
import com.tompee.convoy.dependency.component.LoginComponent
import com.tompee.convoy.dependency.module.LoginModule
import com.tompee.convoy.dependency.module.NavigatorModule
import com.tompee.convoy.feature.login.page.LoginFragment
import com.tompee.convoy.feature.login.page.PageSwitchListener
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : BaseActivity(), ViewPager.PageTransformer,
        ViewPager.OnPageChangeListener, PageSwitchListener {

    @Inject
    lateinit var loginPagerAdapter: LoginPagerAdapter
    lateinit var component: LoginComponent

    //region companion object
    companion object {
        operator fun get(activity: Activity): LoginActivity {
            return activity as LoginActivity
        }
    }
    //endregion

    // region LoginActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    //region BaseActivity
    override fun layoutId(): Int = R.layout.activity_login

    override fun setupComponent() {
        val navComponent = DaggerNavigatorComponent.builder()
                .navigatorModule(NavigatorModule(this))
                .build()
        component = DaggerLoginComponent.builder()
                .loginModule(LoginModule(this))
                .navigatorComponent(navComponent)
                .appComponent(ConvoyApplication[this].component)
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
        if (type == LoginFragment.LOGIN) {
            viewpager.currentItem = 1
        } else {
            viewpager.currentItem = 0
        }
    }
    //endregion
}