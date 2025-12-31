package id.antasari.suara_230104040212_tugasakhir.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import id.antasari.suara_230104040212_tugasakhir.R
import id.antasari.suara_230104040212_tugasakhir.navigation.Screen
import id.antasari.suara_230104040212_tugasakhir.ui.theme.Suara_230104040212_tugasakhirTheme

data class Proposal(
    val id: Int,
    val category: String,
    val title: String,
    val description: String,
    val imageUrl: Int,
    val voteCount: Int,
    val agreementPercentage: Float,
    val categoryColor: Color
)

val dummyProposals = listOf(
    Proposal(
        1,
        "Infrastruktur",
        "Perbaikan Trotoar Sudirman Tahap 2",
        "Usulan perbaikan jalur pedestrian untuk kenyamanan...",
        R.drawable.welcome1, // Placeholder, will replace with something better if available
        1240,
        0.75f,
        Color(0xFFFFA726)
    ),
    Proposal(
        2,
        "Kesehatan",
        "Vaksinasi Booster Gratis Lansia",
        "Program jemput bola untuk vaksinasi booster bagi lansia di...",
        R.drawable.welcome2, // Placeholder
        850,
        0.45f,
        Color(0xFF66BB6A)
    ),
    Proposal(
        3,
        "Pendidikan",
        "Beasiswa Anak Berprestasi 2024",
        "Pendaftaran beasiswa untuk siswa SMA/SMK berprestasi kin...",
        R.drawable.welcome3, // Placeholder
        2450,
        0.92f,
        Color(0xFF42A5F5)
    )
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location Icon",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "DKI Jakarta", fontWeight = FontWeight.Bold)
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Image(
                            painter = painterResource(id = R.drawable.profile), // Replace with a user profile pic
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            FilterChips()
            ProposalList(proposals = dummyProposals)
        }
    }
}

@Composable
fun FilterChips() {
    var selectedChipIndex by remember { mutableStateOf(0) }
    val chipTitles = listOf("Semua", "Infrastruktur", "Kesehatan", "Pendidikan", "Lingkungan")
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(chipTitles) { index, title ->
            InputChip(
                selected = selectedChipIndex == index,
                onClick = { selectedChipIndex = index },
                label = { Text(title) },
                colors = InputChipDefaults.inputChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                border = null
            )
        }
    }
}

@Composable
fun ProposalList(proposals: List<Proposal>) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(proposals) { proposal ->
            ProposalCard(proposal = proposal)
        }
    }
}

@Composable
fun ProposalCard(proposal: Proposal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = proposal.category,
                        color = proposal.categoryColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .background(
                                color = proposal.categoryColor.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = proposal.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = proposal.description, fontSize = 14.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Image(
                    painter = painterResource(id = proposal.imageUrl),
                    contentDescription = proposal.title,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Suara",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${proposal.voteCount} Suara", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${(proposal.agreementPercentage * 100).toInt()}% Setuju",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { proposal.agreementPercentage },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Dukung"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Dukung")
                }
                TextButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = "Komentar"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Komentar")
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Bookmark"
                    )
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Suara_230104040212_tugasakhirTheme {
        HomeScreen(navController = rememberNavController())
    }
}
