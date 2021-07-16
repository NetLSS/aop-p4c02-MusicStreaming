package com.lilcode.aop.p4c02.musicstreaming.service

import retrofit2.Call
import retrofit2.http.GET

interface MusicService {
    @GET("/v3/e4db045a-23a9-4b49-a3fc-78cf51f3f964")
    fun listMusics(): Call<MusicDto>
}