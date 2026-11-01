package com.arjun.meso.roomDB

import androidx.lifecycle.LiveData
import androidx.room.*


@androidx.room.Dao
interface Dao {

    @Insert
    fun insertSound(soundModel: SoundModel)

    @Update
    fun updateSound(soundModel: SoundModel)

    @Delete
    fun deleteSound(soundModel: SoundModel)

    @Query("DELETE FROM mst_sound")
    fun deleteAllSound()

    @Query("SELECT * FROM mst_sound ORDER BY id ASC")
    fun getAllSound() : LiveData<List<SoundModel>>

    @Query("SELECT * FROM mst_sound Where settingsId=:settingsId")
    fun getSoundBySettingsId(settingsId:String) : List<SoundModel>

    @Insert
    fun insertSettings(settingsModel: SettingsModel)

    @Update
    fun updateSettings(settingsModel: SettingsModel)

    @Delete
    fun deleteSettings(settingsModel: SettingsModel)

    @Query("DELETE FROM mst_sound")
    fun deleteAllSettings()

    @Query("SELECT * FROM mst_settings ORDER BY id ASC")
    fun getAllSettings() : LiveData<List<SettingsModel>>

    @Query("SELECT * FROM mst_settings ORDER BY id DESC LIMIT 0,1")
    fun getLastSettings() : LiveData<List<SettingsModel>>
}