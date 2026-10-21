package com.example.ui.components

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class CurvedHeaderShape(
    private val curveDepthPx: Float = 65f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - curveDepthPx)
            // Smooth downward quadratic bezier arc in the middle
            quadraticTo(
                size.width / 2f,
                size.height + curveDepthPx * 0.35f,
                0f,
                size.height - curveDepthPx
            )
            close()
        }
        return Outline.Generic(path)
    }
}
