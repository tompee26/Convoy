package com.tompee.convoy.presentation.splash

import android.graphics.Color
import android.graphics.PorterDuff
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.tompee.convoy.R
import com.tompee.convoy.databinding.FragmentSplashBinding
import com.tompee.convoy.presentation.base.BaseFragment
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    override val layoutId: Int = R.layout.fragment_splash

    @Inject
    lateinit var factory: SplashViewModel.Factory

    //region BaseFragment
    override fun performInject() {
        AndroidSupportInjection.inject(this)
    }

    override fun setupBinding(binding: FragmentSplashBinding) {
        val vm = ViewModelProviders.of(this, factory)[SplashViewModel::class.java]
        binding.progress.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)

        val navigator = findNavController()
        vm.state.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                SplashViewModel.AuthenticationState.UNAUTHENTICATED ->
                    navigator.navigate(R.id.action_splashFragment_to_loginFragment)
                SplashViewModel.AuthenticationState.NO_PROFILE -> {
                    navigator.navigate(R.id.action_splashFragment_to_profileSetupFragment)
                }
                else -> {
                    navigator.navigate(R.id.action_splashFragment_to_mapFragment)
                }
            }
        })
    }

    //endregion
}