package com.arjun.meso.roomDB

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.arjun.meso.utils.MyCallBack


class ViewModal : AndroidViewModel {

    private val repository: RoomRepository

    private var allSettings: LiveData<List<SettingsModel>>? = null
    private var lastSettings: LiveData<List<SettingsModel>>? = null
    private var allSound: LiveData<List<SoundModel>>? = null
    constructor(application: Application) : super(application){
        repository = RoomRepository(application)
        allSettings = repository.getAllSettings()
        lastSettings = repository.getLastSettings()
        allSound = repository.getAllSound()
    }

    fun insertSettings(model: SettingsModel) {
        repository.insertSettings(model)
    }
    fun updateSettings(model: SettingsModel) {
        repository.updateSettings(model)
    }
    fun deleteSettings(model: SettingsModel) {
        repository.deleteSettings(model)
    }
    fun getAllSettings():LiveData<List<SettingsModel>>? {
        return allSettings;
    }
    fun getLastSettings():LiveData<List<SettingsModel>>? {
        return lastSettings;
    }
    fun insertSound(model: SoundModel) {
        repository.insertSound(model)
    }
    fun updateSound(model: SoundModel) {
        repository.updateSound(model)
    }
    fun deleteSound(model: SoundModel) {
        repository.deleteSound(model)
    }
    fun getSoundBySettingsId(settingsId: String,myCallBack: MyCallBack)  {
        repository.getSoundBySettingsId(settingsId,myCallBack)
    }
    fun getAllSound():LiveData<List<SoundModel>>? {
        return allSound;
    }
}