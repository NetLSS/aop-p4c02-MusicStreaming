package com.lilcode.aop.p4c02.musicstreaming

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.lilcode.aop.p4c02.musicstreaming.databinding.FragmentPlayerBinding
import com.lilcode.aop.p4c02.musicstreaming.service.MusicDto
import com.lilcode.aop.p4c02.musicstreaming.service.MusicModel
import com.lilcode.aop.p4c02.musicstreaming.service.MusicService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlayerFragment : Fragment(R.layout.fragment_player) {

    private var _binding: FragmentPlayerBinding? = null
    private lateinit var adapter: MusicAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var player: SimpleExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)

        @Suppress("UnnecessaryVariable")
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPlayView()
        initPlayListButton()
        initPlayControlButtons()
        initRecyclerView()

        getVideoListFromServer()

    }

    private fun initPlayControlButtons() {

        // 재생 or 일시정지 버튼
        binding.playControlImageView.setOnClickListener {
            val player = this.player ?: return@setOnClickListener

            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }

        binding.skipNextImageView.setOnClickListener {

        }

        binding.skipPrevImageView.setOnClickListener {

        }
    }

    private fun initPlayView() {
        context?.let {
            player = SimpleExoPlayer.Builder(it).build()
        }
        binding.playerView.player = player

        player?.addListener(object : Player.EventListener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                // 플레이어가 재생 또는 일시정지 될 떄

                if (isPlaying) {
                    binding.playControlImageView.setImageResource(R.drawable.ic_baseline_pause_48)
                } else {
                    binding.playControlImageView.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                }
            }
        })

    }

    private fun initRecyclerView() {
        adapter = MusicAdapter {
            // TODO: 음악을 재생
        }

        binding.playListRecyclerView.adapter = adapter
        binding.playListRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun initPlayListButton() {
        binding.playListImageView.setOnClickListener {
            //TODO: 만약 서버에서 데이터가 다 불려오지 않은 상태 일 때

            binding.playListGroup.isVisible = binding.playerViewGroup.isVisible.also {
                binding.playerViewGroup.isVisible = binding.playListGroup.isVisible
            }
        }
    }

    private fun getVideoListFromServer() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(MusicService::class.java)
            .also {
                it.listMusics()
                    .enqueue(object : Callback<MusicDto> {
                        override fun onResponse(
                            call: Call<MusicDto>,
                            response: Response<MusicDto>
                        ) {
                            Log.d("PlayerFragment", "${response.body()}")

                            response.body()?.let {
                                val modelList = it.musics.mapIndexed { index, musicEntity ->
                                    musicEntity.mapper(index.toLong())
                                }

                                setMusicList(modelList)
                                adapter.submitList(modelList)
                            }
                        }

                        override fun onFailure(call: Call<MusicDto>, t: Throwable) {

                        }

                    })
            }
    }

    private fun setMusicList(modelList: List<MusicModel>) {
        player?:return
        context?.let {
            player?.addMediaItems(modelList.map{ musicModel ->
                MediaItem.Builder()
                    .setMediaId(musicModel.id.toString())
                    .setUri(musicModel.streamUrl)
                    .build()
                /*
                미디어 아이템에 2가지 태그 지정 가능
                미디어 id, 뷰에 태그 지정했듯 미디어 아이템에 태그 지정 가능
                 */
            })

            player?.prepare()
        }
    }

    companion object {
        fun newInstance(): PlayerFragment {
            return PlayerFragment()
        }
    }
}