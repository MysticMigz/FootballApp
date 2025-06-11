package com.example.footballappcw.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
@Dao
interface ClubDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClub(clubs: List<Club>)

    //Using this to check if the league was already in the database
    @Query("SELECT * FROM Club WHERE strLeague = :leagueName")
    suspend fun getClubsByLeagueName(leagueName: String): List<Club>

    @Query("SELECT * FROM Club WHERE strTeam LIKE '%' || :searchText || '%' OR strLeague LIKE '%' || :searchText || '%'")
    suspend fun searchClubs(searchText: String): List<Club>
}