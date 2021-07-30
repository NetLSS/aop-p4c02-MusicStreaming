package com.lilcode.aop.p4c02.musicstreaming

import com.lilcode.aop.p4c02.musicstreaming.service.MusicDto
import com.lilcode.aop.p4c02.musicstreaming.service.MusicEntity
import com.lilcode.aop.p4c02.musicstreaming.service.MusicModel

// 전달된 MusicModel 에 id 만 추가해서 갱신하는 fun
fun MusicEntity.mapper(id: Long): MusicModel =
    MusicModel(id = id, track, streamUrl, artist, coverUrl)

fun MusicDto.mapper(): PlayerModel =
    PlayerModel(
        playMusicList = musics.mapIndexed { index, musicEntity ->
            musicEntity.mapper(index.toLong())
        }
    )