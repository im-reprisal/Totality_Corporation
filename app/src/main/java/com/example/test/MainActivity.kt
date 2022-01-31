package com.example.test
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.test.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var cameraPermission: Array<String>
    private lateinit var storagePermission: Array<String>
    var imageuri: Uri? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main)

        /**
        * Allowing permissions of gallery and camera
         */
        cameraPermission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        /**
        * After clicking on separate buttons we will have to choose whether to select image from camera and gallery
         */
        binding.btnCamera.setOnClickListener { showImagePicDialog(0) }
        binding.btnGallery.setOnClickListener { showImagePicDialog(1) }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showImagePicDialog(a:Int) {
        if (a == 0) {
            if (!checkCameraPermission()) {
                requestCameraPermission()
            } else {
                pickFromCamera()

            }
        } else if (a == 1) {
            if (!checkStoragePermission()) {
                requestStoragePermission()
            } else {
                pickFromCamera()
            }
        }
    }

    /**
    * Checking storage permissions
     */
    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    /**
    * Requesting gallery permission
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST)
    }

    /**
    * checking camera permissions
     */
    private fun checkCameraPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        return result && result1
    }

    /**
    * Requesting camera permission
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST)
    }

    /**
    * Requesting camera and gallery permission if not declared
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST -> {
                if (grantResults.isNotEmpty()) {
                    val camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (camera_accepted && writeStorageAccepted) {
                        pickFromCamera()
                    } else {
                        Toast.makeText(this, "Please Enable Camera and Storage Permissions", Toast.LENGTH_LONG).show()
                    }
                }
            }
            STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty()) {
                    val writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (writeStorageAccepted) {
                        pickFromCamera()
                    } else {
                        Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG)       .show()
                    }
                }
            }
        }
    }

    /**
    * Pick image from gallery or camera
     */
    private fun pickFromCamera() {
        CropImage.activity().start(this@MainActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                assert(result != null)
                val resultUri = result!!.uri
                Picasso.with(this).load(resultUri).into(binding.ivPreviewImage)
            }
        }else if (resultCode== IMAGE_PICK_GALLERY_REQUEST  && resultCode== RESULT_OK&& data!=null){
            val imageUri= data.data
            binding.ivPreviewImage.setImageURI(imageUri)
        }
    }
    companion object {
        private const val CAMERA_REQUEST = 100
        private const val STORAGE_REQUEST = 200
        private const val IMAGE_PICK_GALLERY_REQUEST = 300
    }
}