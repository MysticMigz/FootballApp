package com.example.footballappcw

import android.app.Application
import androidx.room.Room
import com.example.footballappcw.data.ClubDao
import com.example.footballappcw.data.FootballDataBase

class databaseInitialization : Application(){
    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(this, FootballDataBase::class.java, "tabledatabases").build()
        clubDAO = db.getClubDao()
    }

}

lateinit var db: FootballDataBase
lateinit var clubDAO: ClubDao