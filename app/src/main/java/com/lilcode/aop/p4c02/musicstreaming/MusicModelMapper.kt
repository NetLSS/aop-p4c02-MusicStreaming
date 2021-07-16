package com.lilcode.aop.p4c02.musicstreaming

import com.lilcode.aop.p4c02.musicstreaming.service.MusicEntity
import com.lilcode.aop.p4c02.musicstreaming.service.MusicModel

fun MusicEntity.mapper(id: Long): MusicModel =
    MusicModel(id = id, track, streamUrl, artist, coverUrl)

