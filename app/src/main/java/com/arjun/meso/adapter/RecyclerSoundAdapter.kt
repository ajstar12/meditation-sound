package com.arjun.meso.adapter

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.arjun.meso.R
import com.arjun.meso.databinding.RecyclerSoundListSingleBinding
import com.arjun.meso.roomDB.SoundModel
import java.text.DecimalFormat

class RecyclerSoundAdapter(
    val context: Context,
    val arrayList: ArrayList<SoundModel>,
    val soundModels: List<SoundModel>?
) : RecyclerView.Adapter<RecyclerSoundAdapter.ViewHolder>() {

    var firstTime = true
    var soundPlayer = HashMap<Int, MediaPlayer>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val mainBinding = RecyclerSoundListSingleBinding.inflate(LayoutInflater.from(context))

        return ViewHolder(mainBinding.root,mainBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pojo = arrayList[holder.adapterPosition]
        holder.binding.txtSoundName.text = pojo.soundName

        val mp = MediaPlayer()
        val descriptor = context.assets.openFd(pojo.soundAddress)
        mp.setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
        descriptor.close()
        mp.prepare()
        mp.setOnCompletionListener {
            Log.e("Arjun_Test","Complete")
        }
        soundPlayer[pojo.soundId] = mp

        holder.binding.imgPlay.setOnClickListener(){
            if (mp.isPlaying){
                mp.pause()
                holder.binding.imgPlay.setBackgroundResource(R.drawable.icon_play)
            }else{
                mp.start()
                mp.isLooping = true
                holder.binding.imgPlay.setBackgroundResource(R.drawable.ic_baseline_pause_24)
            }
        }

        holder.binding.txtSoundMinus.setOnClickListener(){
            var volume = pojo.soundVolume
            if (volume>0.1F){
                volume-=0.1F
            }
            arrayList[holder.adapterPosition].soundVolume = volume

            //volume is the float number between 0.0 and 1.0
            mp.setVolume(volume, volume);
            holder.binding.txtSound.text = volumeFormate(volume.toDouble()*10)
        }
        holder.binding.txtSoundPlus.setOnClickListener(){
            var volume = pojo.soundVolume
            if (volume<1.0F){
                volume+=0.1F
            }

            arrayList[holder.adapterPosition].soundVolume = volume

            //volume is the float number between 0.0 and 1.0
            mp.setVolume(volume, volume);
            holder.binding.txtSound.text = volumeFormate(volume.toDouble()*10)
        }
        if (firstTime && checkSelected(pojo.soundName,holder)){
            holder.binding.soundCheck.isChecked = true
            arrayList[holder.adapterPosition].selected = true
        }else{
            holder.binding.soundCheck.isChecked = pojo.selected
        }

        holder.binding.soundCheck.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            arrayList[holder.adapterPosition].selected = holder.binding.soundCheck.isChecked
        })

        if (holder.adapterPosition==(arrayList.size-1)){
            firstTime = false
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class ViewHolder(itemView: View, mainBinding: RecyclerSoundListSingleBinding) : RecyclerView.ViewHolder(itemView){
        var binding: RecyclerSoundListSingleBinding
        init {
            binding = mainBinding
        }
    }
    private fun volumeFormate(toDouble: Double):String{
        val format = DecimalFormat("##")
        return format.format(toDouble)
    }
    private fun checkSelected(soundName: String, holder: ViewHolder):Boolean{
        if (soundModels!=null && soundModels.isNotEmpty()){
            for (array in soundModels){
                if (array.soundName == soundName){
                    holder.binding.txtSound.text = volumeFormate(array.soundVolume.toDouble()*10)
                    arrayList[holder.adapterPosition].soundVolume = array.soundVolume
                    return true
                }
            }
        }
        return false
    }
}