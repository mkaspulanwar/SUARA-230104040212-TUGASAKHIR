package id.antasari.suara_230104040212_tugasakhir.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.antasari.suara_230104040212_tugasakhir.R

// Data Model
data class Proposal(
    val id: Int,
    val institution: String,
    val category: String,
    val title: String,
    val date: String,
    val imageUrl: Int,
    val agreementPercentage: Float,
    val commentCount: Int // Menambahkan field jumlah komentar
)

val dummyProposals = listOf(
    Proposal(1, "Kemendikbud", "Pendidikan", "Guru SD Tak Sengaja Temukan Harta Karun Miliaran di Halaman Sekolah", "4 Jan 2026, 08:30", R.drawable.welcome1, 0.85f, 24),
    Proposal(2, "Kementerian BUMN", "Ekonomi", "PACK Terbitkan OWK Rp3,26 Triliun, Begini Detailnya", "4 Jan 2026, 08:15", R.drawable.welcome2, 0.60f, 12),
    Proposal(3, "Kesehatan RI", "Kesehatan", "Update Vaksinasi Nasional 2026: Capaian Target 99%", "4 Jan 2026, 07:45", R.drawable.welcome3, 0.75f, 56),
    Proposal(4, "Kemenlu", "Internasional", "Chevron Buka Suara Soal Pengambilalihan Minyak di Venezuela", "4 Jan 2026, 07:45", R.drawable.welcome1, 0.45f, 8)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            Surface(shadowElevation = 3.dp) {
                Column(modifier = Modifier.background(Color.White)) {
                    TopAppBar(
                        modifier = Modifier.statusBarsPadding(),
                        title = {
                            Column {
                                Text("Suara Rakyat", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                                    Text("DKI Jakarta", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        },
                        actions = {
                            IconButton(onClick = { }) { Icon(Icons.Outlined.Search, contentDescription = null) }
                            IconButton(onClick = { }) { Icon(Icons.Outlined.Notifications, contentDescription = null) }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                    )
                    FilterSection()
                }
            }
        },
        bottomBar = {
            // Aktifkan jika Anda sudah memiliki BottomNavigationBar
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(paddingValues)
        ) {
            items(dummyProposals) { proposal ->
                NewsCard(proposal)
            }
        }
    }
}

@Composable
fun FilterSection() {
    val categories = listOf("Semua", "Pendidikan", "Kesehatan", "Ekonomi", "Lingkungan")
    var selected by remember { mutableStateOf(0) }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().background(Color.White)
    ) {
        itemsIndexed(categories) { index, title ->
            val isSelected = selected == index
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) Color(0xFF1A73E8) else Color(0xFFF1F3F4))
                    .clickable { selected = index }
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = title,
                    color = if (isSelected) Color.White else Color.DarkGray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun NewsCard(proposal: Proposal) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = proposal.institution,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A73E8)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = proposal.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CategoryBadge(category = proposal.category)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = proposal.date,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Image(
                    painter = painterResource(id = proposal.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { proposal.agreementPercentage },
                    modifier = Modifier.weight(1f).height(4.dp).clip(CircleShape),
                    color = Color(0xFF1A73E8),
                    trackColor = Color(0xFFE8EAED)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("${(proposal.agreementPercentage * 100).toInt()}% Setuju", fontSize = 11.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F3F4))

            // --- BAGIAN TOMBOL AKSI (TERMASUK KOMENTAR) ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActionItem(Icons.Outlined.ThumbUp, "Setuju")

                // Menambahkan Tombol Komentar
                ActionItem(Icons.Outlined.ChatBubbleOutline, proposal.commentCount.toString())

                ActionItem(Icons.Outlined.Share, "Bagikan")
                ActionItem(Icons.Outlined.BookmarkBorder, "Simpan")
                Icon(Icons.Outlined.MoreVert, contentDescription = null, tint = Color.Gray)
            }
        }
    }
}

@Composable
fun CategoryBadge(category: String) {
    val backgroundColor = when (category) {
        "Kesehatan" -> Color(0xFFE8F5E9)
        "Pendidikan" -> Color(0xFFE3F2FD)
        "Ekonomi" -> Color(0xFFFFF3E0)
        else -> Color(0xFFF5F5F5)
    }
    val contentColor = when (category) {
        "Kesehatan" -> Color(0xFF2E7D32)
        "Pendidikan" -> Color(0xFF1565C0)
        "Ekonomi" -> Color(0xFFE65100)
        else -> Color(0xFF616161)
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = category,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}

@Composable
fun ActionItem(icon: ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable { /* Aksi */ }
            .padding(4.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}