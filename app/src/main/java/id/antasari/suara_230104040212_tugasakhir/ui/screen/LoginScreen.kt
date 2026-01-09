package id.antasari.suara_230104040212_tugasakhir.ui.screen

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import id.antasari.suara_230104040212_tugasakhir.R
import id.antasari.suara_230104040212_tugasakhir.ui.factory.ViewModelFactory
import id.antasari.suara_230104040212_tugasakhir.ui.viewmodel.LoginViewModel

// Warna
val BlueMain = Color(0xFF1C74E9)
val TextDark = Color(0xFF1F2937)
val TextGray = Color(0xFF6B7280)
val BorderColor = Color(0xFFE5E7EB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClicked: () -> Unit,
    onForgotPasswordClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onSupportClicked: () -> Unit,
    viewModel: LoginViewModel = viewModel(factory = ViewModelFactory(LocalContext.current))
) {
    val identifier by viewModel.identifier.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // --- 1. KONFIGURASI STATUS BAR (INI TETAP ADA) ---
    // Kode ini memastikan ikon baterai/jam berwarna HITAM (Gelap)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // isAppearanceLightStatusBars = true -> Ikon menjadi Gelap (Hitam)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Background putih ini akan terlihat di Status Bar
    ) {
        // --- 2. BANNER IMAGE ---
        Image(
            painter = painterResource(id = R.drawable.banner_login4),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                // PENTING: statusBarsPadding() dikembalikan.
                // Ini mendorong gambar ke bawah, sehingga area Status Bar menjadi Putih (dari background Box).
                .statusBarsPadding()
                .height(320.dp)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop
        )

        // --- 3. SCROLLABLE CONTENT ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding() // Tambahkan ini agar scroll content juga mulai dari bawah status bar
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {
            // Spacer disesuaikan agar gambar terlihat pas
            Spacer(modifier = Modifier.height(280.dp))

            // --- LOGIN CARD ---
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Indikator garis (Drag Handle)
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.LightGray.copy(alpha = 0.5f))
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Header
                    Text(
                        text = "Selamat Datang",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Suarakan aspirasimu untuk masa depan.",
                        fontSize = 14.sp,
                        color = TextGray,
                        modifier = Modifier.padding(top = 6.dp),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- INPUT SECTION ---
                    OutlinedTextField(
                        value = identifier,
                        onValueChange = { viewModel.onIdentifierChange(it) },
                        label = { Text("NIK / Email") },
                        leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = BlueMain) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BlueMain,
                            focusedLabelColor = BlueMain,
                            cursorColor = BlueMain,
                            unfocusedBorderColor = BorderColor
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = BlueMain) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null, tint = TextGray)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BlueMain,
                            focusedLabelColor = BlueMain,
                            cursorColor = BlueMain,
                            unfocusedBorderColor = BorderColor
                        ),
                        singleLine = true
                    )

                    // Lupa Password
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 24.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = "Lupa Password?",
                            color = BlueMain,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onForgotPasswordClicked() }
                        )
                    }

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    // --- ACTION BUTTONS ---

                    // 1. Masuk
                    Button(
                        onClick = { viewModel.login() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = BlueMain)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Masuk", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 2. Divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
                        Text(
                            text = "Atau masuk dengan",
                            fontSize = 12.sp,
                            color = TextGray,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 3. Google
                    OutlinedButton(
                        onClick = { /* TODO: Google Login */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BorderColor),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_google),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Google",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Footer
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 32.dp)
                    ) {
                        Text("Belum punya akun? ", color = TextGray, fontSize = 14.sp)
                        Text(
                            text = "Daftar Sekarang",
                            color = BlueMain,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { onRegisterClicked() }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // --- 4. TOP ACTION BUTTONS ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SmallCircleButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                description = "Kembali",
                onClick = onBackClicked
            )

            SmallCircleButton(
                icon = Icons.Default.HeadsetMic,
                description = "Hubungi Support",
                onClick = onSupportClicked
            )
        }
    }
}

// Komponen Helper untuk Tombol Bulat
@Composable
fun SmallCircleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.9f),
        shadowElevation = 4.dp,
        modifier = Modifier.size(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = TextDark,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}