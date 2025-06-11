package com.example.footballappcw.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Club::class, League::class], version = 1)
abstract class FootballDataBase: RoomDatabase(){
    abstract fun getLeagueDao(): LeagueDao

    abstract fun getClubDao(): ClubDao
}
