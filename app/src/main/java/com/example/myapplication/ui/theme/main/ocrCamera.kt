    package com.example.myapplication.ui.theme.main

    import android.Manifest
    import android.content.ContentValues
    import android.content.Context
    import android.content.pm.PackageManager
    import android.database.Cursor
    import android.net.Uri
    import android.os.Build
    import android.os.Bundle
    import android.provider.MediaStore
    import android.util.Log
    import android.util.Size
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import androidx.camera.core.AspectRatio
    import androidx.camera.core.CameraSelector
    import androidx.camera.core.ImageCapture
    import androidx.camera.core.ImageCaptureException
    import androidx.camera.core.Preview
    import androidx.camera.lifecycle.ProcessCameraProvider
    import androidx.core.app.ActivityCompat
    import androidx.core.content.ContextCompat
    import com.example.myapplication.databinding.CameraBinding
    import java.text.SimpleDateFormat
    import java.util.Locale
    import java.util.concurrent.ExecutorService
    import java.util.concurrent.Executors
    import com.example.myapplication.ui.theme.main.http.imageUploader
    import java.io.File

    class ocrCamera : AppCompatActivity() {
        private lateinit var viewBinding: CameraBinding
        private var imageCapture: ImageCapture? = null

        private lateinit var cameraExecutor: ExecutorService
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            viewBinding = CameraBinding.inflate(layoutInflater)
            setContentView(viewBinding.root)

            if (allPermissionsGranted()){
                startCamera()
            }
            else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,REQUEST_CODE_PERMISSIONS)
            }

            viewBinding.imageCaptureBtn.setOnClickListener { takePhoto() }

            cameraExecutor = Executors.newSingleThreadExecutor()
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode ==  REQUEST_CODE_PERMISSIONS) {
                if (allPermissionsGranted()){
                    startCamera()
                }
                else {
                    Toast.makeText(this,
                        "권한 부여 안됨",
                        Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        private  fun takePhoto(){
            val imageCapture = imageCapture ?: return

            val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
            val contentValues = ContentValues().apply{
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
                }
            }

            val outputOptions = ImageCapture.OutputFileOptions
                .Builder(contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues)
                .build()

            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults){
                        val msg = "Photo capture succeeded: ${output.savedUri}"
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, msg)
                        val savedUri = output.savedUri ?: return
                        val realPath = getRealPathFromURI(this@ocrCamera, savedUri)
                        val file = realPath?.let { File(it) }
                        if (file != null && file.exists()) {
                            imageUploader(this@ocrCamera).uploadImage(file)
                        } else {
                            Log.e(TAG, "File not found: $realPath")
                        }
                    }
                }
            )
        }
        private fun startCamera() {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
            cameraProviderFuture.addListener({
                // Used to bind the lifecycle of cameras to the lifecycle owner
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                val resolution = Size(1080, 1920) // 예시 해상도
                //val aspectRatio = AspectRatio.RATIO_4_3 // 예시로 4:3 비율 선택
                val rotation = viewBinding.viewFinder.display.rotation

                // Preview
                val preview = Preview.Builder()
                    .setTargetResolution(resolution) // 미리보기 해상도 설정
                    //.setTargetAspectRatio(aspectRatio) // 미리보기와 같은 비율 설정
                    .setTargetRotation(rotation)
                    .build()
                    .also {
                        it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                    }

                // ImageCapture 사용 설정
                imageCapture = ImageCapture.Builder()
                    .setTargetResolution(resolution) // 미리보기 해상도 설정
                    //.setTargetAspectRatio(aspectRatio) // 미리보기와 같은 비율 설정
                    .setTargetRotation(rotation)
                    .build()

                // Select back camera as a default
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture)

                } catch(exc: Exception) {
                    Log.e(TAG, "Use case binding failed", exc)
                }

            }, ContextCompat.getMainExecutor(this))
        }

        private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                baseContext, it) == PackageManager.PERMISSION_GRANTED
        }

        override fun onDestroy() {
            super.onDestroy()
            cameraExecutor.shutdown()
        }


        companion object {
            private const val TAG = "CameraXApp"
            private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
            private const val REQUEST_CODE_PERMISSIONS = 10
            private val REQUIRED_PERMISSIONS =
                mutableListOf (
                    Manifest.permission.CAMERA
                ).apply {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                        add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        add(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }.toTypedArray()
        }

        fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
            var cursor: Cursor? = null
            try {
                val proj = arrayOf(MediaStore.Images.Media.DATA)
                cursor = context.contentResolver.query(contentUri, proj, null, null, null)
                val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor != null && cursor.moveToFirst()) {
                    return cursor.getString(column_index!!)
                }
            } finally {
                cursor?.close()
            }
            return null
        }
    }