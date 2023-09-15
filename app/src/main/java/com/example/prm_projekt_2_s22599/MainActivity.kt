package com.example.prm_projekt_2_s22599

import com.example.prm_projekt_2_s22599.model.Product
import com.example.prm_projekt_2_s22599.geo.LocationService
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.example.prm_projekt_2_s22599.data.ProductDatabase
import com.example.prm_projekt_2_s22599.fragments.*
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity(), Navigable {
    private var REQUEST_PERMISSIONS = 100
    private lateinit var listFragment: ListFragment
    private lateinit var db: ProductDatabase
    private lateinit var products: List<Product>

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkPermissions()) {
            startApp()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS
                ),
                REQUEST_PERMISSIONS
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkPermissions(): Boolean {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
        )

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startApp()
            } else {
                Toast.makeText(this, "Permissions not granted, do it in settings", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startApp() {
        thread {
            db = ProductDatabase.open(this)
            this.products = db.products.getAllSortedByTime().map { entity ->
                Product(
                    entity.id,
                    entity.name,
                    entity.address,
                    entity.path,
                    entity.longitude,
                    entity.latitude
                )
            }
            db.close()
            val serviceIntent = Intent(this, LocationService::class.java)
            serviceIntent.putExtra("products", products as ArrayList<Product>)
            startService(serviceIntent)

            runOnUiThread {
                listFragment = ListFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, listFragment, ListFragment::class.java.name)
                    .commit()
            }
        }
    }

    override fun navigate(to: Navigable.Destination, id: Long?) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
        if (currentFragment !is ListFragment && to == Navigable.Destination.List) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, listFragment, listFragment.javaClass.name)
                .commit()
        }
        else if (to == Navigable.Destination.Add) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container,
                    EditFragment::class.java,
                    Bundle().apply { putLong(ARG_EDIT_ID, id ?: -1L) },
                    EditFragment::class.java.name
                )
                .addToBackStack(EditFragment::class.java.name)
                .commit()
        }
        else if (to == Navigable.Destination.Edit) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container,
                    EditFragment::class.java,
                    Bundle().apply { putLong(ARG_EDIT_ID, id ?: -1L) },
                    EditFragment::class.java.name
                )
                .addToBackStack(EditFragment::class.java.name)
                .commit()
        }
        else if (to == Navigable.Destination.Draw) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container,
                    PaintFragment::class.java,
                    Bundle().apply { putLong(ARG_EDIT_ID_PAINT, id ?: -1L) },
                    PaintFragment::class.java.name
                )
                .addToBackStack(PaintFragment::class.java.name)
                .commit()
        }
    }
}
