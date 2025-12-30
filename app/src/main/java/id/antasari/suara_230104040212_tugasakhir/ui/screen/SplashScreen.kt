package id.antasari.suara_230104040212_tugasakhir.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.antasari.suara_230104040212_tugasakhir.R
import id.antasari.suara_230104040212_tugasakhir.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(key1 = true) {
        delay(3000)
        navController.popBackStack()
        navController.navigate(Screen.Welcome.route)
    }
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF1C74E9)) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "SUARA", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = "CIVIC PARTICIPATION", fontSize = 16.sp, color = Color.White)
                Spacer(modifier = Modifier.height(32.dp))
                CircularProgressIndicator(color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Powered by Government", fontSize = 12.sp, color = Color.White)
            }
        }
    }
}