package dev.sajidali.vod.discovery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.sajidali.vod.discovery.ui.theme.ButtonShape
import dev.sajidali.vod.discovery.ui.theme.MovieCardShape
import dev.sajidali.vod.discovery.ui.theme.WhatToWatchTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * A demo component to showcase the theme elements
 */
@Preview
@Composable
fun ThemeDemo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Typography demo
        Text(
            "Typography Demo",
            style = MaterialTheme.typography.h4
        )
        
        Text("Heading 1", style = MaterialTheme.typography.h1)
        Text("Heading 2", style = MaterialTheme.typography.h2)
        Text("Heading 3", style = MaterialTheme.typography.h3)
        Text("Heading 4", style = MaterialTheme.typography.h4)
        Text("Heading 5", style = MaterialTheme.typography.h5)
        Text("Heading 6", style = MaterialTheme.typography.h6)
        Text("Subtitle 1", style = MaterialTheme.typography.subtitle1)
        Text("Subtitle 2", style = MaterialTheme.typography.subtitle2)
        Text("Body 1", style = MaterialTheme.typography.body1)
        Text("Body 2", style = MaterialTheme.typography.body2)
        Text("Button", style = MaterialTheme.typography.button)
        Text("Caption", style = MaterialTheme.typography.caption)
        Text("Overline", style = MaterialTheme.typography.overline)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Colors demo
        Text(
            "Colors Demo",
            style = MaterialTheme.typography.h4
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ColorSwatch("Primary", MaterialTheme.colors.primary)
            ColorSwatch("Secondary", MaterialTheme.colors.secondary)
            ColorSwatch("Background", MaterialTheme.colors.background)
            ColorSwatch("Surface", MaterialTheme.colors.surface)
            ColorSwatch("Error", MaterialTheme.colors.error)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Shapes demo
        Text(
            "Shapes Demo",
            style = MaterialTheme.typography.h4
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShapeDemo("Small", MaterialTheme.shapes.small)
            ShapeDemo("Medium", MaterialTheme.shapes.medium)
            ShapeDemo("Large", MaterialTheme.shapes.large)
            ShapeDemo("Movie Card", MovieCardShape)
            ShapeDemo("Button", ButtonShape)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Theme extras demo
        Text(
            "Theme Extras Demo",
            style = MaterialTheme.typography.h4
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ColorSwatch("Movie Card BG", WhatToWatchTheme.extras.movieCardBackground)
            ColorSwatch("Rating Star", WhatToWatchTheme.extras.ratingStarColor)
            ColorSwatch("Favorite", WhatToWatchTheme.extras.favoriteIconColor)
        }
    }
}

@Composable
private fun ColorSwatch(name: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = name,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun ShapeDemo(name: String, shape: androidx.compose.ui.graphics.Shape) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(shape)
                .background(MaterialTheme.colors.primary)
        )
        Text(
            text = name,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}