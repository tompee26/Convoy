package com.tompee.convoy.feature.login

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import com.tompee.convoy.R
import com.tompee.convoy.base.BaseActivity
import com.tompee.convoy.dependency.component.DaggerAuthComponent
import com.tompee.convoy.dependency.component.DaggerLoginComponent
import com.tompee.convoy.dependency.module.AuthModule
import com.tompee.convoy.dependency.module.LoginModule
import com.tompee.convoy.feature.profile.ProfileActivity
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : BaseActivity(), LoginActivityMvpView, ViewPager.PageTransformer,
        ViewPager.OnPageChangeListener {

    @Inject
    lateinit var loginPagerAdapter: LoginPagerAdapter

    @Inject
    lateinit var loginActivityPresenter: LoginActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginActivityPresenter.attachView(this)
        viewpager.setPageTransformer(false, this)
        viewpager.addOnPageChangeListener(this)
        viewpager.adapter = loginPagerAdapter
    }

    override fun layoutId(): Int = R.layout.activity_login

    override fun setupComponent() {
        val loginComponent = DaggerLoginComponent.builder()
                .loginModule(LoginModule(this))
                .authComponent(DaggerAuthComponent.builder().authModule(AuthModule(this)).build())
                .build()
        loginComponent.inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        loginActivityPresenter.detachView()
    }

    override fun moveToNextActivity(email: String) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra(ProfileActivity.EMAIL, email)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = loginPagerAdapter.getItem(viewpager.currentItem)
        fragment.onActivityResult(requestCode, resultCode, data)
    }

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
                view.findViewById<View>(R.id.iv_user_icon).translationX = pageWidth * position

                view.findViewById<View>(R.id.passView).translationX = pageWidth * position
                view.findViewById<View>(R.id.tv_pass_label).translationX = pageWidth * position
                view.findViewById<View>(R.id.view_pass_underline).translationX = pageWidth * position
                view.findViewById<View>(R.id.iv_pass_icon).translationX = pageWidth * position

                view.findViewById<View>(R.id.commandButton).translationX = -(pageWidth * position)
            }
            else -> view.alpha = 0f
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        val x = ((viewpager.width * position + positionOffsetPixels) * computeFactor())
        scrollView.scrollTo(x.toInt() + imageView.width / 3, 0)
    }

    override fun onPageSelected(position: Int) {
    }

    private fun computeFactor(): Float {
        return (imageView.width / 2 - viewpager.width) / (viewpager.width *
                loginPagerAdapter.count - 1).toFloat()
    }

}