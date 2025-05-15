package chaitu.android.greenpulse

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import chaitu.android.greenpulse.ui.theme.GreenPulseTheme
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.database.FirebaseDatabase


class QRActivity : ComponentActivity() {
    private var showSuccess by mutableStateOf(false)

    private var scannedDeviceId by mutableStateOf("")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val systemUiController = rememberSystemUiController()
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = true
            )

            var hasPermission by remember { mutableStateOf(false) }
            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->
                hasPermission = granted
            }

            LaunchedEffect(Unit) {
                permissionLauncher.launch(Manifest.permission.CAMERA);
            }



            GreenPulseTheme {
                when {
                    showSuccess -> SuccessScreen(
                        deviceId = scannedDeviceId,
                        onDone = {startActivity(Intent(this,MainActivity::class.java)); finish() }
                    )
                    hasPermission -> QRScannerScreen { deviceId ->
                        scannedDeviceId = deviceId
                        registerDevice(deviceId)

                    }
                    else -> PermissionDeniedScreen()
                }
            }
        }
    }



    private fun extractDeviceId(data: String): String {
        return data.removePrefix("device:")
    }


    private fun registerDevice(id: String) {

        val deviceId = extractDeviceId(id)
        val db = FirebaseDatabase.getInstance().reference
        val deviceRef = db.child("Devices").child(deviceId)

        deviceRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                deviceRef.setValue(true).addOnSuccessListener {
                    showSuccess = true


                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to register device!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Device already registered!", Toast.LENGTH_SHORT).show()
                showSuccess = true
            }
        }
    }
}






