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
import androidx.compose.material.icons.outlined.CheckCircle
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
import id.antasari.suara_230104040212_tugasakhir.data.model.PolicyComment
import id.antasari.suara_230104040212_tugasakhir.ui.factory.ViewModelFactory
import id.antasari.suara_230104040212_tugasakhir.ui.viewmodel.PolicyViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PolicyScreen(navController: NavController, policyId: String?) {
    val context = LocalContext.current

    // Inisialisasi ViewModel menggunakan Factory
    val viewModel: PolicyViewModel = viewModel(factory = ViewModelFactory(context))

    // --- STATE DARI VIEWMODEL ---
    val selectedPolicy by viewModel.selectedPolicy.collectAsState()
    val voteStats by viewModel.voteStats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentUserVote by viewModel.currentUserVote.collectAsState()

    // State Komentar
    val comments by viewModel.comments.collectAsState()
    val commentText by viewModel.commentText.collectAsState()
    val isPosting by viewModel.isPostingComment.collectAsState()

    // Load data saat layar dibuka
    LaunchedEffect(policyId) {
        if (policyId != null) {
            viewModel.loadPolicyDetail(policyId)
        }
    }

    Scaffold(
        // Mencegah Header tertutup Notch/Status Bar
        contentWindowInsets = WindowInsets.statusBars,

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
            // FIX KEYBOARD SMOOTH: Gunakan Surface sebagai container utama input field
            // navigationBarsPadding() -> Mencegah tertutup tombol back/home HP
            // imePadding() -> Membuat input naik otomatis saat keyboard muncul (perlu adjustResize di Manifest)
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding() // Jarak aman navigasi bawah
                    .imePadding() // Jarak aman keyboard
            ) {
                // Panggil Input Field (tanpa Surface lagi di dalamnya)
                CommentInputFieldContent(
                    value = commentText,
                    onValueChange = { viewModel.onCommentTextChanged(it) },
                    onSend = {
                        if (policyId != null) {
                            viewModel.sendComment(policyId)
                        }
                    },
                    isSending = isPosting
                )
            }
        }
    ) { paddingValues ->
        if (isLoading || selectedPolicy == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1A73E8))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 20.dp) // Tambah padding bawah agar list tidak terpotong
            ) {
                // --- BAGIAN HEADER ---
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item {
                    PolicyHeader(
                        institution = selectedPolicy!!.institution,
                        date = selectedPolicy!!.createdAt,
                        title = selectedPolicy!!.title,
                        category = selectedPolicy!!.category
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
                item {
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

                // --- Sentimen Publik ---
                item {
                    PublicSentiment(
                        voteStats = voteStats,
                        userChoice = currentUserVote,
                        onVote = { choice ->
                            if (policyId != null) {
                                viewModel.castVote(policyId, choice)
                            }
                        }
                    )
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
                item {
                    Text("Komentar Masyarakat (${comments.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }

                // --- List Komentar ---
                if (comments.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Belum ada komentar. Jadilah yang pertama!",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(comments) { comment ->
                        CommentItem(comment)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

// --- FUNGSI FORMAT TANGGAL ---
fun formatDateTime(isoString: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date = parser.parse(isoString)

        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        formatter.timeZone = TimeZone.getDefault()

        formatter.format(date ?: java.util.Date())
    } catch (e: Exception) {
        isoString
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun PolicyHeader(institution: String, date: String, title: String, category: String) {
    Column {
        // 1. KATEGORI (Format Normal: "Kategori: Kesehatan")
        Surface(
            color = Color(0xFFE3F2FD),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Kategori: $category",
                color = Color(0xFF1565C0),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. JUDUL
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 3. INSTITUTION & DATE
        Text(
            text = "$institution • ${formatDateTime(date)}",
            fontSize = 12.sp,
            color = Color.Gray
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
    voteStats: PolicyViewModel.VoteStats,
    userChoice: String?,
    onVote: (String) -> Unit
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
    val formattedTotal = numberFormat.format(voteStats.totalVotes)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Sentimen Publik", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Total Partisipasi: $formattedTotal Suara", fontSize = 12.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                SentimentCard(
                    isAgreement = true,
                    count = voteStats.agreeCount,
                    percentage = voteStats.agreePercentage,
                    isSelected = userChoice == "setuju",
                    onClick = { onVote("setuju") }
                )
            }
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                SentimentCard(
                    isAgreement = false,
                    count = voteStats.disagreeCount,
                    percentage = voteStats.disagreePercentage,
                    isSelected = userChoice == "tidak",
                    onClick = { onVote("tidak") }
                )
            }
        }
    }
}

@Composable
fun SentimentCard(
    isAgreement: Boolean,
    count: Int,
    percentage: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val baseBackgroundColor = if (isAgreement) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val baseContentColor = if (isAgreement) Color(0xFF2E7D32) else Color(0xFFC62828)
    val borderColor = if (isSelected) baseContentColor else Color.Transparent
    val borderWidth = if (isSelected) 2.dp else 0.dp
    val icon = if (isAgreement) Icons.Outlined.ThumbUp else Icons.Outlined.ThumbDown
    val label = if (isAgreement) "Setuju" else "Tidak Setuju"
    val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = baseBackgroundColor),
        border = BorderStroke(borderWidth, borderColor),
        modifier = Modifier
            .fillMaxSize()
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 36.dp, bottom = 16.dp, start = 8.dp, end = 8.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(icon, contentDescription = null, tint = baseContentColor, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(label, color = baseContentColor, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text("$percentage%", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = baseContentColor)
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = baseContentColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "${numberFormat.format(count)} Suara",
                        fontSize = 10.sp,
                        color = baseContentColor,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(color = baseContentColor, shape = RoundedCornerShape(bottomStart = 12.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pilihanmu", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: PolicyComment) {
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
                Text(
                    text = comment.userName.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(comment.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                if (comment.isAdmin) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(color = Color(0xFF1A73E8), shape = RoundedCornerShape(4.dp)) {
                        Text("OFFICIAL", color = Color.White, fontSize = 8.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(comment.createdAt.take(10), fontSize = 11.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(comment.message, fontSize = 14.sp, color = Color.DarkGray)

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                Text("Balas", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A73E8))
            }
        }
    }
}

// --- SUB-COMPONENT INPUT FIELD ---
@Composable
fun CommentInputFieldContent(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    isSending: Boolean
) {
    // Tidak perlu Surface lagi di sini, karena sudah dibungkus Surface di bottomBar
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("Beri tanggapan Anda...", fontSize = 14.sp) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp),
            enabled = !isSending,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = Color(0xFF1A73E8)
            ),
            maxLines = 3
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = onSend,
            enabled = value.isNotBlank() && !isSending,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (value.isNotBlank() && !isSending) Color(0xFF1A73E8) else Color.LightGray)
        ) {
            if (isSending) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}