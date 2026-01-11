package id.antasari.suara_230104040212_tugasakhir.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import id.antasari.suara_230104040212_tugasakhir.navigation.Screen
import id.antasari.suara_230104040212_tugasakhir.ui.factory.ViewModelFactory
import id.antasari.suara_230104040212_tugasakhir.ui.viewmodel.AspirationViewModel
import id.antasari.suara_230104040212_tugasakhir.ui.viewmodel.AspirationModel // Pastikan import ini ada
import java.text.SimpleDateFormat // Import untuk format tanggal kompatibel
import java.util.Locale // Import Locale

// Data Class StatusInfo (Untuk Warna Chip)
data class StatusInfo(
    val backgroundColor: Color,
    val textColor: Color,
    val icon: ImageVector?,
    val isDot: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AspirationScreen(navController: NavController) {
    val context = LocalContext.current

    // 1. Inisialisasi ViewModel
    val viewModel: AspirationViewModel = viewModel(factory = ViewModelFactory(context))

    // 2. Ambil Data List dari ViewModel
    val aspirations by viewModel.aspirationList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 3. Panggil Fetch Saat Layar Dibuka
    LaunchedEffect(Unit) {
        viewModel.fetchAspirations()
    }

    var searchText by remember { mutableStateOf("") }
    val filters = listOf("Semua", "pending", "Diproses", "Selesai")
    var selectedFilter by remember { mutableStateOf("Semua") }

    Scaffold(
        topBar = { AspirationTopAppBar() },
        bottomBar = { BottomNavigationBar(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AspirationForm.route) },
                containerColor = Color(0xFF1C74E9),
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Aspiration")
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF3F4F6))
                    .padding(16.dp)
            ) {
                // Search Bar
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Cari laporan...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF1C74E9),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Filter Chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filters) { filter ->
                        val isSelected = selectedFilter == filter
                        val displayText = if (filter == "pending") "Menunggu" else filter

                        Button(
                            onClick = { selectedFilter = filter },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color(0xFF1C74E9) else Color.White,
                                contentColor = if (isSelected) Color.White else Color.Black
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = if(isSelected) 2.dp else 0.dp)
                        ) {
                            Text(displayText, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Loading Indicator
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (aspirations.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Belum ada aspirasi.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        val filteredList = aspirations.filter { item ->
                            val matchesFilter = selectedFilter == "Semua" || item.status == selectedFilter
                            val matchesSearch = item.title.contains(searchText, ignoreCase = true)
                            matchesFilter && matchesSearch
                        }

                        items(filteredList) { aspiration ->
                            AspirationCard(aspiration = aspiration)
                        }

                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AspirationTopAppBar() {
    TopAppBar(
        modifier = Modifier.statusBarsPadding(),
        title = {
            Text(
                "Aspirasi Saya",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            )
        },
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = "Notifications",
                    tint = Color.Gray
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
fun AspirationCard(aspiration: AspirationModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = { /* TODO: Detail */ }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val shortId = if (aspiration.id.length > 5) "#${aspiration.id.takeLast(5).uppercase()}" else "#${aspiration.id}"
                    Text(shortId, color = Color.Gray, fontSize = 12.sp)

                    Text(formatDate(aspiration.createdAt), color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text(aspiration.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 2)
                Spacer(Modifier.height(16.dp))
                StatusChip(status = aspiration.status)
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = "Details",
                tint = Color.Gray,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val statusInfo = when (status) {
        "Diproses" -> StatusInfo(Color(0xFFEFF6FF), Color(0xFF3B82F6), Icons.Default.Refresh, false)
        "Selesai" -> StatusInfo(Color(0xFFF0FDF4), Color(0xFF16A34A), Icons.Default.CheckCircle, false)
        "pending" -> StatusInfo(Color(0xFFFFF7ED), Color(0xFFEA580C), Icons.Default.HourglassEmpty, false)
        else -> StatusInfo(Color.LightGray, Color.Black, null, false)
    }

    val displayStatus = if (status == "pending") "Menunggu" else status

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(statusInfo.backgroundColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        if (statusInfo.isDot) {
            Box(modifier = Modifier.size(8.dp).background(statusInfo.textColor, CircleShape))
        } else if (statusInfo.icon != null) {
            Icon(
                imageVector = statusInfo.icon,
                contentDescription = null,
                tint = statusInfo.textColor,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = displayStatus, color = statusInfo.textColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

// --- PERBAIKAN DI SINI: Menggunakan SimpleDateFormat yang Kompatibel API 24 ---
fun formatDate(dateString: String): String {
    return try {
        // Ambil 10 karakter pertama (YYYY-MM-DD) agar aman diparsing
        // Contoh data Appwrite: 2026-01-12T01:00:00.000+00:00
        val simpleDatePart = if (dateString.length >= 10) dateString.substring(0, 10) else dateString

        // Format Input: yyyy-MM-dd
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = inputFormat.parse(simpleDatePart) ?: return "Baru saja"

        // Format Output: 12 Jan 2026 (Bahasa Indonesia)
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        outputFormat.format(date)
    } catch (e: Exception) {
        "Baru saja"
    }
}