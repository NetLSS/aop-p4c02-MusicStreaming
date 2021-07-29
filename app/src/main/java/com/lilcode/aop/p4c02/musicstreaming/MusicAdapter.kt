package com.lilcode.aop.p4c02.musicstreaming

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lilcode.aop.p4c02.musicstreaming.databinding.ItemMusicBinding
import com.lilcode.aop.p4c02.musicstreaming.service.MusicModel

class MusicAdapter: ListAdapter<MusicModel, MusicAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val itemMusicBinding: ItemMusicBinding):RecyclerView.ViewHolder(itemMusicBinding.root){
        fun bind(music: MusicModel){
            itemMusicBinding.itemArtistTextView.text = music.artist
            itemMusicBinding.itemTrackTextView.text = music.track

            Glide.with(itemMusicBinding.itemCoverImageView.context)
                .load(music.coverUrl)
                .into(itemMusicBinding.itemCoverImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<MusicModel>(){
            override fun areItemsTheSame(oldItem: MusicModel, newItem: MusicModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MusicModel, newItem: MusicModel): Boolean {
                return oldItem == newItem
            }

        }
    }


}