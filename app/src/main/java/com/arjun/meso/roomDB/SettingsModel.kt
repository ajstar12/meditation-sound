package com.arjun.meso.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mst_settings")
class SettingsModel {
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0;
    var settingsName = "";
    var isPlaying = false;
}