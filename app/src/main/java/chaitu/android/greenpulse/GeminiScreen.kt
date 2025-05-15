package chaitu.android.greenpulse


import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject



@Composable
fun GeminiActivityUI(deviceId: String, context: Context) {
    val soilOptions = listOf("Sandy", "Loamy", "Clay", "Silty")
    var selectedSoil by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var plantList by remember { mutableStateOf<List<Plant>>(emptyList()) }
    val temp = remember { mutableStateOf<Float?>(null) }
    val moisture = remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        // Fetch latest temp & moisture from Firebase
        val dbRef = Firebase.database.reference.child("devices").child(deviceId).child("data")
        dbRef.child("temperature").orderByKey().limitToLast(1)
            .get().addOnSuccessListener {
                val latest = it.children.firstOrNull()?.getValue(Float::class.java)
                temp.value = latest
            }
        dbRef.child("soil").orderByKey().limitToLast(1)
            .get().addOnSuccessListener {
                val latest = it.children.firstOrNull()?.getValue(Int::class.java)
                moisture.value = latest
            }
    }
    if(isLoading){
        LoadingScreen()
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)) {
        Spacer(modifier = Modifier.height(20.dp))
        Text("Choose your soil type:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Box {
            OutlinedTextField(
                value = selectedSoil,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {Icon(
                    imageVector = if (isDropdownExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.clickable { isDropdownExpanded = !isDropdownExpanded }
                )},

                label = { Text("Soil Type") }
            )
            DropdownMenu(expanded = isDropdownExpanded, onDismissRequest = { isDropdownExpanded = false }, modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)) {
                soilOptions.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            selectedSoil = it
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        val coroutineScope = rememberCoroutineScope()
        Button(
            onClick = {
                if (selectedSoil.isNotEmpty() && temp.value != null && moisture.value != null) {
                    isLoading = true
                    coroutineScope.launch {
                        generateSuggestions(
                            selectedSoil,
                            temp.value!!,
                            moisture.value!!,
                            onResult = {
                                plantList=it
                                isLoading = false
                            }
                        )
                    }

                } else {
                    Toast.makeText(context, "Please wait for data & select soil", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Suggest Plants")
        }

        Spacer(Modifier.height(24.dp))
        LazyColumn {
            items(plantList,key = null){
                PlantCard(it)
            }
        }

    }
}

data class Plant(
    val name: String,
    val description: String,
    val imageKeyword:String,

)

fun generateSuggestions(
    soilType: String,
    temperature: Float,
    moisture: Int,
    onResult: (List<Plant>) -> Unit
) {
    val prompt = """
You are a plant advisor. Based on these conditions:
- Soil type: $soilType
- Temperature: $temperature°C
- Soil moisture: $moisture

Suggest 3–5 suitable plants. For each plant, include:
1. Name
2. One-line description
3. A Key word for fetching image url from google custom search.

Return the output in this JSON format:

[
  {
    "name": "Tomato",
    "description": "Grows well in warm, loamy soil.",
    "keyword": "Any keyword"
  },
  ...
]
""".trimIndent()




    val apiKey = "AIzaSyCHnm1CHj2wgW4uBctpDSUfPqtA63aRJ5Q"

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val generativeModel = GenerativeModel(
                modelName = "gemini-2.5-pro-exp-03-25",
                apiKey = apiKey
            )

            val response = generativeModel.generateContent(prompt)
            val responseText = response.text ?: ""

            Log.d("Gemini", "Raw response: $responseText")


            val plantList = mutableListOf<Plant>()

            try {
                // Clean and parse the response
                val cleaned = responseText
                    .replace("```json", "")
                    .replace("```", "")
                    .trim()

                val jsonArray = JSONArray(cleaned)

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    plantList.add(
                        Plant(
                            name = obj.getString("name"),
                            description = obj.getString("description"),
                            imageKeyword = obj.getString("keyword")
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("Gemini", "JSON Parsing error: ${e.message}")
            }

            withContext(Dispatchers.Main) {
                onResult(plantList)
            }

        } catch (e: Exception) {
            Log.e("Gemini", "Error calling API: ${e.message}")
            withContext(Dispatchers.Main) {
                onResult(emptyList())
            }
        }
    }
}
@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LottieAnimationView()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Finding the best plants for your garden...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun LottieAnimationView() {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("loading.json"))
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.size(180.dp)
    )
}



