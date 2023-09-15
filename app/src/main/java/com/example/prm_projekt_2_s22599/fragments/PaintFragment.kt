package com.example.prm_projekt_2_s22599.fragments

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.prm_projekt_2_s22599.Navigable
import com.example.prm_projekt_2_s22599.databinding.FragmentPaintBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

const val ARG_EDIT_ID_PAINT = "edit_id"

class PaintFragment : Fragment() {

    private lateinit var binding: FragmentPaintBinding
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private var imageUri: Uri? = null
    private var idEdit: Long = -1L

    private val onTakePhoto: (Boolean) -> Unit = { photography: Boolean ->
        if (!photography) {
            imageUri?.let {
                requireContext().contentResolver.delete(it, null, null)

                if (idEdit != -1L){
                    (activity as? Navigable)?.navigate(Navigable.Destination.Edit,idEdit)
                }else{
                    (activity as? Navigable)?.navigate(Navigable.Destination.Add)
                }
            }
        } else {
            loadPhoto()
        }
    }

    private fun loadPhoto() {
        val imageUri = imageUri ?: return
        requireContext().contentResolver.openInputStream(imageUri)?.use {
            BitmapFactory.decodeStream(it)
        }?.let {
            binding.paintView.background = it
        }
    }

    private fun createImage() {
        val imagesUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "photo_$timeStamp.jpg"

        val ct = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        imageUri = requireContext().contentResolver.insert(imagesUri, ct)
        cameraLauncher.launch(imageUri)
    }

    fun save(){
        val imageUri = imageUri?: return
        val bmp = binding.paintView.generateBitmap()
        requireContext().contentResolver.openOutputStream(imageUri)?.use {
            bmp.compress(Bitmap.CompressFormat.JPEG,90,it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicture(), onTakePhoto)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentPaintBinding.inflate(
            inflater,container,false
        ).also{
            binding  = it
        }.root
        binding.buttonsSave.visibility =  View.INVISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val id = requireArguments().getLong(ARG_EDIT_ID_PAINT, -1)
        if (id != -1L) {
            idEdit = id
            }

        createImage()
        binding.buttonsSave.visibility =  View.VISIBLE
        binding.buttonsSave.setOnClickListener{
            thread {
                save()

                if (idEdit != -1L){
                    (activity as? Navigable)?.navigate(Navigable.Destination.Edit,idEdit)
                }else{
                    (activity as? Navigable)?.navigate(Navigable.Destination.Add)
                }

            }
        }

    }

}