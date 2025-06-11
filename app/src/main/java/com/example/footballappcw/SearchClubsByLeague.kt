package com.example.footballappcw

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.footballappcw.data.Club
import com.example.footballappcw.data.ClubDao
import com.example.footballappcw.data.FootballDataBase
import com.example.footballappcw.ui.theme.FootballAppCWTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class SearchClubsByLeague : ComponentActivity() {

    private lateinit var clubdao: ClubDao // Initialize clubdao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initializes the clubdao with an instance of ClubDao
        clubdao = DatabaseHolder.getDatabase(applicationContext).getClubDao()

        setContent {
            FootballAppCWTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SCBLGUI(clubdao)
                }
            }
        }
    }
}
@Composable
fun SCBLGUI(clubDao: ClubDao) {
    var leagueName by rememberSaveable { mutableStateOf("") }
    var clubDetails by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        item {
            TextField(
                value = leagueName,
                onValueChange = { leagueName = it },
                label = { Text("Enter League Name") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Button(
                onClick = {
                    scope.launch {
                        try {
                            val response = fetchClubsFromWebService(leagueName)
                            clubDetails = parseClubDetails(response)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Retrieve Clubs")
            }
        }
        item {
            Button(
                onClick = {
                    scope.launch {
                        try {
                            val clubs = fetchClubsFromWebService(leagueName)
                            val saved = saveClubsToDatabase(clubDao, clubs, leagueName)
                            if (saved) {
                                Toast.makeText(context, "Clubs saved to database", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "League '$leagueName' already exists in the database", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Log.e("SaveClubs", "Error saving clubs to database: ${e.message}", e)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Save to Database")
            }
        }
        items(clubDetails) { detail ->
            Text(
                text = detail,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )
        }
    }
}

suspend fun fetchClubsFromWebService(leagueName: String): List<Club> {
    return withContext(Dispatchers.IO) {
        val encodedLeagueName = URLEncoder.encode(leagueName, "UTF-8")
        val urlString = "https://www.thesportsdb.com/api/v1/json/3/search_all_teams.php?l=$encodedLeagueName"
        println("URL: $urlString") // Log the constructed URL

        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = connection.inputStream
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val response = StringBuilder()

            var inputLine: String?
            while (bufferedReader.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
            bufferedReader.close()

            println("Response: $response") // Log the response from the API

            parseJSON(response.toString())
        } else {
            throw Exception("Failed to fetch data. Response code: $responseCode")
        }
    }
}

fun parseJSON(response: String): List<Club> {
    val clubsList = mutableListOf<Club>()
    val jsonObject = JSONObject(response)

    // Check if the response contains the expected key
    if (jsonObject.has("teams")) {
        val teamsArray = jsonObject.getJSONArray("teams")

        for (i in 0 until teamsArray.length()) {
            val teamObject = teamsArray.getJSONObject(i)
            val club = Club(
                idTeam = teamObject.getString("idTeam"),
                strTeam = teamObject.getString("strTeam"),
                strTeamShort = teamObject.getString("strTeamShort"),
                strAlternate = teamObject.getString("strAlternate"),
                intFormedYear = teamObject.getString("intFormedYear"),
                strLeague = teamObject.getString("strLeague"),
                idLeague = teamObject.getString("idLeague"),
                strStadium = teamObject.getString("strStadium"),
                strKeywords = teamObject.getString("strKeywords"),
                strStadiumLocation = teamObject.getString("strStadiumLocation"),
                intStadiumCapacity = teamObject.getString("intStadiumCapacity"),
                strWebsite = teamObject.getString("strWebsite"),
                strTeamJersey = teamObject.getString("strTeamJersey"),
                strTeamLogo = teamObject.getString("strTeamLogo")
            )
            clubsList.add(club)
        }
    } else {
        Log.e("ParseJSON", "No 'teams' key found in JSON response: $response")
    }

    return clubsList
}

// Parse the JSON response to extract club details
fun parseClubDetails(response: List<Club>): List<String> {
    val clubDetails = mutableListOf<String>()
    for (club in response) {
        val clubName = club.strTeam
        val clubShortName = club.strTeamShort
        val clubStadium = club.strStadium
        val clubCapacity = club.intStadiumCapacity
        // Format club details
        val clubDetailString = "Club Name: $clubName\nShort Name: $clubShortName\nStadium: $clubStadium\nCapacity: $clubCapacity\n"
        clubDetails.add(clubDetailString)
    }
    return clubDetails
}

suspend fun saveClubsToDatabase(clubDao: ClubDao, clubs: List<Club>, leagueName: String): Boolean {
    // Check if the league already exists in the database
    val existingLeague = clubDao.getClubsByLeagueName(leagueName)

    return if (existingLeague.isNotEmpty()) {
        Log.d("SaveClubs", "League '$leagueName' already exists in the database")
        // Return false indicating that league already exists
        false
    } else {
        // Insert clubs into the database
        clubDao.insertClub(clubs)
        Log.d("SaveClubs", "Clubs saved to database")
        // Return true indicating successful save
        true
    }
}

object DatabaseHolder {
    private var footballDatabase: FootballDataBase? = null

    fun getDatabase(context: Context): FootballDataBase {
        if (footballDatabase == null) {
            synchronized(FootballDataBase::class.java) {
                if (footballDatabase == null) {
                    footballDatabase = Room.databaseBuilder(
                        context.applicationContext,
                        FootballDataBase::class.java,
                        "tabledatabases"
                    ).build()
                }
            }
        }
        return footballDatabase!!
    }
}