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

fun formatAppwriteDate(isoDate: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        val date = inputFormat.parse(isoDate)
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        isoDate
    }
}

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
            Surface(shadowElevation = 3.dp) {
                Column(modifier = Modifier.background(Color.White)) {
                    // MENGGUNAKAN CenterAlignedTopAppBar AGAR LOGO BENAR-BENAR DI TENGAH
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
                            // Logo sekarang akan otomatis berada di tengah layar
                            Image(
                                painter = painterResource(id = R.drawable.logo_horizontal_bluenobg),
                                contentDescription = "Logo",
                                modifier = Modifier.height(30.dp),
                                contentScale = ContentScale.Fit
                            )
                        },
                        actions = {
                            IconButton(onClick = { navController.navigate(Screen.Notification.route) }) {
                                Icon(Icons.Outlined.Notifications, contentDescription = null)
                            }
                            IconButton(onClick = { }) {
                                Icon(Icons.Outlined.BookmarkBorder, contentDescription = null)
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.White
                        )
                    )
                    FilterSection()
                }
            }
        },
        bottomBar = {
            // Pastikan komponen ini sudah Anda buat/import
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        if (viewModel.policies.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1A73E8))
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = policy.institution,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A73E8)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = policy.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { 0.75f },
                    modifier = Modifier.weight(1f).height(6.dp).clip(CircleShape),
                    color = Color(0xFF1A73E8),
                    trackColor = Color(0xFFE8EAED)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("75% Setuju", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFF1F3F4))
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActionItem(Icons.Outlined.ThumbUp, "1.1rb")
                ActionItem(Icons.Outlined.ChatBubbleOutline, "24")
                ActionItem(Icons.Outlined.Share, "Bagikan")
                ActionItem(Icons.Outlined.BookmarkBorder, "Simpan")
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