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
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import id.antasari.suara_230104040212_tugasakhir.R

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
    Comment(1, "Budi Santoso", "2 jam yang lalu", "Sangat mendukung inisiatif ini! Terutama bagian cuti ayah, itu sangat progresif untuk Indonesia. Semoga implementasinya diawasi ketat.", 24, true),
    Comment(2, "Siti Aminah", "5 jam yang lalu", "Perlu revisi di pasal 4 ayat 2. Beban anggaran daerah akan terlalu berat jika subsidi ditanggung penuh oleh PEMDA tanpa bantuan pusat.", 56, false),
    Comment(3, "Admin SUARA", "2 jam yang lalu", "Terima kasih atas masukannya, Bu Siti. Poin ini sudah kami notifikasi ke tim perumus.", 0, true, isAdmin = true)
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PolicyScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Kebijakan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .padding(16.dp)
        ) {
            item { PolicyHeader() }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { PolicyBody() }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { PublicSentiment() }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Komentar (124)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Lihat Semua", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
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

@Composable
fun PolicyHeader() {
    Column {
        Surface(
            color = Color(0xFFE3F2FD),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "STATUS: TERBUKA",
                color = Color(0xFF1565C0),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("12 Okt 2023 - Kementerian Sosial", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "RUU Kesejahteraan Ibu dan Anak",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 32.sp
        )
    }
}

@Composable
fun PolicyBody() {
    Text(
        "Rancangan undang-undang ini bertujuan untuk menjamin hak-hak dasar ibu dan anak dalam masa pertumbuhan, memastikan akses kesehatan yang layak, serta dukungan nutrisi yang memadai bagi keluarga prasejahtera.\n\nFokus utama meliputi pemberian cuti melahirkan yang lebih panjang bagi ibu pekerja, penyediaan ruang laktasi di tempat umum dan tempat kerja, serta subsidi gizi untuk anak usia dini di wilayah 3T (Tertinggal, Terdepan, dan Terluar). Kebijakan ini diharapkan dapat menurunkan angka stunting nasional sebesar 14% dalam 5 tahun ke depan.",
        fontSize = 15.sp,
        lineHeight = 24.sp,
        color = Color.DarkGray
    )
}

@Composable
fun PublicSentiment() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Sentimen Publik", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Total Suara: 15.4k", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            SentimentCard(true, 72)
            SentimentCard(false, 28)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Suara Anda bersifat anonim dan terenkripsi.", fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun SentimentCard(isAgreement: Boolean, percentage: Int) {
    val backgroundColor = if (isAgreement) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val contentColor = if (isAgreement) Color(0xFF2E7D32) else Color(0xFFC62828)
    val icon = if (isAgreement) Icons.Outlined.ThumbUp else Icons.Default.ThumbDown

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier.width(150.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = contentColor)
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isAgreement) "Setuju" else "Tidak Setuju", color = contentColor, fontWeight = FontWeight.Bold)
            }
            Text("$percentage%", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = contentColor)
            LinearProgressIndicator(
                progress = { percentage / 100f },
                color = contentColor,
                trackColor = contentColor.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
            )
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Row {
        if(comment.isAdmin) {
             Image(
                painter = painterResource(id = R.drawable.logo), // Replace with your admin logo drawable
                contentDescription = "Admin",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
        } else {
             Image(
                painter = painterResource(id = R.drawable.ic_launcher_background), // Replace with your user avatar drawable
                contentDescription = comment.author,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
        }


        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(comment.author, fontWeight = FontWeight.Bold)
                if (comment.isAdmin) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(color = Color.Blue, shape = RoundedCornerShape(4.dp)) {
                         Text("OFFICIAL", color = Color.White, fontSize = 8.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(comment.time, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.weight(1f))
                val statusColor = if (comment.isAgreement) Color(0xFF2E7D32) else Color(0xFFC62828)
                val statusText = if (comment.isAgreement) "Setuju" else "Tidak Setuju"
                if (!comment.isAdmin) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = statusColor, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(statusText, color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

            }
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F3F4))
            ) {
                Text(comment.content, modifier = Modifier.padding(12.dp), fontSize = 14.sp)
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                Icon(Icons.Outlined.ThumbUp, contentDescription = "Likes", modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(comment.likes.toString(), fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Reply", modifier = Modifier.size(16.dp), tint = Color.Gray)
                 Spacer(modifier = Modifier.width(4.dp))
                Text("Balas", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}


@Composable
fun CommentInputField() {
    Surface(shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Tulis pendapatmu...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                     unfocusedBorderColor = Color.LightGray,
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PolicyScreenPreview() {
    PolicyScreen(rememberNavController())
}