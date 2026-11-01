package com.arjun.meso.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mst_sound")
class SoundModel (
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0,
    var soundId:Int = 0,
    var settingsId:Int = 0,
    var soundName:String="",
    var soundAddress:String="",
    var soundVolume:Float=1.0F,
    var selected:Boolean = false
)