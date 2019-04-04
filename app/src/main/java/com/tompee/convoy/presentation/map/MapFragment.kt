package com.tompee.convoy.presentation.map

import android.Manifest
import android.view.Gravity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.tompee.convoy.R
import com.tompee.convoy.databinding.DrawerHeaderBinding
import com.tompee.convoy.databinding.FragmentMapBinding
import com.tompee.convoy.presentation.base.BaseFragment
import dagger.android.support.AndroidSupportInjection
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

class MapFragment : BaseFragment<FragmentMapBinding>(), EasyPermissions.PermissionCallbacks {

    companion object {
        private const val RC_LOCATION_PERM = 124
    }

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

        binding.toolbar.menu.setOnClickListener {
            binding.drawerLayout.openDrawer(Gravity.LEFT)
        }

        binding.navigationView.setNavigationItemSelectedListener {
            binding.drawerLayout.closeDrawers()
            val navigation = findNavController()
            when (it.itemId) {
                R.id.friend_list -> navigation.navigate(R.id.action_mapFragment_to_friendListFragment)
            }
            return@setNavigationItemSelectedListener true
        }
    }

    override fun onResume() {
        super.onResume()
        checkAndRequestPermission()
    }

    @AfterPermissionGranted(RC_LOCATION_PERM)
    private fun checkAndRequestPermission() {
        if (!EasyPermissions.hasPermissions(activity!!, Manifest.permission.ACCESS_FINE_LOCATION)) {
            EasyPermissions.requestPermissions(
                this, getString(R.string.rationale_location_request),
                RC_LOCATION_PERM, Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            val vm = ViewModelProviders.of(this, factory)[MapViewModel::class.java]
            vm.getLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
        activity?.finish()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }
}