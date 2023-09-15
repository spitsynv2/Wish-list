package com.example.prm_projekt_2_s22599.geo

import com.example.prm_projekt_2_s22599.model.Product
import WishLocationListener
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.location.LocationManager


class LocationService : Service() {
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: WishLocationListener

    private var products: ArrayList<Product>? = null

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (intent != null && intent.hasExtra("products")) {
            products = intent.getParcelableArrayListExtra("products")
            if (products != null) {
                locationListener = WishLocationListener(products!!)
                locationListener.setContext(this)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
            }
            return START_STICKY
        } else {
            return START_NOT_STICKY
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(locationListener)
    }
}
