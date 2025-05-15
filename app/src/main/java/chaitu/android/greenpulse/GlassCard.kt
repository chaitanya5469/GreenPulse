package chaitu.android.greenpulse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val premiumCardColors = listOf(
        Color(0xFF1C1C1E), // deep gray black
        Color(0xFF2A2A40), // night blue
        Color(0xFF3D3D5C), // muted indigo
        Color(0xFF4B3832), // chocolate smoke
        Color(0xFF2F2F2F), // charcoal
        Color(0xFF202020), // near black
        Color(0xFF1F2633), // navy muted
        Color(0xFF242424), // dark metal
        Color(0xFF2C2C3A), // graphite
        Color(0xFF282C34),  // IntelliJ style dark

    )


    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = premiumCardColors.random()
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFD700).copy(alpha = 0.25f), // gold glow
                            Color(0xFF00FFFF).copy(alpha = 0.10f), // teal shimmer
                            Color.Transparent
                        ),
                        start = Offset.Zero,
                        end = Offset.Infinite
                    )
                )
                .padding(20.dp)
        ) {

            content()
        }
    }
}