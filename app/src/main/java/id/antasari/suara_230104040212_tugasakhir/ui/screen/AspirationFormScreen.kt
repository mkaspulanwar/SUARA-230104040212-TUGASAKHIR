package id.antasari.suara_230104040212_tugasakhir.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import id.antasari.suara_230104040212_tugasakhir.ui.factory.ViewModelFactory
import id.antasari.suara_230104040212_tugasakhir.ui.viewmodel.AspirationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AspirationFormScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: AspirationViewModel = viewModel(factory = ViewModelFactory(context))

    // --- State dari ViewModel ---
    val institution by viewModel.institution.collectAsState()
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()

    // --- Daftar Dinas (Hardcoded atau bisa dari DB) ---
    val institutions = listOf(
        "Dinas Pendidikan",
        "Dinas Kesehatan",
        "Dinas Lingkungan Hidup",
        "Dinas Perhubungan",
        "Dinas Pekerjaan Umum (PU)",
        "Dinas Sosial"
    )
    var expanded by remember { mutableStateOf(false) }

    // --- Image Picker Launcher ---
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onImageSelected(uri)
    }

    // --- Efek Saat Sukses ---
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            navController.popBackStack() // Kembali ke halaman list aspirasi
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buat Aspirasi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Button(
                onClick = { viewModel.submitAspiration(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading, // Matikan tombol saat loading
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C74E9))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Kirim Aspirasi", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) // Agar bisa discroll
                .background(Color.White)
        ) {

            // 1. Pilih Dinas
            Text("Lembaga Terkait", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = institution,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Pilih Dinas Tujuan...") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1C74E9),
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    institutions.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                viewModel.onInstitutionChange(item)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Judul Laporan (Baru ditambahkan agar sesuai ViewModel)
            Text("Judul Laporan", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { viewModel.onTitleChange(it) },
                placeholder = { Text("Contoh: Jalan Berlubang di Jl. Ahmad Yani") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1C74E9),
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Deskripsi
            Text("Detail Aspirasi", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { viewModel.onDescriptionChange(it) },
                placeholder = { Text("Jelaskan keluhan atau saran Anda secara rinci, lokasi kejadian, dll...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1C74E9),
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Upload Foto
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Lampiran Foto", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text("(Opsional)", color = Color.Gray, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(
                        BorderStroke(
                            width = 1.dp,
                            color = if (selectedImageUri != null) Color(0xFF1C74E9) else Color.LightGray,
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { launcher.launch("image/*") } // Buka Galeri
                    .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    // Tampilkan Preview Foto
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Tombol Ganti Foto Kecil di pojok
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                        Text("Ketuk untuk ganti foto", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Tampilan Kosong
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Icon(
                            Icons.Default.AddAPhoto,
                            contentDescription = "Tambah Foto",
                            tint = Color(0xFF1C74E9),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tambah Foto Bukti", fontWeight = FontWeight.Bold, color = Color(0xFF1C74E9))
                        Text("Mendukung JPG, PNG", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp)) // Spacer bawah agar tidak tertutup tombol kirim
        }
    }
}