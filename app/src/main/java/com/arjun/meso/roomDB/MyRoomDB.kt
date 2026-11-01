package com.arjun.meso.roomDB

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [SettingsModel::class,SoundModel::class], version = 2)
abstract class MyRoomDB : RoomDatabase() {

    abstract fun Dao(): Dao

    companion object {
        private var instance: MyRoomDB? = null

        @Synchronized
        open fun getInstance(application: Application?): MyRoomDB? {
            if (instance == null) {
                instance =  Room.databaseBuilder(application!!.applicationContext,
                    MyRoomDB::class.java, "sound_database") // below line is use to add fall back to
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()
            }
            return instance
        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                //populateDatabase(instance!!)
            }
        }

        /*private fun populateDatabase(db: NoteDatabase) {
            val noteDao = db.noteDao()
            subscribeOnBackground {
                noteDao.insert(Note("title 1", "desc 1", 1))
                noteDao.insert(Note("title 2", "desc 2", 2))
                noteDao.insert(Note("title 3", "desc 3", 3))

            }
        }*/
    }
}