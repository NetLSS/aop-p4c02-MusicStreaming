package com.lilcode.aop.p4c02.musicstreaming.service

data class MusicModel (
    val id: Long,
    val track: String,
    val streamUrl: String,
    val artist: String,
    val coverUrl: String,
    val isPlaying: Boolean = false
)