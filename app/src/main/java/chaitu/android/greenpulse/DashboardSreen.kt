package chaitu.android.greenpulse

import ChartSection
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.substring
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = viewModel()) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { viewModel.loadDevices(context) }


    val deviceList by viewModel.devices.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        Spacer(modifier = Modifier.height(36.dp))
        HeaderWithLottieQR{
            context.startActivity(Intent(context, QRActivity::class.java))
        }



        LazyColumn(contentPadding = PaddingValues(6.dp)) {
            items(deviceList, key = { it.deviceId }) { device ->
                var expanded by remember { mutableStateOf(false) }
                var renameDialog by remember { mutableStateOf(false) }
                var deleteDialog by remember { mutableStateOf(false) }
                var showThresholdDialog by remember { mutableStateOf(false) }
                var thresholdValue by remember { mutableStateOf(convertSoilToPercent(device.recentSoil.toInt()).toInt().toString()) }
                var newName by remember { mutableStateOf(TextFieldValue(device.nickname)) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { _, dragAmount ->
                                if (dragAmount > 50) renameDialog = true
                                if (dragAmount < -50) deleteDialog = true
                            }
                        }
                ) {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .padding(4.dp),
                        content = {
                            Column(modifier = Modifier.padding(vertical = 16.dp)) {

                                // Title Row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if(device.nickname.length>16)device.nickname.uppercase().substring(0,16)else device.nickname.uppercase(),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    IconButton(onClick = { expanded = !expanded }) {
                                        Icon(
                                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Expand",
                                            tint = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 18.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Colored dot
                                    val currentTime = System.currentTimeMillis()

                                    val isOnline=(currentTime-device.lastSeen)<30000
                                    val pulse = rememberInfiniteTransition()
                                    val scale by pulse.animateFloat(
                                        initialValue = 1f,
                                        targetValue = 1.5f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(800, easing = FastOutSlowInEasing),
                                            repeatMode = RepeatMode.Reverse
                                        ), label = ""
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .graphicsLayer(scale,scale)
                                            .clip(CircleShape)
                                            .background(if (isOnline) Color.Green else Color.Red)
                                    )

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Text(
                                        text = if (isOnline) "Online" else "Offline",
                                        fontSize = 12.sp,
                                        color = if (isOnline) Color.Green else Color.Red
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                // Readings Row aligned with title
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    ReadingBox(
                                        icon = "🌡️",
                                        label = "Temp",
                                        value = "${device.recentTemp} °C",
                                        modifier = Modifier.weight(1f)
                                    )
                                    ReadingBox(
                                        icon = "💧",
                                        label = "Humidity",
                                        value = "${device.recentHumidity} %",
                                        modifier = Modifier.weight(1f)
                                    )
                                    ReadingBox(
                                        icon = "🌱",
                                        label = "Soil",
                                        value = "${convertSoilToPercent(device.recentSoil.toInt())} %",
                                        modifier = Modifier.weight(1f)
                                    )
                                }


                                Spacer(modifier = Modifier.height(12.dp))

                                // Last Watered Timeline
                                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                    Text(
                                        text = "Last Watered Timeline:",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White
                                    )

                                    device.wateredTimestamps.slice(IntRange(0,2)).forEach {

                                        Text("\uD83D\uDCA7 ${formatTimestamp(it)}", style = MaterialTheme.typography.bodySmall, color = Color.White)
                                    }
                                }

                                if (expanded) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                        Text("Real-time Graphs", color = Color.White, style = MaterialTheme.typography.titleMedium)
                                        ChartSection("Temperature", device.tempHistory,50.0)
                                        ChartSection("Humidity", device.humidityHistory,100.0)

                                        ChartSection("Soil", device.soilHistory,1024.0)
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp),

                                        ) {

                                            Button(
                                                onClick = { sendWaterNowCommand(device.deviceId, context = context) },
                                                shape = RoundedCornerShape(16.dp),
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                                            ) {
                                                Text("💧 Water Now", color = Color.White)
                                            }

                                            Button(
                                                onClick = { showThresholdDialog = true }, // Create a dialog later
                                                shape = RoundedCornerShape(16.dp),
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                                            ) {
                                                Text("⚙️ Update Threshold", color = Color.White)
                                            }
                                            Button(
                                                onClick = {  context.startActivity(Intent(context, GeminiActivity::class.java).apply {
                                                    putExtra("deviceId", device.deviceId)
                                                }) }, // Create a dialog later
                                                shape = RoundedCornerShape(16.dp),
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                                            ) {
                                                Text("Smart Suggestions", color = Color.White)
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    )

                }

                // Dialogs
                if (renameDialog) {
                    AlertDialog(
                        onDismissRequest = { renameDialog = false },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.renameDevice(context, device.deviceId, newName.text)
                                renameDialog = false
                            }) { Text("Rename") }
                        },
                        dismissButton = {
                            TextButton(onClick = { renameDialog = false }) { Text("Cancel") }
                        },
                        title = { Text("Rename Device") },
                        text = {
                            OutlinedTextField(
                                value = newName,
                                onValueChange = { newName = it },
                                label = { Text("New Device Name") }
                            )
                        }
                    )
                }

                if (deleteDialog) {
                    AlertDialog(
                        onDismissRequest = { deleteDialog = false },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.deleteDevice(context, device.deviceId)
                                deleteDialog = false
                            }) { Text("Delete") }
                        },
                        dismissButton = {
                            TextButton(onClick = { deleteDialog = false }) { Text("Cancel") }
                        },
                        title = { Text("Delete Device") },
                        text = { Text("Are you sure you want to delete this device?") }
                    )
                }

                if (showThresholdDialog) {
                    AlertDialog(
                        onDismissRequest = { showThresholdDialog = false },
                        confirmButton = {
                            TextButton(onClick = {
                                try{
                                    if(thresholdValue.isNotEmpty())
                                        if(thresholdValue.toFloat()> 100){
                                            Toast.makeText(context,"Value must be between 0 to 100",Toast.LENGTH_SHORT).show()

                                        }else{
                                            updateThreshold(device.deviceId, thresholdValue,context)
                                            showThresholdDialog = false
                                        }
                                }catch (e:Exception){
                                    e.printStackTrace()
                                }


                            }) {
                                Text("Save")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showThresholdDialog = false }) {
                                Text("Cancel")
                            }
                        },
                        title = { Text("Set Soil Moisture Threshold") },
                        text = {
                            OutlinedTextField(
                                value = thresholdValue,
                                onValueChange = { thresholdValue = it

                                                },
                                label = { Text("Enter Threshold", color = MaterialTheme.colorScheme.onSurface) },
                                placeholder = { Text("0-100%", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = TextFieldDefaults.textFieldColors(

                                    containerColor = MaterialTheme.colorScheme.surface,
                                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    cursorColor = MaterialTheme.colorScheme.primary
                                ),
                            )
                        }
                    )
                }

            }
        }
    }
}
fun sendWaterNowCommand(deviceId: String,context: Context) {
    val dbRef = FirebaseDatabase.getInstance().getReference("devices/$deviceId/commands")
    dbRef.child("waterNow").setValue(true).addOnSuccessListener {
       Toast.makeText(context,"Watered Successfully",Toast.LENGTH_SHORT).show()
    }
}
fun updateThreshold(deviceId: String, value: String,context: Context) {
    val dbRef = FirebaseDatabase.getInstance().getReference("devices/$deviceId/commands")
    dbRef.child("threshold").setValue(convertPercentToAnalog(value.toFloat())).addOnSuccessListener {
        Toast.makeText(context,"Threshold Updated Successfully",Toast.LENGTH_SHORT).show()
    }
}

fun convertSoilToPercent(rawValue: Int, dryValue: Int = 1024, wetValue: Int = 300): Float {
    val percent = ((rawValue - dryValue).toFloat() / (wetValue - dryValue) * 100)
    return percent.coerceIn(0f, 100F)
}
fun convertPercentToAnalog(percent: Float, dryValue: Int = 1024, wetValue: Int = 300): Float {
    val clampedPercent = percent.coerceIn(0F, 100F)
    return (dryValue + (wetValue - dryValue) * (clampedPercent / 100.0)).toFloat()
}




@Composable
fun HeaderWithLottieQR(onQrClick: () -> Unit) {

    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("scanner_line.json"))
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
    ) {
        Text(
            text = "🌿 Green Pulse",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface,
        )

        Box(
            modifier = Modifier
                .size(40.dp)
                .clickable { onQrClick() }
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress }
            )
        }
    }
}

