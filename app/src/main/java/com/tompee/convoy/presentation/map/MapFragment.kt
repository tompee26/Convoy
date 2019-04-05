package com.tompee.convoy.presentation.map

import android.Manifest
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
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

    private lateinit var map: GoogleMap
    private lateinit var selfMarker: Marker

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
        binding.searchView.apply {
            attachNavigationDrawerToMenuButton(binding.drawerLayout)
            setOnMenuItemClickListener { vm.moveToCurrentLocation() }
        }

        vm.selfMarker.observe(this, Observer {
            if (::selfMarker.isInitialized) {
                selfMarker.remove()
            }
            selfMarker = map.addMarker(it)
        })
        vm.moveLocation.observe(this, Observer {
            map.moveCamera(it)
        })

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync {
            map = it
            checkAndRequestPermission()
        }
    }

    @AfterPermissionGranted(RC_LOCATION_PERM)
    private fun checkAndRequestPermission() {
        if (!EasyPermissions.hasPermissions(activity!!, Manifest.permission.ACCESS_FINE_LOCATION)) {
            EasyPermissions.requestPermissions(
                this, getString(R.string.rationale_location_request),
                RC_LOCATION_PERM, Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            map.apply {
                setMapStyle(MapStyleOptions.loadRawResourceStyle(activity!!, R.raw.map_style))
            }

            ViewModelProviders.of(this, factory)[MapViewModel::class.java].apply {
                getLocation()
            }
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