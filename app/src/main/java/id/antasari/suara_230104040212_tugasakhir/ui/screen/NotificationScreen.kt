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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

enum class NotificationType {
    POLICY,
    ASPIRATION,
    WARNING,
    VOTE,
    PROFILE
}

data class Notification(
    val id: Int,
    val type: NotificationType,
    val title: String,
    val description: String,
    val time: String,
    val isRead: Boolean
)

val newNotifications = listOf(
    Notification(1, NotificationType.POLICY, "Kebijakan Baru Transportasi", "Pemerintah kota telah merilis jadwal baru untuk bus TransKota efektif mulai besok pagi.", "2 jam yang lalu", false),
    Notification(2, NotificationType.ASPIRATION, "Aspirasi Anda Ditinjau", "Usulan perbaikan jalan di Jl. Sudirman sedang dalam tahap verifikasi lapangan oleh dinas...", "4 jam yang lalu", false)
)

val olderNotifications = listOf(
    Notification(3, NotificationType.WARNING, "Peringatan Cuaca Ekstrem", "BMKG memprediksi hujan lebat disertai angin kencang sore ini. Harap berhati-hati saat...", "Kemarin", true),
    Notification(4, NotificationType.VOTE, "Pemungutan Suara Dibuka", "Vote untuk desain taman kota baru sekarang dibuka. Partisipasi Anda menentukan wajah kota...", "2 hari yang lalu", true),
    Notification(5, NotificationType.PROFILE, "Profil Berhasil Diperbarui", "Data kependudukan anda telah berhasil disinkronisasi dengan database pusat.", "3 hari yang lalu", true)
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifikasi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    TextButton(onClick = { /*TODO*/ }) {
                        Text("Tandai dibaca", color = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                SectionHeader("TERBARU")
            }
            items(newNotifications) { notification ->
                NotificationItem(notification = notification, modifier = Modifier.padding(bottom = 8.dp))
            }
            item {
                SectionHeader("TERDAHULU")
            }
            items(olderNotifications) { notification ->
                NotificationItem(notification = notification, modifier = Modifier.padding(bottom = 8.dp))
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier.padding(vertical = 12.dp)
    )
}


@Composable
fun NotificationItem(notification: Notification, modifier: Modifier = Modifier) {
    val iconData = when (notification.type) {
        NotificationType.POLICY -> Pair(Icons.Default.Campaign, Color(0xFFE3F2FD))
        NotificationType.ASPIRATION -> Pair(Icons.Default.CheckCircle, Color(0xFFE8F5E9))
        NotificationType.WARNING -> Pair(Icons.Default.Warning, Color(0xFFFFF3E0))
        NotificationType.VOTE -> Pair(Icons.Default.HowToVote, Color(0xFFF3E5F5))
        NotificationType.PROFILE -> Pair(Icons.Default.Person, Color(0xFFE0E0E0))
    }
    val iconColor = when (notification.type) {
        NotificationType.POLICY -> Color(0xFF1A73E8)
        NotificationType.ASPIRATION -> Color(0xFF2E7D32)
        NotificationType.WARNING -> Color(0xFFE65100)
        NotificationType.VOTE -> Color(0xFF6A1B9A)
        NotificationType.PROFILE -> Color(0xFF616161)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconData.second),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconData.first,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.description,
                fontSize = 13.sp,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = notification.time,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        if (!notification.isRead) {
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(8.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
    NotificationScreen(navController = rememberNavController())
}
