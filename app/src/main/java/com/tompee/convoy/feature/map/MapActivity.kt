package com.tompee.convoy.feature.map

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.jakewharton.rxbinding2.view.RxView
import com.tompee.convoy.ConvoyApplication
import com.tompee.convoy.R
import com.tompee.convoy.base.BaseActivity
import com.tompee.convoy.dependency.component.DaggerMapComponent
import com.tompee.convoy.dependency.module.MapModule
import com.tompee.convoy.feature.friend.FriendListActivity
import com.tompee.convoy.feature.search.SearchActivity
import com.tompee.convoy.interactor.model.User
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.CompletableSubject
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.drawer_header.*
import kotlinx.android.synthetic.main.toolbar_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

class MapActivity : BaseActivity(), MapMvpView, OnMapReadyCallback, EasyPermissions.PermissionCallbacks {
    companion object {
        const val EMAIL = "email"
        private const val RC_LOCATION_PERM = 124
    }

    @Inject
    lateinit var presenter: MapPresenter
    private val mapSubject = BehaviorSubject.create<GoogleMap>()
    private val permissionSubject = CompletableSubject.create()

    // region View/Presenter setup
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        presenter.attachView(this)
        RxView.clicks(search).subscribe({
            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra(SearchActivity.EMAIL, this@MapActivity.intent.getStringExtra(EMAIL))
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        })
        navigationView.setNavigationItemSelectedListener { handleNavigationItemClick(it) }
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun handleNavigationItemClick(item: MenuItem): Boolean {
        drawerLayout.closeDrawers()
        when (item.itemId) {
            R.id.friend_list -> {
                val intent = Intent(this, FriendListActivity::class.java)
                intent.putExtra(FriendListActivity.EMAIL, this@MapActivity.intent.getStringExtra(EMAIL))
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }
        return true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapSubject.onNext(googleMap)
        checkAndRequestPermission()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun layoutId(): Int = R.layout.activity_map

    override fun setupComponent() {
        val mapComponent = DaggerMapComponent.builder()
                .appComponent(ConvoyApplication[this].component)
                .mapModule(MapModule()).build()
        mapComponent.inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(RC_LOCATION_PERM)
    private fun checkAndRequestPermission() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_location),
                    RC_LOCATION_PERM, Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            permissionSubject.onComplete()
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
    // endregion

    // region Observables
    override fun getEmail(): String {
        return intent.getStringExtra(EMAIL)
    }

    override fun getGoogleMap(): Observable<GoogleMap> {
        return mapSubject
    }

    override fun goToMyLocationRequest(): Observable<Any> {
        return RxView.clicks(myLocation)
    }

    override fun permissionChange(): Completable {
        return permissionSubject
    }
    // endregion

    override fun setupProfile(user: User, bitmap: Bitmap) {
        emailView.text = user.email
        displayNameView.text = user.display
        portrait.setImageBitmap(bitmap)
    }
}