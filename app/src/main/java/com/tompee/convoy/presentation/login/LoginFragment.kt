package com.tompee.convoy.presentation.login

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.tompee.convoy.R
import com.tompee.convoy.databinding.FragmentLoginBinding
import com.tompee.convoy.presentation.base.BaseFragment
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class LoginFragment : BaseFragment<FragmentLoginBinding>(), ViewPager.OnPageChangeListener,
    ViewPager.PageTransformer {

    @Inject
    lateinit var loginPagerAdapter: LoginPagerAdapter

    @Inject
    lateinit var factory: LoginViewModel.Factory

    override val layoutId: Int = R.layout.fragment_login

    override fun performInject() {
        AndroidSupportInjection.inject(this)
    }

    override fun setupBinding(binding: FragmentLoginBinding) {
        val vm = ViewModelProviders.of(activity!!, factory)[LoginViewModel::class.java]
        binding.viewpager.apply {
            addOnPageChangeListener(this@LoginFragment)
            setPageTransformer(false, this@LoginFragment)
            adapter = loginPagerAdapter
        }
        vm.switch.observe(this, Observer {
            if (binding.viewpager.currentItem == 0) {
                binding.viewpager.currentItem = 1
            } else {
                binding.viewpager.currentItem = 0
            }
        })
        vm.message.observe(this, Observer {
            Snackbar.make(
                activity?.findViewById(android.R.id.content)!!,
                it, Snackbar.LENGTH_LONG
            ).show()
        })
        val navigator = findNavController()
        vm.profileState.observe(this, Observer {
            when (it) {
                LoginViewModel.ProfileState.NO_PROFILE -> {
                    navigator.navigate(R.id.action_loginFragment_to_profileSetupFragment)
                }
                else -> {
                    navigator.navigate(R.id.action_loginFragment_to_mapFragment)
                }
            }
        })
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        fun computeFactor(): Float =
            (binding.imageView.width / 2 - binding.viewpager.width) / (binding.viewpager.width *
                    loginPagerAdapter.count - 1).toFloat()

        val x = ((binding.viewpager.width * position + positionOffsetPixels) * computeFactor())
        binding.scrollView.scrollTo(x.toInt() + binding.imageView.width / 3, 0)
    }

    override fun onPageSelected(position: Int) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = loginPagerAdapter.getItem(binding.viewpager.currentItem)
        fragment.onActivityResult(requestCode, resultCode, data)
    }
}