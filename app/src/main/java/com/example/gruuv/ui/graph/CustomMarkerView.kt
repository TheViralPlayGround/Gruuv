package com.example.gruuv.ui.graph

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.example.gruuv.R

class CustomMarkerView(
    context: Context,
    layoutResource: Int
) : MarkerView(context, layoutResource) {

    private val textView: TextView = findViewById(R.id.marker_text)

    // Update marker content
    override fun refreshContent(entry: Entry?, highlight: Highlight?) {
        textView.text = "Effort: ${entry?.y?.toInt()} on ${entry?.x?.toInt() ?: ""}"
        super.refreshContent(entry, highlight)
    }

    // Position the marker correctly
    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }
}
