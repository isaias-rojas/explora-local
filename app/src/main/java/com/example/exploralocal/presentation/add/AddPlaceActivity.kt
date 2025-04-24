package com.example.exploralocal.presentation.add


import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.exploralocal.databinding.ActivityAddPlaceBinding
import com.example.exploralocal.domain.model.Place
import com.example.exploralocal.util.Constants
import com.example.exploralocal.util.ImageUtils
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.UUID

@AndroidEntryPoint
class AddPlaceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPlaceBinding
    private val viewModel: AddPlaceViewModel by viewModels()

    private var photoFile: File? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            takePicture()
        } else {
            Toast.makeText(
                this,
                "Se requiere permiso de cámara para tomar fotos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            updatePhotoPreview()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get coordinates from intent
        latitude = intent.getDoubleExtra(Constants.EXTRA_LATITUDE, 0.0)
        longitude = intent.getDoubleExtra(Constants.EXTRA_LONGITUDE, 0.0)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.buttonTakePhoto.setOnClickListener {
            checkCameraPermission()
        }

        binding.buttonSave.setOnClickListener {
            savePlace()
        }

        binding.buttonCancel.setOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.savingState.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Lugar guardado exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.error.observe(this) { errorMsg ->
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
        }
    }

    private fun savePlace() {
        val name = binding.editTextName.text.toString().trim()
        val description = binding.editTextDescription.text.toString().trim()
        val ratingValue = binding.ratingBar.rating

        if (name.isEmpty()) {
            binding.editTextName.error = "El nombre es obligatorio"
            return
        }

        val place = Place(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            rating = ratingValue,
            latitude = latitude,
            longitude = longitude,
            photoPath = photoFile?.absolutePath,
            createdAt = System.currentTimeMillis()
        )

        viewModel.savePlace(place)
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                takePicture()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // Show explanation if needed
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Create file where the photo should go
        photoFile = ImageUtils.createImageFile(this)

        photoFile?.let {
            val photoURI = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                it
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

            try {
                takePictureLauncher.launch(takePictureIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    this,
                    "No se encontró aplicación de cámara",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updatePhotoPreview() {
        photoFile?.let {
            // Load thumbnail for preview
            val bitmap = ImageUtils.getImageThumbnail(it.absolutePath)
            bitmap?.let { bmp ->
                binding.imageViewPhoto.setImageBitmap(bmp)
                binding.imageViewPhoto.visibility = android.view.View.VISIBLE
            }
        }
    }
}