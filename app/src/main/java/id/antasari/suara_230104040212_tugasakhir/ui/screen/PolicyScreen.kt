package id.antasari.suara_230104040212_tugasakhir.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import id.antasari.suara_230104040212_tugasakhir.R
import id.antasari.suara_230104040212_tugasakhir.data.model.PolicyModel
import id.antasari.suara_230104040212_tugasakhir.data.remote.AppwriteService
import id.antasari.suara_230104040212_tugasakhir.ui.factory.ViewModelFactory
import id.antasari.suara_230104040212_tugasakhir.ui.viewmodel.PolicyViewModel

// Data Model untuk Komentar (Masih Dummy sementara, nanti bisa dihubungkan ke tabel policy_comments)
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
    val service = remember { AppwriteService(context) }
    val viewModel: PolicyViewModel = viewModel(factory = ViewModelFactory(context))

    // State untuk menampung detail kebijakan yang dicari
    var selectedPolicy by remember { mutableStateOf<PolicyModel?>(null) }

    // Ambil data detail saat layar dibuka
    LaunchedEffect(policyId) {
        if (policyId != null) {
            selectedPolicy = service.getPolicyById(policyId)
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
        if (selectedPolicy == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1A73E8))
            }
        } else {
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
                    // Menampilkan Gambar Kebijakan dari Appwrite Storage
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
                item { PublicSentiment() }
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
fun PublicSentiment() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Sentimen Publik", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Total Dukungan: 1.2k", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) { SentimentCard(true, 85) }
            Box(modifier = Modifier.weight(1f)) { SentimentCard(false, 15) }
        }
    }
}

@Composable
fun SentimentCard(isAgreement: Boolean, percentage: Int) {
    val backgroundColor = if (isAgreement) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val contentColor = if (isAgreement) Color(0xFF2E7D32) else Color(0xFFC62828)
    val icon = if (isAgreement) Icons.Outlined.ThumbUp else Icons.Outlined.ThumbDown

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = contentColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(if (isAgreement) "Setuju" else "Tidak", color = contentColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("$percentage%", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = contentColor)
        }
    }
}

// ... Sisanya (CommentItem & CommentInputField) tetap sama seperti sebelumnya ...

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