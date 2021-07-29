package com.lilcode.aop.p4c02.musicstreaming

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lilcode.aop.p4c02.musicstreaming.databinding.ItemMusicBinding
import com.lilcode.aop.p4c02.musicstreaming.service.MusicModel

class MusicAdapter: ListAdapter<MusicModel, MusicAdapter.ViewHolder>(diffUtil) {


    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        fun bind(music: MusicModel){
            val binding = ItemMusicBinding.bind(itemView)
            binding.itemArtistTextView.text = music.artist
            binding.itemTrackTextView.text = music.track

            Glide.with(binding.itemCoverImageView.context)
                .load(music.coverUrl)
                .into(binding.itemCoverImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_music, parent, false)
        return ViewHolder(view)
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