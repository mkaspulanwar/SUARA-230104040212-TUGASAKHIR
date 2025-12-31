package id.antasari.suara_230104040212_tugasakhir.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import id.antasari.suara_230104040212_tugasakhir.R
import id.antasari.suara_230104040212_tugasakhir.navigation.Screen

@OptIn(ExperimentalPagerApi::class)
@Composable
fun WelcomeScreen(navController: NavController) {
    val pagerState = rememberPagerState()
    val pages = listOf(
        WelcomePage(
            image = R.drawable.welcome1,
            title = "Suarakan Aspirasimu",
            description = "Sampaikan keluhan dan saran langsung ke pemerintah dengan mudah"
        ),
        WelcomePage(
            image = R.drawable.welcome2,
            title = "Kebijakan Publik",
            description = "Ikut menentukan kebijakan di wilayahmu melalui sistem voting transparan"
        ),
        WelcomePage(
            image = R.drawable.welcome3,
            title = "Diskusi Warga",
            description = "Berbagi pemikiran dan berinteraksi dengan warga lain di fitur Stream"
        )
    )

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF1C74E9)) {
        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                count = pages.size,
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) {
                WelcomeContent(page = pages[it])
            }
            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
                activeColor = Color.White,
                inactiveColor = Color.White.copy(alpha = 0.4f)
            )
            if (pagerState.currentPage == pages.size - 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = { navController.navigate(Screen.Login.route) }) {
                        Text(text = "Masuk", color = Color.White)
                    }
                    Button(
                        onClick = { navController.navigate(Screen.Login.route) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF9A825))
                    ) {
                        Text(text = "Mulai Sekarang", color = Color.White)
                    }
                }
            } else {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    TextButton(onClick = { navController.navigate(Screen.Login.route) }, modifier = Modifier.align(Alignment.CenterEnd)) {
                        Text(text = "Lewati", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeContent(page: WelcomePage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(id = page.image), contentDescription = page.title)
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = page.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = page.description, fontSize = 16.sp, textAlign = TextAlign.Center, color = Color.White)
    }
}

data class WelcomePage(val image: Int, val title: String, val description: String)
