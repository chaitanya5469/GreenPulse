package chaitu.android.greenpulse

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
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun QRScannerScreen(
    onScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var scanned by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    if (isError) {
        ErrorDialog(
            text = "⚠\uFE0F Invalid QR Code",
            show = isError,
            onDismiss = { isError = false }
        )
    }


    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black)
    ) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val analyzer = ImageAnalysis.Builder().build().also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(ctx), QRAnalyzer { result ->
                        if (!scanned) {
                            if (!isValidQR(result)) {
                                isError=true
                                scanned=false

                            }else{
                                isError=false
                                scanned = true
                                onScanned(result)

                            }

                        }
                    })
                }

                cameraProviderFuture.get().bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, analyzer
                )

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Dark overlay with transparent scan box
        // ✨ Custom Overlay with transparent center using Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(Color.Black.copy(alpha = 0.7f))
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(
                    size.width / 2 - 125.dp.toPx(),
                    size.height / 2 - 125.dp.toPx()
                ),
                size = Size(250.dp.toPx(), 250.dp.toPx()),
                cornerRadius = CornerRadius(32.dp.toPx(), 32.dp.toPx()),
                blendMode = BlendMode.Clear // Punch a transparent hole
            )
        }

        // Transparent scanning area
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.Center)
                .border(3.dp, Color.White, RoundedCornerShape(32.dp))
                .background(Color.Transparent)
        )

        // Scan animation

        val composition by rememberLottieComposition(LottieCompositionSpec.Asset("scanner_line.json"))
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .width(180.dp)
                .align(Alignment.Center)
        )

        // Instruction
        Text(
            text = "Align the QR code within the frame",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
        )
    }
}
private fun isValidQR(data: String): Boolean {
    return data.startsWith("device:") && data.length > 8
}