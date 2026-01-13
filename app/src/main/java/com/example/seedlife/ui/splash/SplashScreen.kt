package com.example.seedlife.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seedlife.R
import com.example.seedlife.ui.theme.LightGreenBg
import kotlinx.coroutines.delay

/**
 * Pantalla de splash con ilustración y nombre de la app
 */
@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = rememberInfiniteAnimation(
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2000) // Mostrar splash por 2 segundos
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGreenBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Ilustración grande
            Image(
                painter = painterResource(id = R.drawable.splash_illustration),
                contentDescription = "LifeSeeds",
                modifier = Modifier
                    .size(300.dp)
                    .padding(bottom = 32.dp)
            )

            // Nombre de la app con animación suave
            Text(
                text = "LifeSeeds",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50), // Verde hoja
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(alphaAnim.value)
                    .padding(top = 16.dp)
            )
        }
    }
}
