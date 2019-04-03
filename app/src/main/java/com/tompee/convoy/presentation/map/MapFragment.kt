package com.tompee.convoy.presentation.map

import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.tompee.convoy.R
import com.tompee.convoy.databinding.DrawerHeaderBinding
import com.tompee.convoy.databinding.FragmentMapBinding
import com.tompee.convoy.presentation.base.BaseFragment
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class MapFragment : BaseFragment<FragmentMapBinding>() {

    @Inject
    lateinit var factory: MapViewModel.Factory

    override val layoutId: Int = R.layout.fragment_map

    override fun performInject() {
        AndroidSupportInjection.inject(this)
    }

    override fun setupBinding(binding: FragmentMapBinding) {
        val vm = ViewModelProviders.of(this, factory)[MapViewModel::class.java]
        val headerBinding = DrawerHeaderBinding.bind(binding.navigationView.getHeaderView(0))
        headerBinding.lifecycleOwner = this
        headerBinding.viewModel = vm

        binding.navigationView.setNavigationItemSelectedListener {
            binding.drawerLayout.closeDrawers()
            val navigation = findNavController()
            when (it.itemId) {
                R.id.friend_list -> navigation.navigate(R.id.action_mapFragment_to_friendListFragment)
            }
            return@setNavigationItemSelectedListener true
        }
    }
}