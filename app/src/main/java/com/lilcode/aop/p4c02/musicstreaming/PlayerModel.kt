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
            copy() 를 통해 새로운 값만 갱신
            copy() 를 통해 리스트를 갱신 하여 리사이클러뷰에 리스트 등록 시
            변경된 값만 UI 갱신 하도록 할 수 있었음
             */
            val newItem = musicModel.copy(
                isPlaying = index == currentPosition
            )
            newItem
        }
    }

    fun updateCurrentPosition(musicModel: MusicModel) {
        /*
        어차피 id 값이 인덱스 값이긴 함.
         */
        currentPosition = playMusicList.indexOf(musicModel)
    }

    fun nextMusic(): MusicModel? {
        if (playMusicList.isEmpty()) return null

        currentPosition = if ((currentPosition + 1) == playMusicList.size) 0 else currentPosition + 1

        return playMusicList[currentPosition]
    }

    fun prevMusic(): MusicModel? {
        if (playMusicList.isEmpty()) return null

        // kotlin 의 lastIndex 사용 해보기
        currentPosition = if ((currentPosition - 1) < 0) playMusicList.lastIndex else currentPosition - 1

        return playMusicList[currentPosition]
    }
}