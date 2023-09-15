package com.example.prm_projekt_2_s22599.fragments

import ProductImageAdapter
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prm_projekt_2_s22599.Navigable
import com.example.prm_projekt_2_s22599.data.ProductDatabase
import com.example.prm_projekt_2_s22599.data.model.ProductEntity
import com.example.prm_projekt_2_s22599.databinding.FragmentEditBinding
import java.util.*
import kotlin.concurrent.thread

const val ARG_EDIT_ID = "edit_id"

class EditFragment : Fragment() {

    private var imageUri: String? = null
    private lateinit var binding: FragmentEditBinding
    private lateinit var adapter: ProductImageAdapter
    private lateinit var db: ProductDatabase
    private var latitude: Double? = 0.0
    private var longitude: Double? = 0.0
    private var product: ProductEntity? = null
    private lateinit var locationManager: LocationManager
    private var locationListener: LocationListener? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = ProductDatabase.open(requireContext())
        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentEditBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ProductImageAdapter()
        adapter.loadImages()

        binding.btnTakePhoto.setOnClickListener {
            (activity as? Navigable)?.navigate(Navigable.Destination.Draw, product?.id)
        }

        binding.deleteBtn.visibility = View.GONE

        val id = requireArguments().getLong(ARG_EDIT_ID, -1)
        if (id != -1L) {
            thread {
                product = db.products.getProduct(id)
                latitude = product?.latitude
                longitude = product?.longitude
                imageUri =  product?.path

                requireActivity().runOnUiThread {
                    binding.productName.setText(product?.name ?: "")
                    binding.productAddress.setText(product?.address ?: "")
                    adapter.setSelection(imageUri)
                    binding.deleteBtn.visibility = View.VISIBLE
                }

            }
        }

        binding.images.apply {
            adapter = this@EditFragment.adapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        binding.save.setOnClickListener {

            val product = product?.copy(
                name = binding.productName.text.toString(),
                address = binding.productAddress.text.toString(),
                path = adapter.selectedImagePath,
                longitude = this.longitude!!,
                latitude = this.latitude!!
            ) ?: ProductEntity(
                name = binding.productName.text.toString(),
                address = binding.productAddress.text.toString(),
                path =  adapter.selectedImagePath,
                longitude = this.longitude!!,
                latitude = this.latitude!!
            )

            this.product = product

            thread {
                Log.d(TAG, "productHasCoordinates:" + ((product.longitude + product.latitude)!=0.0).toString())
            }

            thread {
                db.products.addProduct(product)
                (activity as? Navigable)?.navigate(Navigable.Destination.List)
            }

        }

        binding.mapImg.setOnClickListener {
            locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    locationManager.removeUpdates(this)

                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                    if (addresses?.isNotEmpty() == true) {
                        val address = addresses[0]
                        latitude = location.latitude
                        longitude = location.longitude
                        val addressText = address.getAddressLine(0)
                        binding.productAddress.setText(addressText)
                    }
                }
            }
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,
                locationListener as LocationListener, null)
        }

        binding.deleteBtn.setOnClickListener{
            thread {
                db.products.remove(id)
                (activity as? Navigable)?.navigate(Navigable.Destination.List)
            }
        }
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        locationListener?.let {
            locationManager.removeUpdates(it)
        }
    }


}
