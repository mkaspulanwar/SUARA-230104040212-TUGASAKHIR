package id.antasari.suara_230104040212_tugasakhir.ui.screen

import androidx.compose.foundation.BorderStroke // <-- INI YANG KURANG TADI
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import id.antasari.suara_230104040212_tugasakhir.navigation.Screen
import id.antasari.suara_230104040212_tugasakhir.ui.viewmodel.PolicyViewModel
import id.antasari.suara_230104040212_tugasakhir.ui.factory.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

// --- WARNA KONSISTEN ---
val PrimaryBlue = Color(0xFF1C74E9) // Warna Biru Utama (#1c74e9)
val BackgroundWhite = Color(0xFFFFFFFF) // Warna Putih (#ffffff)

// --- HELPER FUNCTIONS ---

// 1. Format Tanggal
fun formatAppwriteDate(isoDate: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val date = inputFormat.parse(isoDate)

        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        outputFormat.timeZone = TimeZone.getDefault()

        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        if(isoDate.length >= 10) isoDate.take(10) else isoDate
    }
}

// 2. Format Angka (1200 -> 1.2rb)
fun formatCount(count: Int): String {
    return when {
        count >= 1000 -> String.format(Locale.US, "%.1frb", count / 1000.0)
        else -> count.toString()
    }
}

// --- MAIN SCREEN ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: PolicyViewModel = viewModel(
        factory = ViewModelFactory(context)
    )

    LaunchedEffect(Unit) {
        viewModel.fetchPolicies()
    }

    Scaffold(
        topBar = {
            Surface(shadowElevation = 3.dp, color = BackgroundWhite) {
                Column(modifier = Modifier.background(BackgroundWhite)) {
                    CenterAlignedTopAppBar(
                        modifier = Modifier.statusBarsPadding(),
                        navigationIcon = {
                            Image(
                                painter = painterResource(id = R.drawable.profile),
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .size(35.dp)
                                    .clip(CircleShape)
                                    .clickable { /* Aksi profil */ },
                                contentScale = ContentScale.Crop
                            )
                        },
                        title = {
                            Image(
                                painter = painterResource(id = R.drawable.logo_horizontal_bluenobg),
                                contentDescription = "Logo",
                                modifier = Modifier.height(30.dp),
                                contentScale = ContentScale.Fit
                            )
                        },
                        actions = {
                            IconButton(onClick = { navController.navigate(Screen.Notification.route) }) {
                                Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Color.Gray)
                            }
                            IconButton(onClick = { }) {
                                Icon(Icons.Outlined.BookmarkBorder, contentDescription = null, tint = Color.Gray)
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = BackgroundWhite
                        )
                    )
                    FilterSection()
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        if (viewModel.policies.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA))
                    .padding(paddingValues)
            ) {
                item { Spacer(modifier = Modifier.height(12.dp)) }

                items(viewModel.policies) { policy ->
                    NewsCard(
                        policy = policy,
                        onClick = {
                            navController.navigate("policy_detail/${policy.id}")
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun NewsCard(policy: PolicyModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // --- HEADER CARD ---
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = policy.institution,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = policy.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp,
                        maxLines = 3,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CategoryBadge(category = policy.category)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = formatAppwriteDate(policy.createdAt),
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                AsyncImage(
                    model = policy.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // --- PROGRESS BAR ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { policy.agreePercentage / 100f },
                    modifier = Modifier.weight(1f).height(6.dp).clip(CircleShape),
                    color = PrimaryBlue,
                    trackColor = Color(0xFFE8EAED),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "${policy.agreePercentage}% Setuju",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFF1F3F4))

            // --- ACTION BUTTONS ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActionItem(Icons.Outlined.ThumbUp, formatCount(policy.totalVotes))
                ActionItem(Icons.Outlined.ChatBubbleOutline, formatCount(policy.totalComments))
                ActionItem(Icons.Outlined.Share, "Bagikan")
                ActionItem(Icons.Outlined.BookmarkBorder, "Simpan")
            }
        }
    }
}

// --- FILTER SECTION (OUTLINE STYLE) ---
@Composable
fun FilterSection() {
    val categories = listOf("Semua", "Pendidikan", "Kesehatan", "Ekonomi", "Lingkungan", "Infrastruktur")
    var selected by remember { mutableStateOf(0) }

    val unselectedBorderColor = Color(0xFFE0E0E0) // Warna border abu-abu

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().background(BackgroundWhite)
    ) {
        itemsIndexed(categories) { index, title ->
            val isSelected = selected == index

            val borderColor = if (isSelected) PrimaryBlue else unselectedBorderColor
            val textColor = if (isSelected) PrimaryBlue else Color.Gray

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        BorderStroke(1.dp, borderColor), // Outline biru atau abu
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { selected = index }
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = title,
                    color = textColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun CategoryBadge(category: String) {
    Surface(color = Color(0xFFF1F3F4), shape = RoundedCornerShape(4.dp)) {
        Text(
            text = category,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )
    }
}

@Composable
fun ActionItem(icon: ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clip(RoundedCornerShape(6.dp)).clickable { }.padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}