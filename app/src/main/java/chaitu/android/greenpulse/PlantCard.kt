package chaitu.android.greenpulse

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.net.URLEncoder
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

@Composable
fun PlantCard(plant: Plant) {

    var image by remember {
        mutableStateOf("")
    }
    val apiKey = "AIzaSyCHnm1CHj2wgW4uBctpDSUfPqtA63aRJ5Q"
    val cx = "51642fa4910cc4899"
    val query =plant.imageKeyword
   if(image.isEmpty()){
       fetchImageUrl(query, apiKey, cx) { imageUrl ->
           if (imageUrl != null) {
               image=imageUrl
               Log.d("ImageURL", "Fetched: $imageUrl")
               // Load into Image composable or Glide, Coil etc.
           } else {
               Log.e("ImageURL", "Failed to fetch")
           }
       }
   }


    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(1f, animationSpec = tween(1000))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .graphicsLayer { alpha = alphaAnim.value },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column {
            AsyncImage(
                model =image ,
                contentDescription = plant.name,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder_plant), // your placeholder
                error = painterResource(id = R.drawable.placeholder_plant),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(plant.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(Modifier.height(4.dp))
                Text(plant.description)
            }
        }
    }
}


fun fetchImageUrl(query: String, apiKey: String, cx: String, onResult: (String?) -> Unit) {
    val encodedQuery = query.replace(" ", "+")
    val url =
        "https://www.googleapis.com/customsearch/v1?q=$encodedQuery&searchType=image&key=$apiKey&cx=$cx"

    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()

    Thread {
        try {
            val response = client.newCall(request).execute()
            val body = response.body?.string()
            Log.d("body",body.toString())
            val json = JSONObject(body ?: "")
            val items = json.getJSONArray("items")
            val firstImageUrl = items.getJSONObject(0).getString("link")


            onResult(firstImageUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            onResult(null)
        }
    }.start()
}

