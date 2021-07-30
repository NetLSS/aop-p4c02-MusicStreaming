package com.lilcode.aop.p4c02.musicstreaming

import com.lilcode.aop.p4c02.musicstreaming.service.MusicModel

data class PlayerModel (
    private val playMusicList: List<MusicModel> = emptyList(),
    var currentPosition: Int = -1, // -1: 초기화 되지 않은 값
    var isWatchingPlayListView: Boolean = true
){

    // 가져 갈 때마다 position 위치 보고 반환
    fun getAdapterModels(): List<MusicModel> {
        return playMusicList.mapIndexed { index, musicModel ->
            // data class 의 강력한 기능 중 하나 copy. 수정하려는 값만 수정.
            /*
            copy를 통해 새로운 값만 갱신
             */
            val newItem = musicModel.copy(
                isPlaying = index == currentPosition
            )
            newItem
        }
    }
}