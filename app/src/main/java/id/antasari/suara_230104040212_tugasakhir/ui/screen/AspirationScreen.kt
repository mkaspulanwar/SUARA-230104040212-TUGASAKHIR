package id.antasari.suara_230104040212_tugasakhir.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import id.antasari.suara_230104040212_tugasakhir.ui.theme.Suara_230104040212_tugasakhirTheme

data class AspirationItem(
    val id: String,
    val title: String,
    val time: String,
    val status: String
)

data class StatusInfo(
    val backgroundColor: Color,
    val textColor: Color,
    val icon: ImageVector?,
    val isDot: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AspirationScreen(navController: NavController) {
    val sampleAspirations = remember {
        listOf(
            AspirationItem("#LP-8821", "Perbaikan Jalan Berlubang di Jl. Sudirman", "2 hours ago", "Menunggu"),
            AspirationItem("#LP-8805", "Lampu Penerangan Jalan Mati di Sektor 9", "1 day ago", "Diproses"),
            AspirationItem("#LP-8750", "Permintaan Tong Sampah Umum Taman Kota", "1 week ago", "Selesai"),
            AspirationItem("#LP-8642", "Pemangkasan Pohon Rawan Tumbang", "2 weeks ago", "Selesai")
        )
    }

    var searchText by remember { mutableStateOf("") }
    val filters = listOf("Semua", "Menunggu", "Diproses", "Selesai")
    var selectedFilter by remember { mutableStateOf("Semua") }

    Scaffold(
        topBar = { AspirationTopAppBar() },
        bottomBar = { BottomNavigationBar(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Handle FAB click */ }) {
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
                    placeholder = { Text("Cari laporan saya...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEach { filter ->
                        Button(
                            onClick = { selectedFilter = filter },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedFilter == filter) MaterialTheme.colorScheme.primary else Color.White,
                                contentColor = if (selectedFilter == filter) Color.White else Color.Black
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(filter)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Aspiration List
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(sampleAspirations.filter {
                        selectedFilter == "Semua" || it.status == selectedFilter
                    }) { aspiration ->
                        AspirationCard(aspiration = aspiration)
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
        title = {
            Text(
                "Aspirasi",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun AspirationCard(aspiration: AspirationItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = { /* TODO: Handle card click */ }
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
                    Text(aspiration.id, color = Color.Gray, fontSize = 12.sp)
                    Text(aspiration.time, color = Color.Gray, fontSize = 12.sp)
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
        "Menunggu" -> StatusInfo(Color(0xFFFFFBEB), Color(0xFFF59E0B), null, true)
        "Diproses" -> StatusInfo(Color(0xFFEFF6FF), Color(0xFF3B82F6), Icons.Default.Refresh, false)
        "Selesai" -> StatusInfo(Color(0xFFF0FDF4), Color(0xFF16A34A), Icons.Default.CheckCircle, false)
        else -> StatusInfo(Color.LightGray, Color.Black, null, false)
    }

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
        Text(text = status, color = statusInfo.textColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}


@Preview(showBackground = true)
@Composable
fun AspirationScreenPreview() {
    Suara_230104040212_tugasakhirTheme {
        AspirationScreen(rememberNavController())
    }
}
