package com.arjun.meso.adapter

import android.app.Dialog
import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arjun.meso.R
import com.arjun.meso.databinding.RecyclerSettingsListSingleBinding
import com.arjun.meso.databinding.SoundDialogSingleBinding
import com.arjun.meso.roomDB.SettingsModel
import com.arjun.meso.roomDB.SoundModel
import com.arjun.meso.roomDB.ViewModal
import com.arjun.meso.utils.MyCallBack

class RecyclerSettingsListAdapter(var arrayList: List<SettingsModel>) : RecyclerView.Adapter<RecyclerSettingsListAdapter.ViewHolder>() {

    var playPosition = -1
    lateinit var soundTrack : HashMap<Int, HashMap<Int,MediaPlayer>>
    private lateinit var viewModal : ViewModal
    lateinit var activity: AppCompatActivity
    private lateinit var context:Context
    class ViewHolder(itemView: View, mainBinding: RecyclerSettingsListSingleBinding) : RecyclerView.ViewHolder(itemView){
        var binding: RecyclerSettingsListSingleBinding
        init {
            binding = mainBinding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        soundTrack = HashMap<Int, HashMap<Int,MediaPlayer>>()

        context = parent.context
        activity = context as AppCompatActivity
        viewModal = ViewModal(activity.application)
        var mainBinding = RecyclerSettingsListSingleBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(mainBinding.root,mainBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var pojo = arrayList[holder.adapterPosition]
        holder.binding.txtSettingsName.text = pojo.settingsName
        if (playPosition != holder.adapterPosition && pojo.isPlaying){
            if (soundTrack.containsKey(pojo.id)){
                soundPause(soundTrack[pojo.id])
            }
            pojo.isPlaying = false
            holder.binding.imgPlay.setBackgroundResource(R.drawable.icon_play)
        }else if (playPosition == holder.adapterPosition && pojo.isPlaying){
            holder.binding.imgPlay.setBackgroundResource(R.drawable.ic_baseline_pause_24)
        }else{
            pojo.isPlaying = false
            holder.binding.imgPlay.setBackgroundResource(R.drawable.icon_play)
        }
        holder.binding.imgSettingsMenu.setOnClickListener(){
            var popupMenu = PopupMenu(context,it);
            popupMenu.inflate(R.menu.setting_pop_up_menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { it ->
                when (it!!.itemId){
                    R.id.menuEdit -> {
                        getSoundListForEdit(pojo,true);
                    }
                    R.id.menuDelete -> {
                        viewModal.deleteSettings(pojo)
                    }
                }
                return@OnMenuItemClickListener true
            })
            popupMenu.show()
        }

        holder.binding.imgPlay.setOnClickListener(){
            if (pojo.isPlaying){
                if (soundTrack.containsKey(pojo.id)){
                    soundPause(soundTrack[pojo.id])
                }

                playPosition = -1
                pojo.isPlaying = false
                notifyDataSetChanged()
            }else{
                playPosition = holder.adapterPosition
                if (soundTrack.containsKey(pojo.id)){
                    soundPlay(soundTrack[pojo.id])
                }else{
                    getSoundList(pojo)
                }

                pojo.isPlaying = true
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
    fun getSoundList(pojo: SettingsModel) {
        viewModal.getSoundBySettingsId(pojo.id.toString(), object :MyCallBack{
            override fun callBack(list: List<SoundModel>) {
                activity.runOnUiThread { playMusic(list,pojo) }
            }
        })
    }
    fun getSoundListForEdit(pojo: SettingsModel, editStatus: Boolean) {
        var a = editStatus
        viewModal.getSoundBySettingsId(pojo.id.toString(), object: MyCallBack{
            override fun callBack(list: List<SoundModel>) {
                activity.runOnUiThread {
                    if (a){
                        a = false
                        soundEdit(pojo,list)
                    }
                }
            }
        })
    }
    fun playMusic(soundModels: List<SoundModel>, pojo: SettingsModel) {
        var soundHash = HashMap<Int,MediaPlayer>()
        for (array in soundModels){
            val mp = MediaPlayer()
            val descriptor = context.assets.openFd(array.soundAddress)
            mp.setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
            descriptor.close()
            mp.prepare()
            mp.setOnCompletionListener {
                Log.e("Arjun_Test","Complete")
            }

            Log.e("Arjun_Test","Start")
            mp.start()
            mp.isLooping = true

            mp.setVolume(array.soundVolume, array.soundVolume);
            soundHash.put(array.soundId,mp)
        }
        soundTrack.put(pojo.id,soundHash)
        notifyDataSetChanged()
    }
    fun soundPlay(soundModels: HashMap<Int, MediaPlayer>?) {

        for (array in soundModels!!.values){
            val mp = array

            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                mp.start()
                mp.isLooping = true
            },200)
        }
        notifyDataSetChanged()
    }
    fun soundPause(soundModels: HashMap<Int, MediaPlayer>?) {
        for (array in soundModels!!.values){
            val mp = array
            mp.pause()
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

    private fun soundEdit(pojo: SettingsModel, soundModels: List<SoundModel>) {
        val dialog= Dialog(context)
        val bindingDialog = SoundDialogSingleBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(bindingDialog.root)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)

        bindingDialog.recyclerSound.layoutManager= GridLayoutManager(context,1)
        val arrayList = setSoundArrayList()
        val adapter = RecyclerSoundAdapter(context,arrayList,soundModels)
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
                bindingDialog.edtSetupName.setText(pojo.settingsName)
            }else{
                Toast.makeText(context,"Please Select At Least One Sound.", Toast.LENGTH_SHORT).show()
            }
        }
        bindingDialog.btnSoundSubmit.setOnClickListener(){

            var settingsModel = SettingsModel()
            settingsModel.id = pojo.id
            settingsModel.settingsName = bindingDialog.edtSetupName.text.toString()

            viewModal.updateSettings(settingsModel)

            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                for (array in arrayList){
                    if (array.selected){
                        array.settingsId = pojo.id
                        var soundId = checkSelected(array.soundName,soundModels)
                        if (soundId==0){
                            viewModal.insertSound(array)
                        }else{
                            array.id = soundId
                            viewModal.updateSound(array)
                        }
                    }else{
                        var soundId = checkSelected(array.soundName,soundModels)
                        if (soundId!=0){
                            array.id = soundId
                            viewModal.deleteSound(array)
                        }
                    }
                    if (adapter.soundPlayer.containsKey(array.soundId)){
                        if (adapter.soundPlayer[array.soundId]!!.isPlaying){
                            adapter.soundPlayer[array.soundId]!!.pause()
                        }
                    }
                }
                dialog.dismiss()
            },200)
        }
        dialog.show()
        dialog.setOnDismissListener {
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
    }
    private fun checkSelected(soundName: String, soundModels: List<SoundModel>):Int{
        if (soundModels!=null && soundModels.isNotEmpty()){
            for (array in soundModels){
                if (array.soundName == soundName){
                    return array.id
                }
            }
        }
        return 0
    }
}