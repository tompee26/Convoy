package com.tompee.convoy.feature.map

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.core.view.GravityCompat
import android.view.MenuItem
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.jakewharton.rxbinding2.view.RxView
import com.tompee.convoy.ConvoyApplication
import com.tompee.convoy.R
import com.tompee.convoy.base.BaseActivity
import com.tompee.convoy.dependency.component.DaggerMapComponent
import com.tompee.convoy.dependency.component.DaggerNavigatorComponent
import com.tompee.convoy.dependency.module.MapModule
import com.tompee.convoy.dependency.module.NavigatorModule
import com.tompee.convoy.feature.friend.FriendListActivity
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.toolbar_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

class MapActivity : BaseActivity(), MapView, OnMapReadyCallback, EasyPermissions.PermissionCallbacks {
    companion object {
        private const val RC_LOCATION_PERM = 124
    }

    @Inject
    lateinit var presenter: MapPresenter
    private val mapSubject = BehaviorSubject.create<GoogleMap>()

    // region MapActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        presenter.attachView(this)
        navigationView.setNavigationItemSelectedListener { handleNavigationItemClick(it) }

//        RxView.clicks(search).subscribe({
//            val intent = Intent(this, SearchActivity::class.java)
//            intent.putExtra(SearchActivity.EMAIL, this@MapActivity.intent.getStringExtra(EMAIL))
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            startActivity(intent)
//        })
        checkAndRequestPermission()
    }

    private fun handleNavigationItemClick(item: MenuItem): Boolean {
        drawerLayout.closeDrawers()
        when (item.itemId) {
            R.id.friend_list -> {
                val intent = Intent(this, FriendListActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }
        return true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapSubject.onNext(googleMap)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    // endregion

    //region Permissions
    @AfterPermissionGranted(RC_LOCATION_PERM)
    private fun checkAndRequestPermission() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_location_request),
                    RC_LOCATION_PERM, Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
        finish()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
    //endregion

    //region BaseActivity
    override fun layoutId(): Int = R.layout.activity_map

    override fun setupComponent() {
        val navComponent = DaggerNavigatorComponent.builder()
                .navigatorModule(NavigatorModule(this))
                .build()
        val mapComponent = DaggerMapComponent.builder()
                .navigatorComponent(navComponent)
                .appComponent(ConvoyApplication[this].component)
                .mapModule(MapModule()).build()
        mapComponent.inject(this)
    }
    //endregion

    // region MapView

    override fun getGoogleMap(): Observable<GoogleMap> = mapSubject

    override fun locationRequest(): Observable<Any> = RxView.clicks(myLocation)

    // endregion
}