package com.example.footballappcw

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.footballappcw.data.League
import com.example.footballappcw.data.LeagueDao
import com.example.footballappcw.ui.theme.FootballAppCWTheme
import kotlinx.coroutines.launch

private lateinit var leaguedao: LeagueDao

class AddLeaguesToDB : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        leaguedao = db.getLeagueDao() //Creates the table
        setContent {
            FootballAppCWTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AddLeaguesGUI()
                }
            }
        }
    }
}

@Composable
fun AddLeaguesGUI() {
    var strLeague by rememberSaveable { mutableStateOf("") }
    var strSport by rememberSaveable { mutableStateOf("") }
    var strLeagueAlternate by rememberSaveable { mutableStateOf("") }
    var leagueString by rememberSaveable { mutableStateOf("") }
    var fetchedLeagues by rememberSaveable { mutableStateOf<List<League>>(emptyList()) }
    var leaguesInserted by rememberSaveable { mutableStateOf(false) } //Validation to check if leagues are already inserted


    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(leagueString) {
        if (!leaguesInserted) { //This is checking if leagues have already been inserted
            try {
                leaguedao.insertLeague(
                    League(
                        strLeague = "English Premier League",
                        strSport = "Soccer",
                        strLeagueAlternate = "Premier League, EPL"
                    ),
                    League(
                        strLeague = "English League Championship",
                        strSport = "Soccer",
                        strLeagueAlternate = "Championship"
                    ),
                    League(
                        strLeague = "Scottish Premier League",
                        strSport = "Soccer",
                        strLeagueAlternate = "Scottish Premiership, SPFL"
                    ),
                    League(
                        strLeague = "German Bundesliga",
                        strSport = "Soccer",
                        strLeagueAlternate = "Bundesliga, FuÃŸball-Bundesliga"
                    ),
                    League(
                        strLeague = "Italian Serie A",
                        strSport = "Soccer",
                        strLeagueAlternate = "Serie A"
                    ),
                    League(
                        strLeague = "French Ligue 1",
                        strSport = "Soccer",
                        strLeagueAlternate = "Ligue 1 Conforama"
                    ),
                    League(
                        strLeague = "Spanish La Liga",
                        strSport = "Soccer",
                        strLeagueAlternate = "LaLiga Santander, La Liga"
                    ),
                    League(
                        strLeague = "Greek Superleague Greece",
                        strSport = "Soccer",
                        strLeagueAlternate = ""
                    ),
                    League(
                        strLeague = "Dutch Eredivisie",
                        strSport = "Soccer",
                        strLeagueAlternate = "Eredivisie"
                    ),
                    League(
                        strLeague = "Belgian First Division A",
                        strSport = "Soccer",
                        strLeagueAlternate = "Jupiler Pro League"
                    ),
                    League(
                        strLeague = "Turkish Super Lig",
                        strSport = "Soccer",
                        strLeagueAlternate = "Super Lig"
                    ),
                    League(
                        strLeague = "Danish Superliga",
                        strSport = "Soccer",
                        strLeagueAlternate = ""
                    ),
                    League(
                        strLeague = "Portuguese Primeira Liga",
                        strSport = "Soccer",
                        strLeagueAlternate = "Liga NOS"
                    ),
                    League(
                        strLeague = "American Major League Soccer",
                        strSport = "Soccer",
                        strLeagueAlternate = "MLS, Major League Soccer"
                    ),
                    League(
                        strLeague = "Swedish Allsvenskan",
                        strSport = "Soccer",
                        strLeagueAlternate = "Fotbollsallsvenskan"
                    ),
                    League(
                        strLeague = "Mexican Primera League",
                        strSport = "Soccer",
                        strLeagueAlternate = "Liga MX"
                    ),
                    League(
                        strLeague = "Brazilian Serie A",
                        strSport = "Soccer",
                        strLeagueAlternate = ""
                    ),
                    League(
                        strLeague = "Ukrainian Premier League",
                        strSport = "Soccer",
                        strLeagueAlternate = ""
                    ),
                    League(
                        strLeague = "Russian Football Premier League",
                        strSport = "Soccer",
                        strLeagueAlternate = "Ð§ÐµÐ¼Ð¿Ð¸Ð¾Ð½Ð°Ñ‚ Ð Ð¾ÑÑÐ¸Ð¸ Ð¿Ð¾ Ñ„ÑƒÑ‚Ð±Ð¾Ð»Ñƒ"
                    ),
                    League(
                        strLeague = "Australian A-League",
                        strSport = "Soccer",
                        strLeagueAlternate = "A-League"
                    ),
                    League(
                        strLeague = "Norwegian Eliteserien",
                        strSport = "Soccer",
                        strLeagueAlternate = "Eliteserien"
                    ),
                    League(
                        strLeague = "Chinese Super League",
                        strSport = "Soccer",
                        strLeagueAlternate = ""
                    ),
                )
                leaguesInserted = true
            } catch (e: Exception) {
                // Handle insertion error, if any
                Toast.makeText(
                    context,
                    "Could not insert leagues: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column {
            // TextFields for league information
            TextField(
                modifier = Modifier.padding(5.dp),
                label = { Text("League Name") },
                value = strLeague,
                onValueChange = { newText ->
                    strLeague = newText
                }
            )
            TextField(
                modifier = Modifier.padding(5.dp),
                label = { Text("League Sport") },
                value = strSport,
                onValueChange = { newText ->
                    strSport = newText
                }
            )
            TextField(
                modifier = Modifier.padding(5.dp),
                label = { Text("Alternate League Name") },
                value = strLeagueAlternate,
                onValueChange = { newText ->
                    strLeagueAlternate = newText
                }
            )
        }
        Spacer(modifier = Modifier.padding(10.dp))

        // Button to add a new league using the text fields
        Button(
            onClick = {
                if (strLeague.isNotEmpty() && strSport.isNotEmpty() && strLeagueAlternate.isNotEmpty()) {
                    try {
                        scope.launch {
                            leaguedao.insertLeague(
                                League(
                                    strLeague = strLeague,
                                    strSport = strSport,
                                    strLeagueAlternate = strLeagueAlternate
                                )
                            )
                            Toast.makeText(
                                context,
                                "League added to database",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Could not add League: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Please fill in all fields",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        ) {
            Text("Add New League")
        }

        Spacer(modifier = Modifier.padding(10.dp))

        // Button to add leagues to the database using hardcoded values
        Button(
            onClick = {
                if (!leaguesInserted) {
                    // Insert hardcoded leagues
                    // (Same code as the LaunchedEffect)
                } else {
                    Toast.makeText(
                        context,
                        "Leagues already inserted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        ) {
            Text("Add League to DB")
        }

        Spacer(modifier = Modifier.padding(10.dp))

        // Button to fetch teams from the database
        Button(
            onClick = {
                scope.launch {
                    try {
                        val leagues = leaguedao.getAllLeagues()
                        fetchedLeagues = leagues
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Could not fetch leagues: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        ) {
            Text("Display all Leagues")
        }

        // Display the fetched leagues in a lazy column
        LazyColumn {
            items(fetchedLeagues) { league ->
                Spacer(modifier = Modifier.padding(vertical = 8.dp)) // Adjust the padding values as needed
                LeagueItem(league = league)
            }
        }
    }
}

@Composable
fun LeagueItem(league: League) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = "League Name: ${league.strLeague}",
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Sport: ${league.strSport}"
        )
        Text(
            text = "Alternate Name: ${league.strLeagueAlternate}"
        )
    }
}