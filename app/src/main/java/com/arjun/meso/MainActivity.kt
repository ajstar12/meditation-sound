package com.arjun.meso

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.arjun.meso.adapter.RecyclerSettingsListAdapter
import com.arjun.meso.adapter.RecyclerSoundAdapter
import com.arjun.meso.databinding.ActivityMainBinding
import com.arjun.meso.databinding.SoundDialogSingleBinding
import com.arjun.meso.roomDB.SettingsModel
import com.arjun.meso.roomDB.SoundModel
import com.arjun.meso.roomDB.ViewModal
import com.arjun.meso.utils.Constant
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class MainActivity : AppCompatActivity() {

    var lastSettingId=0;
    lateinit var viewModal: ViewModal
    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Constant.log("App_link","https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName())

        inIt()
        setClick()
    }
    lateinit var sharedPreferences: SharedPreferences
    lateinit var arrayList : List<SettingsModel>
    private fun inIt() {
        sharedPreferences = getSharedPreferences("Data", MODE_PRIVATE)

        viewModal = ViewModal(application);
        arrayList = ArrayList()

        viewModal.getLastSettings()!!.observe(this, Observer {
            if (it.size>0){
                lastSettingId = it.get(0).id
            }
        })
        viewModal.getAllSettings()!!.observe(this, Observer {
            if (it.size>0){
                arrayList = it
                binding.recyclerSettingsList.layoutManager = GridLayoutManager(this,1)
                var adapter = RecyclerSettingsListAdapter(arrayList)
                binding.recyclerSettingsList.adapter = adapter
            }
        })
        binding.imgOptionMenu.setOnClickListener{
            var popupMenu = PopupMenu(this,it);
            popupMenu.inflate(R.menu.main_pop_up_menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { it ->
                when (it!!.itemId){
                    R.id.menuFeedback -> {
                        startActivity(Intent(this@MainActivity,FeedbackActivity::class.java))
                    }
                }
                return@OnMenuItemClickListener true
            })
            popupMenu.show()
        }

        checkUpdateAvailble()
    }
    private fun setClick() {
        binding.fabAdd.setOnClickListener(){
            soundDialog();
        }
    }

    fun setSoundArrayList():ArrayList<SoundModel>{
        val arrayList = ArrayList<SoundModel>()
        val pojoOm = SoundModel(0,1,0,"Om Sound","om_sound.mpeg",1.0F,false)
        arrayList.add(pojoOm)
        val pojoSitar = SoundModel(0,2,0,"Sitar Sound","sitar_sound.mpeg",1.0F,false)
        arrayList.add(pojoSitar)
        val pojoWater = SoundModel(0,3,0,"Water Sound","water_sound.mpeg",1.0F,false)
        arrayList.add(pojoWater)
        val pojoBird = SoundModel(0,4,0,"Bird Sound","bird_sound.mpeg",1.0F,false)
        arrayList.add(pojoBird)
        val pojoWind = SoundModel(0,5,0,"Wind Sound","wind_sound.mpeg",1.0F,false)
        arrayList.add(pojoWind)
        val pojobowl = SoundModel(0,6,0,"Singing Bowl Sound","singing_bowl.mpeg",1.0F,false)
        arrayList.add(pojobowl)
        val pojobowl2 = SoundModel(0,7,0,"Singing Bowl 2 Sound","singing_bowl2.mpeg",1.0F,false)
        arrayList.add(pojobowl2)
        return arrayList
    }
    private fun soundDialog() {
        val dialog=Dialog(this@MainActivity)
        val bindingDialog = SoundDialogSingleBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(bindingDialog.root)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)

        bindingDialog.recyclerSound.layoutManager=GridLayoutManager(this@MainActivity,1)
        val arrayList = setSoundArrayList()
        val adapter = RecyclerSoundAdapter(this@MainActivity, arrayList, null)
        bindingDialog.recyclerSound.adapter = adapter

        bindingDialog.btnCreateSound.setOnClickListener(){
            var selectionCount = 0
            for (array in arrayList){
                if (array.selected){
                    selectionCount++
                }
            }
            if (selectionCount>0){
                bindingDialog.linearSetup.isVisible = true
            }else{
                Toast.makeText(this,"Please Select At Least One Sound.",Toast.LENGTH_SHORT).show()
            }
        }
        bindingDialog.btnSoundSubmit.setOnClickListener(){

            var settingsModel = SettingsModel()
            settingsModel.settingsName = bindingDialog.edtSetupName.text.toString()

            viewModal.insertSettings(settingsModel)

            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                for (array in arrayList){
                    if (array.selected){
                        array.settingsId = lastSettingId
                        viewModal.insertSound(array)
                    }
                }
                dialog.dismiss()
            },200)
        }
        dialog.show()
        dialog.setOnDismissListener {
            for (array in arrayList){
                if (adapter.soundPlayer.containsKey(array.soundId)){
                    if (adapter.soundPlayer[array.soundId]!!.isPlaying){
                        adapter.soundPlayer[array.soundId]!!.pause()
                    }
                }
            }
        }
    }

    private fun checkUpdateAvailble() {
        val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask: Task<AppUpdateInfo> = appUpdateManager.getAppUpdateInfo()

        appUpdateInfoTask.addOnSuccessListener(OnSuccessListener<AppUpdateInfo> { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() === UpdateAvailability.UPDATE_AVAILABLE) {
                getUpdate(appUpdateManager, appUpdateInfo)
            }
        })
    }

    var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult<IntentSenderRequest, ActivityResult>(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            // handle callback
            if (result.resultCode != RESULT_OK) {
                Constant.log(
                    "ActivityResultCallback",
                    "Update flow failed! Result code: " + result.resultCode
                )
                // If the update is canceled or fails,
                // you can request to start the update again.
            }
        }

    private fun appUpdatePopUp(vesionCode: Int, changeList: String) {
        val dialog = Dialog(this@MainActivity)
        dialog.setContentView(R.layout.dialog_app_update)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvUpdate = dialog.findViewById<Button>(R.id.tvUpdate)
        val tvChangeList = dialog.findViewById<TextView>(R.id.tvChangeList)

        tvChangeList.text = changeList

        tvUpdate.setOnClickListener { //app link
            val viewIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName())
            )
            startActivity(viewIntent)
        }
        dialog.show()
    }
    fun getUpdate(appUpdateManager: AppUpdateManager, appUpdateInfo: AppUpdateInfo) {

        val db = Firebase.firestore
        db.collection("App_update")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Constant.log("getUpdate", "${document.id} => ${document.data.get("version").toString()} ")
                    if (document.data.get("version").toString().isEmpty()) {
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, activityResultLauncher, AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build())
                    } else {
                        try {
                            if (document.data["version"].toString() == "" + appUpdateInfo.availableVersionCode() && document.data["update_type"].toString() != "optional") {
                                appUpdatePopUp(appUpdateInfo.availableVersionCode(), document.data["change_list"].toString())
                            } else {
                                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, activityResultLauncher, AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build())
                            }
                        } catch (e: Exception) {
                            Constant.log("checkUpdateAvailble Error", e.message!!, e)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Constant.log("getUpdate", "Error getting documents.", exception)
            }
    }
}