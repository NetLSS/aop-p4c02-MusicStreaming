package com.lilcode.aop.p4c02.musicstreaming

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lilcode.aop.p4c02.musicstreaming.databinding.ItemMusicBinding
import com.lilcode.aop.p4c02.musicstreaming.service.MusicModel

class MusicAdapter(private val callback: (MusicModel) -> Unit): ListAdapter<MusicModel, MusicAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val itemMusicBinding: ItemMusicBinding):RecyclerView.ViewHolder(itemMusicBinding.root){
        fun bind(music: MusicModel){
            itemMusicBinding.itemArtistTextView.text = music.artist
            itemMusicBinding.itemTrackTextView.text = music.track

            Glide.with(itemMusicBinding.itemCoverImageView.context)
                .load(music.coverUrl)
                .into(itemMusicBinding.itemCoverImageView)

            // 재생 중에 따라
            if(music.isPlaying){
                // itemView 를 사용했는데 이건 리사이클러 뷰에서 뷰홀더(아이템 하나) 현재 아이템에 해당
                itemView.setBackgroundColor(Color.GRAY) // 재생중이면 배경 색을 회색
            }else{
                itemView.setBackgroundColor(Color.TRANSPARENT)
            }

            itemView.setOnClickListener {
                callback(music)
            }
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
            override fun areItemsTheSame(oldItem: MusicModel, newItem: MusicModel): Boolean { // id 값만 비교
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MusicModel, newItem: MusicModel): Boolean { // 내부 내용 비교
                return oldItem == newItem
            }
        }
    }
}