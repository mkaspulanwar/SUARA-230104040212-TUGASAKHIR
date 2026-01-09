package id.antasari.suara_230104040212_tugasakhir.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
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
import coil.compose.AsyncImage
import id.antasari.suara_230104040212_tugasakhir.data.remote.AppwriteService
import id.antasari.suara_230104040212_tugasakhir.ui.factory.ViewModelFactory
import id.antasari.suara_230104040212_tugasakhir.ui.viewmodel.PolicyViewModel
import java.text.NumberFormat
import java.util.Locale

// Data Model untuk Komentar (Masih Dummy sementara)
data class Comment(
    val id: Int,
    val author: String,
    val time: String,
    val content: String,
    val likes: Int,
    val isAgreement: Boolean,
    val isAdmin: Boolean = false
)

val dummyComments = listOf(
    Comment(1, "Budi Santoso", "2 jam yang lalu", "Sangat mendukung inisiatif ini! Pembangunan tanggul laut sangat krusial untuk mencegah banjir rob di Jakarta Utara.", 24, true),
    Comment(2, "Siti Aminah", "5 jam yang lalu", "Harus dipastikan dampak lingkungan terhadap nelayan sekitar tidak diabaikan.", 56, false),
    Comment(3, "Admin SUARA", "2 jam yang lalu", "Terima kasih atas masukannya. Tim terkait sedang melakukan studi dampak lingkungan lebih lanjut.", 0, true, isAdmin = true)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PolicyScreen(navController: NavController, policyId: String?) {
    val context = LocalContext.current

    // Inisialisasi ViewModel menggunakan Factory
    val viewModel: PolicyViewModel = viewModel(factory = ViewModelFactory(context))

    // MENGAMBIL STATE DARI VIEWMODEL (Reactive)
    val selectedPolicy by viewModel.selectedPolicy.collectAsState()
    val voteStats by viewModel.voteStats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // STATE BARU: Mengambil status vote user saat ini ("setuju", "tidak", atau null)
    val currentUserVote by viewModel.currentUserVote.collectAsState()

    // Panggil fungsi loadPolicyDetail di ViewModel saat layar dibuka
    LaunchedEffect(policyId) {
        if (policyId != null) {
            viewModel.loadPolicyDetail(policyId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Kebijakan", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share action */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            CommentInputField()
        }
    ) { paddingValues ->
        // Tampilkan Loading jika sedang memuat atau data belum ada
        if (isLoading || selectedPolicy == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1A73E8))
            }
        } else {
            // Data sudah siap, tampilkan UI
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                item {
                    PolicyHeader(
                        institution = selectedPolicy!!.institution,
                        date = selectedPolicy!!.createdAt,
                        title = selectedPolicy!!.title
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item {
                    // Menampilkan Gambar Kebijakan
                    AsyncImage(
                        model = selectedPolicy!!.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
                item { PolicyBody(content = selectedPolicy!!.content) }
                item { Spacer(modifier = Modifier.height(24.dp)) }

                // --- UPDATE: Menampilkan Sentimen Publik dengan Interaksi ---
                item {
                    PublicSentiment(
                        totalVotes = voteStats.totalVotes,
                        agreePercentage = voteStats.agreePercentage,
                        disagreePercentage = voteStats.disagreePercentage,
                        userChoice = currentUserVote, // Kirim status pilihan user
                        onVote = { choice ->
                            // Kirim aksi vote ke ViewModel
                            if (policyId != null) {
                                viewModel.castVote(policyId, choice)
                            }
                        }
                    )
                }
                // -----------------------------------------------------------

                item { Spacer(modifier = Modifier.height(24.dp)) }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Komentar Masyarakat", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                items(dummyComments) { comment ->
                    CommentItem(comment)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun PolicyHeader(institution: String, date: String, title: String) {
    Column {
        Surface(
            color = Color(0xFFE3F2FD),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "STATUS: TERBUKA",
                color = Color(0xFF1565C0),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text("$institution • ${date.take(10)}", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp
        )
    }
}

@Composable
fun PolicyBody(content: String) {
    Text(
        text = content,
        fontSize = 15.sp,
        lineHeight = 26.sp,
        color = Color(0xFF333333)
    )
}

@Composable
fun PublicSentiment(
    totalVotes: Int,
    agreePercentage: Int,
    disagreePercentage: Int,
    userChoice: String?, // Bisa null, "setuju", atau "tidak"
    onVote: (String) -> Unit // Callback saat user klik tombol
) {
    // Format angka ke format Indonesia (contoh: 1.256)
    val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
    val formattedTotal = numberFormat.format(totalVotes)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Sentimen Publik", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Total Dukungan: $formattedTotal", fontSize = 12.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                SentimentCard(
                    isAgreement = true,
                    percentage = agreePercentage,
                    isSelected = userChoice == "setuju", // Cek apakah ini pilihan user
                    onClick = { onVote("setuju") }
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                SentimentCard(
                    isAgreement = false,
                    percentage = disagreePercentage,
                    isSelected = userChoice == "tidak", // Cek apakah ini pilihan user
                    onClick = { onVote("tidak") }
                )
            }
        }
    }
}

@Composable
fun SentimentCard(
    isAgreement: Boolean,
    percentage: Int,
    isSelected: Boolean, // Status apakah card ini dipilih
    onClick: () -> Unit // Fungsi klik
) {
    // Tentukan Warna Dasar
    val baseBackgroundColor = if (isAgreement) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val baseContentColor = if (isAgreement) Color(0xFF2E7D32) else Color(0xFFC62828)

    // Tentukan Visual jika Selected (Border tebal & Background sedikit berbeda opsional)
    val borderColor = if (isSelected) baseContentColor else Color.Transparent
    val borderWidth = if (isSelected) 2.dp else 0.dp

    val icon = if (isAgreement) Icons.Outlined.ThumbUp else Icons.Outlined.ThumbDown

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = baseBackgroundColor),
        border = BorderStroke(borderWidth, borderColor), // Tambahkan Border Stroke
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() } // Jadikan card bisa diklik
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = baseContentColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                if (isAgreement) "Setuju" else "Tidak",
                color = baseContentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(
                "$percentage%",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = baseContentColor
            )

            // Indikator teks kecil jika terpilih (Feedback Visual Tambahan)
            if (isSelected) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = baseContentColor,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "Pilihanmu",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Row {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (comment.isAdmin) Color(0xFF1A73E8) else Color(0xFFF1F3F4)),
            contentAlignment = Alignment.Center
        ) {
            if (comment.isAdmin) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            } else {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(comment.author, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                if (comment.isAdmin) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(color = Color(0xFF1A73E8), shape = RoundedCornerShape(4.dp)) {
                        Text("OFFICIAL", color = Color.White, fontSize = 8.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(comment.time, fontSize = 11.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(comment.content, fontSize = 14.sp, color = Color.DarkGray)

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                Icon(Icons.Outlined.ThumbUp, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(comment.likes.toString(), fontSize = 11.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Balas", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A73E8))
            }
        }
    }
}

@Composable
fun CommentInputField() {
    Surface(shadowElevation = 8.dp, color = Color.White) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Beri tanggapan Anda...", fontSize = 14.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedBorderColor = Color(0xFF1A73E8)
                ),
                maxLines = 3
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1A73E8))
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}