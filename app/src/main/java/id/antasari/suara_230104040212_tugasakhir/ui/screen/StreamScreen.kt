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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import id.antasari.suara_230104040212_tugasakhir.R
import id.antasari.suara_230104040212_tugasakhir.ui.theme.Suara_230104040212_tugasakhirTheme

// Data classes
data class Post(
    val id: Int,
    val userName: String,
    val userLocation: String,
    val userAvatar: Int,
    val timeAgo: String,
    val content: String,
    val image: Int?,
    val tag: String,
    val likes: Int,
    val comments: Int
)

// Dummy data
val dummyPosts = listOf(
    Post(
        id = 1,
        userName = "Budi Santoso",
        userLocation = "Menteng, Jakarta Pusat",
        userAvatar = R.drawable.logo_blue_nobg, // Using logo as placeholder
        timeAgo = "2h ago",
        content = "Jalan di Kecamatan Menteng rusak parah setelah hujan semalam. Mohon dinas terkait segera cek. Banyak pengendara motor yang hampir jatuh karena lubang tertutup genangan air.",
        image = null,
        tag = "Infrastructure",
        likes = 12,
        comments = 4
    ),
    Post(
        id = 2,
        userName = "Siti Aminah",
        userLocation = "Kebayoran Baru",
        userAvatar = R.drawable.logo_blue_nobg, // Using logo as placeholder
        timeAgo = "5h ago",
        content = "Sangat setuju dengan kebijakan baru mengenai taman kota. Anak-anak jadi punya tempat bermain yang aman dan bersih. Semoga perawatannya juga konsisten ya Pak/Bu! 🌳🌲",
        image = R.drawable.welcome1, // Using welcome1 as placeholder
        tag = "Public Space",
        likes = 45,
        comments = 10
    ),
    Post(
        id = 3,
        userName = "Andi Pratama",
        userLocation = "Tebet",
        userAvatar = R.drawable.logo_blue_nobg, // Using logo as placeholder
        timeAgo = "1d ago",
        content = "Lampu penerangan jalan di sepanjang jalan utama banyak yang mati. Cukup berbahaya kalau malam hari.",
        image = null,
        tag = "Safety",
        likes = 8,
        comments = 2
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamScreen(navController: NavController) {
    Scaffold(
        topBar = { StreamTopAppBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: New post action */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Edit, contentDescription = "New Post")
            }
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item { PostInputCard() }
            items(dummyPosts) { post ->
                Spacer(modifier = Modifier.height(8.dp))
                PostCard(post)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamTopAppBar() {
    TopAppBar(
        modifier = Modifier.statusBarsPadding(),
        title = {
            Text(
                "Suara Warga",
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
fun PostInputCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logo_blue_nobg), // Placeholder
                    contentDescription = "Your Avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Apa yang Anda pikirkan? Sampaikan aspirasi Anda...", color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Icon(Icons.Outlined.PhotoCamera, contentDescription = "Add Photo", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Outlined.LocationOn, contentDescription = "Add Location", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Outlined.Analytics, contentDescription = "Create Poll", tint = MaterialTheme.colorScheme.primary)
                }
                Button(onClick = { /*TODO*/ }) {
                    Text("Post")
                }
            }
        }
    }
}

@Composable
fun PostCard(post: Post) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = post.userAvatar),
                    contentDescription = post.userName,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(post.userName, fontWeight = FontWeight.Bold)
                    Text("${post.userLocation} • ${post.timeAgo}", fontSize = 12.sp, color = Color.Gray)
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Default.MoreHoriz, contentDescription = "More options")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(post.content)
            if (post.image != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Image(
                    painter = painterResource(id = post.image),
                    contentDescription = "Post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            SuggestionChip(
                onClick = { /* TODO */ },
                label = { Text(post.tag) }
            )
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Like")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(post.likes.toString())
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Comment")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(post.comments.toString())
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StreamScreenPreview() {
    Suara_230104040212_tugasakhirTheme {
        StreamScreen(navController = rememberNavController())
    }
}
