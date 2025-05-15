import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.github.mikephil.charting.animation.Easing.EaseInOutCubic

import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.VerticalIndicatorProperties

@Composable
fun ChartSection(title: String, data: List<Pair<Long, Float>>,max:Double) {
    if (data.isNotEmpty()) {
        val lineData = remember(data) {
            listOf(
                Line(
                    label = title,

                    values = data.map { it.second.toDouble() },
                    color = SolidColor(Color(0xFF23af92)),
                    firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .4f),
                    secondGradientFillColor = Color.Transparent,
                    strokeAnimationSpec = tween(2000, easing = androidx.compose.animation.core.EaseInOutCubic),
                    gradientAnimationDelay = 1000,
                    drawStyle = DrawStyle.Stroke(width = 3.dp),
                    curvedEdges = false,


                )
            )
        }

        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {

            LineChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(horizontal = 16.dp),
                data = lineData,
                minValue = 0.0,
                maxValue = max,
                gridProperties = GridProperties( enabled = false),
                labelHelperProperties = LabelHelperProperties(
                    enabled = true,
                    textStyle = TextStyle(color = Color.White)
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    enabled = true,
                    textStyle = TextStyle(color = Color.White)
                ),
                animationMode = AnimationMode.Together(delayBuilder = { it * 200L }),
            )
        }
    }
}

