package id.antasari.suara_230104040212_tugasakhir.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.antasari.suara_230104040212_tugasakhir.R
import id.antasari.suara_230104040212_tugasakhir.ui.factory.ViewModelFactory
import id.antasari.suara_230104040212_tugasakhir.ui.viewmodel.LoginViewModel

// Palet Warna Sesuai Permintaan
val BlueMain = Color(0xFF1C74E9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClicked: () -> Unit,
    onForgotPasswordClicked: () -> Unit,
    viewModel: LoginViewModel = viewModel(factory = ViewModelFactory(LocalContext.current))
) {
    val identifier by viewModel.identifier.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var rememberMe by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            onLoginSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // 1. BANNER DI ATAS
        Image(
            painter = painterResource(id = R.drawable.logo_banner_login), // Pastikan file banner ada di drawable
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            contentScale = ContentScale.Crop
        )

        // 2. KONTEN UTAMA (OVERLAP KE BANNER)
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 220.dp), // Membuat konten "naik" ke banner
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Greeting Sesuai Permintaan
                Text(
                    text = "Selamat Datang",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Input NIK / Email
                OutlinedTextField(
                    value = identifier,
                    onValueChange = { viewModel.onIdentifierChange(it) },
                    label = { Text("NIK / Email") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BlueMain) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BlueMain,
                        focusedLabelColor = BlueMain
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Input Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BlueMain) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BlueMain,
                        focusedLabelColor = BlueMain
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Remember Me & Forgot Password
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { rememberMe = !rememberMe }
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(checkedColor = BlueMain)
                        )
                        Text("Ingat saya", fontSize = 14.sp, color = Color.DarkGray)
                    }
                    TextButton(onClick = { onForgotPasswordClicked() }) {
                        Text("Lupa Password?", color = BlueMain, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                if (errorMessage != null) {
                    Text(errorMessage!!, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tombol Login Utama
                Button(
                    onClick = { viewModel.login() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = BlueMain)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Masuk", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 3. SOCIAL LOGIN SECTION
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color(0xFFEEEEEE))
                    Text(" Atau masuk dengan ", fontSize = 12.sp, color = Color.Gray)
                    HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color(0xFFEEEEEE))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SocialIcon(R.drawable.logo_google) // Pastikan file ikon ada di drawable
                    SocialIcon(R.drawable.logo_facebook)
                    SocialIcon(R.drawable.logo_x)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Footer Daftar Sekarang
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Belum punya akun? ", color = Color.Gray, fontSize = 14.sp)
                    Text(
                        text = "Daftar Sekarang",
                        color = BlueMain,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onRegisterClicked() }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SocialIcon(iconRes: Int) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(Color(0xFFF5F5F5))
            .clickable { /* Aksi Social Login */ }
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(id = iconRes), contentDescription = null)
    }
}