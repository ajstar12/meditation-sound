package com.arjun.meso.roomDB

import android.app.Application
import androidx.lifecycle.LiveData
import com.arjun.meso.utils.MyCallBack
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class RoomRepository {
    private var dao: Dao? = null
    private var allSettings: LiveData<List<SettingsModel>>? = null
    private var lastSettings: LiveData<List<SettingsModel>>? = null
    private var allSound: LiveData<List<SoundModel>>? = null

    constructor(application: Application?) {
        val database: MyRoomDB? = MyRoomDB.getInstance(application)
        dao = database!!.Dao()
        allSettings = dao!!.getAllSettings()
        lastSettings = dao!!.getLastSettings()
        allSound = dao!!.getAllSound()
    }
    fun insertSettings(model: SettingsModel) {
        subscribeOnBackground{
            dao!!.insertSettings(model)
        }
    }
    fun updateSettings(model: SettingsModel) {
        subscribeOnBackground{
            dao!!.updateSettings(model)
        }
    }
    fun deleteSettings(model: SettingsModel) {
        subscribeOnBackground{
            dao!!.deleteSettings(model)
        }
    }
    fun getAllSettings():LiveData<List<SettingsModel>>? {
        return allSettings;
    }
    fun getLastSettings():LiveData<List<SettingsModel>>? {
        return lastSettings;
    }
    fun insertSound(model: SoundModel) {
        subscribeOnBackground{
            dao!!.insertSound(model)
        }
    }
    fun updateSound(model: SoundModel) {
        subscribeOnBackground{
            dao!!.updateSound(model)
        }
    }
    fun deleteSound(model: SoundModel) {
        subscribeOnBackground{
            dao!!.deleteSound(model)
        }
    }
    fun getSoundBySettingsId(settingsId: String, myCallBack: MyCallBack)  {
        Thread {
            myCallBack.callBack(dao!!.getSoundBySettingsId(settingsId))
//            val list: List<TicketRoomModul> = dao.get100Ticket()
//            activity.runOnUiThread(Runnable { mycallbackClass.ticketDataCallback(list) })
        }.start()
    }
    fun getAllSound():LiveData<List<SoundModel>>? {
        return allSound;
    }
    fun subscribeOnBackground(function: () -> Unit) {
        Single.fromCallable {
            function()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }
}