package id.antasari.suara_230104040212_tugasakhir

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import androidx.activity.enableEdgeToEdge
import id.antasari.suara_230104040212_tugasakhir.navigation.NavGraph
import id.antasari.suara_230104040212_tugasakhir.ui.theme.Suara_230104040212_tugasakhirTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Aktifkan Edge-to-Edge agar layout mengisi seluruh layar termasuk status bar
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            Suara_230104040212_tugasakhirTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}