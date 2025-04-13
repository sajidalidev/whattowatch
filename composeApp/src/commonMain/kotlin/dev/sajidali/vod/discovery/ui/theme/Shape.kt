package dev.sajidali.vod.discovery.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

// Define the shapes for the application
val AppShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp)
)

// Additional custom shapes that can be used throughout the app
val MovieCardShape = RoundedCornerShape(8.dp)
val ButtonShape = RoundedCornerShape(20.dp)
val SearchBarShape = RoundedCornerShape(24.dp)
val BottomSheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)