package com.example.gruuv.ui.graph

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

@Composable
fun LineChartMPAndroid(
    data: List<Entry>,
    labels: List<String>,
    yAxisMin: Float = 0f,
    yAxisMax: Float? = null,
    legendLabel: String,
    tooltipText: (Int) -> String
) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            LineChart(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                xAxis.apply {
                    setDrawGridLines(false)
                    textColor = Color.parseColor("#5A5A5A")
                    textSize = 16f
                    valueFormatter = IndexAxisValueFormatter(labels.distinct())
                    labelRotationAngle = -30f
                    granularity = 1f
                }

                axisLeft.apply {
                    setDrawGridLines(false)
                    textColor = Color.parseColor("#5A5A5A")
                    textSize = 16f
                    axisMinimum = yAxisMin
                    axisMaximum = (yAxisMax ?: data.maxOfOrNull { it.y } ?: 10f) + 2f
                }

                axisRight.isEnabled = false
                description.apply {
                    isEnabled = true
                    text = "Get your Gruuv on"
                    textSize = 10f
                }
                legend.apply {
                    isEnabled = true
                    form = Legend.LegendForm.LINE
                    textSize = 14f
                    verticalAlignment = Legend.LegendVerticalAlignment.TOP
                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    orientation = Legend.LegendOrientation.HORIZONTAL
                    setDrawInside(false)
                    yOffset = 10f
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        update = { lineChart ->
            val dataSet = LineDataSet(data, legendLabel).apply {
                color = Color.RED
                lineWidth = 2f
                circleRadius = 6f
                valueTextSize = 10f
                setDrawFilled(true)
                fillAlpha = 100
                enableDashedLine(10f, 5f, 0f)
                setCircleColor(Color.parseColor("#FFFFFF"))
                circleHoleColor = Color.parseColor("#FF0000")
                circleHoleRadius = 4f
                val gradientDrawable = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(Color.parseColor("#FF6347"), Color.parseColor("#FFE4E1"))
                )
                fillDrawable = gradientDrawable
            }

            val lineData = LineData(dataSet)
            lineChart.data = lineData
            lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    val pointIndex = e?.x?.toInt() ?: return
                    Toast.makeText(context, tooltipText(pointIndex), Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected() {}
            })
            lineChart.animateXY(2000, 2000, Easing.EaseInOutBounce)
            lineChart.invalidate()
        }
    )
}
