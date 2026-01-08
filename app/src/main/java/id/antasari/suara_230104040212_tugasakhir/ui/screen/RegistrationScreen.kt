package id.antasari.suara_230104040212_tugasakhir.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign // Import Penting untuk Rata Tengah
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.antasari.suara_230104040212_tugasakhir.R
import id.antasari.suara_230104040212_tugasakhir.ui.factory.ViewModelFactory
import id.antasari.suara_230104040212_tugasakhir.ui.theme.Suara_230104040212_tugasakhirTheme
import id.antasari.suara_230104040212_tugasakhir.ui.viewmodel.RegistrationViewModel
import kotlinx.coroutines.launch

// Definisi warna
val SuaraBlue = Color(0xFF1C74E9)
val SuaraWhite = Color(0xFFFFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onRegisterSuccess: () -> Unit,
    onLoginClicked: () -> Unit,
    onTermsClicked: () -> Unit,
    onPrivacyPolicyClicked: () -> Unit,
    viewModel: RegistrationViewModel = viewModel(factory = ViewModelFactory(LocalContext.current))
) {
    // Collect State
    val nik by viewModel.nik.collectAsState()
    val nikError by viewModel.nikError.collectAsState()
    val nama by viewModel.nama.collectAsState()
    val namaError by viewModel.namaError.collectAsState()
    val email by viewModel.email.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val password by viewModel.password.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val confirmPasswordError by viewModel.confirmPasswordError.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    // Local State
    var agreedToTerms by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    // Effects
    LaunchedEffect(successMessage, errorMessage) {
        successMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.onMessagesShown()
            }
        }
        errorMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it, withDismissAction = true)
                viewModel.onMessagesShown()
            }
        }
    }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            onRegisterSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = SuaraWhite
    ) { paddingValues ->
        // Ganti Column utama dengan Box agar bisa overlap
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SuaraWhite) // Base background
        ) {

            // --- LAYER 1: BANNER IMAGE ---
            // Gambar ditempel di paling atas (TopCenter)
            // Tingginya diatur fix (misal 280dp) agar tertutup sebagian oleh form
            Image(
                painter = painterResource(id = R.drawable.logo_banner_login), // Pastikan resource ini benar
                contentDescription = "Registration Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp) // Tinggi gambar
                    .align(Alignment.TopCenter)
            )

            // --- LAYER 2: SCROLLABLE FORM ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Spacer Transparan
                // Ini memberikan jarak dari atas agar gambar terlihat sebelum form dimulai
                Spacer(modifier = Modifier.height(220.dp))

                // White Card / Surface
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp), // Lengkungan sudut atas
                    color = SuaraWhite,
                    shadowElevation = 0.dp // Flat style seperti login
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 32.dp), // Padding bawah agar scroll tidak mentok
                        horizontalAlignment = Alignment.CenterHorizontally // RATA TENGAH
                    ) {

                        // Handle Bar (Garis kecil di atas seperti bottom sheet)
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(4.dp)
                                .background(Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // Header Text (Rata Tengah)
                        Text(
                            text = "Buat Akun Baru",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            ),
                            textAlign = TextAlign.Center, // Teks Center
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Lengkapi data diri Anda untuk mulai berpartisipasi.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray
                            ),
                            textAlign = TextAlign.Center, // Teks Center
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // NIK Input
                        SuaraInputField(
                            value = nik,
                            onValueChange = viewModel::onNikChange,
                            label = "Nomor Induk Kependudukan",
                            placeholder = "16 Digit NIK",
                            icon = Icons.Outlined.Badge,
                            errorMessage = nikError,
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Nama Input
                        SuaraInputField(
                            value = nama,
                            onValueChange = viewModel::onNamaChange,
                            label = "Nama Lengkap",
                            placeholder = "Nama Sesuai KTP",
                            icon = Icons.Outlined.Person,
                            errorMessage = namaError,
                            imeAction = ImeAction.Next
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email Input
                        SuaraInputField(
                            value = email,
                            onValueChange = viewModel::onEmailChange,
                            label = "Alamat Email",
                            placeholder = "email@anda.com",
                            icon = Icons.Outlined.Email,
                            errorMessage = emailError,
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password Input
                        SuaraPasswordField(
                            value = password,
                            onValueChange = viewModel::onPasswordChange,
                            label = "Kata Sandi",
                            errorMessage = passwordError,
                            imeAction = ImeAction.Next
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Confirm Password Input
                        SuaraPasswordField(
                            value = confirmPassword,
                            onValueChange = viewModel::onConfirmPasswordChange,
                            label = "Konfirmasi Kata Sandi",
                            errorMessage = confirmPasswordError,
                            imeAction = ImeAction.Done,
                            onDone = {
                                focusManager.clearFocus()
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Terms Checkbox
                        TermsAndConditionsCheckbox(
                            isChecked = agreedToTerms,
                            onCheckedChange = { agreedToTerms = it },
                            onTermsClicked = onTermsClicked,
                            onPrivacyClicked = onPrivacyPolicyClicked
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Action Button
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.register()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = agreedToTerms && !isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SuaraBlue,
                                contentColor = Color.White,
                                disabledContainerColor = SuaraBlue.copy(alpha = 0.5f)
                            ),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Text(
                                    text = "Daftar Sekarang",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Footer
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 32.dp)
                        ) {
                            Text(
                                text = "Sudah punya akun? ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            Text(
                                text = "Masuk disini",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = SuaraBlue,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier
                                    .clickable { onLoginClicked() }
                                    .padding(4.dp)
                            )
                        }

                        SecurityBadge()
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

// --- REUSABLE COMPONENTS ---
// (Tidak ada perubahan signifikan di sini, hanya memastikan konsistensi)

@Composable
fun SuaraInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    errorMessage: String?,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(icon, contentDescription = null, tint = if (errorMessage != null) MaterialTheme.colorScheme.error else Color.Gray) },
            isError = errorMessage != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SuaraBlue,
                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                cursorColor = SuaraBlue,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun SuaraPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errorMessage: String?,
    imeAction: ImeAction,
    onDone: (() -> Unit)? = null
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(label, color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = if (errorMessage != null) MaterialTheme.colorScheme.error else Color.Gray) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = { onDone?.invoke() }
            ),
            singleLine = true,
            trailingIcon = {
                val image = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                val description = if (passwordVisible) "Sembunyikan password" else "Tampilkan password"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description, tint = Color.Gray)
                }
            },
            isError = errorMessage != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SuaraBlue,
                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                cursorColor = SuaraBlue,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun TermsAndConditionsCheckbox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onTermsClicked: () -> Unit,
    onPrivacyClicked: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(24.dp).padding(top = 2.dp),
            colors = CheckboxDefaults.colors(
                checkedColor = SuaraBlue,
                uncheckedColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.width(12.dp))

        val annotatedString = buildAnnotatedString {
            append("Saya menyetujui ")
            pushStringAnnotation(tag = "terms", annotation = "terms")
            withStyle(style = SpanStyle(color = SuaraBlue, fontWeight = FontWeight.SemiBold)) {
                append("Syarat & Ketentuan")
            }
            pop()
            append(" serta ")
            pushStringAnnotation(tag = "policy", annotation = "policy")
            withStyle(style = SpanStyle(color = SuaraBlue, fontWeight = FontWeight.SemiBold)) {
                append("Kebijakan Privasi")
            }
            pop()
            append(".")
        }

        ClickableText(
            text = annotatedString,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.Gray,
                lineHeight = 20.sp,
                textAlign = TextAlign.Start
            ),
            onClick = { offset ->
                annotatedString.getStringAnnotations(tag = "terms", start = offset, end = offset)
                    .firstOrNull()?.let { onTermsClicked() }
                annotatedString.getStringAnnotations(tag = "policy", start = offset, end = offset)
                    .firstOrNull()?.let { onPrivacyClicked() }
            }
        )
    }
}

@Composable
fun SecurityBadge() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Security",
            tint = Color.Gray,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Data Anda dienkripsi secara End-to-End",
            color = Color.Gray,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegistrationScreenPreview() {
    Suara_230104040212_tugasakhirTheme {
        RegistrationScreen({}, {}, {}, {})
    }
}