package com.example.footballappcw


import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.StrictMode
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.footballappcw.data.Club
import com.example.footballappcw.data.ClubDao
import com.example.footballappcw.ui.theme.FootballAppCWTheme
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


private lateinit var clubDao: ClubDao // Initialize clubDao

class SearchForClubs : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clubDao = DatabaseHolder.getDatabase(applicationContext).getClubDao()
        setContent {
            FootballAppCWTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SearchClubsScreen()
                }
            }
        }
    }
}

@Composable
fun SearchClubsScreen() {
    var searchText by rememberSaveable { mutableStateOf("") }
    var searchResults by rememberSaveable { mutableStateOf<List<Club>>(emptyList()) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            modifier = Modifier.padding(5.dp),
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search for Club or League") }
        )

        Spacer(modifier = Modifier.padding(10.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        searchResults = clubDao.searchClubs(searchText)
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Error searching for clubs: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        ) {
            Text("Search")
        }

        Spacer(modifier = Modifier.padding(10.dp))

        LazyColumn {
            items(searchResults) { club ->
                ClubItem(club)
            }
        }
    }
}


@Composable
fun ClubItem(club: Club) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        val clubImageUrl = club.strTeamLogo
        val bitmap = fetchImageFromUrl(clubImageUrl)
        bitmap?.let {
            Image(
                painter = BitmapPainter(it),
                contentDescription = "Club Logo",
                modifier = Modifier.size(100.dp)
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Text("Club Name: ${club.strTeam}")
        Text("Stadium: ${club.strStadium}")
        Text("League: ${club.strLeague}")
    }
}


@Composable
fun fetchImageFromUrl(url: String): ImageBitmap? {
    var imageBitmap: ImageBitmap? = null
    try {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val imageUrl = URL(url)
        val connection: HttpURLConnection = imageUrl.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input = connection.inputStream
        val bitmap = BitmapFactory.decodeStream(input)
        imageBitmap = bitmap.asImageBitmap()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return imageBitmap
}
