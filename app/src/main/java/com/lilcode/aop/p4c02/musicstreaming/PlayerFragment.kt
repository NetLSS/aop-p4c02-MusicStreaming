package com.lilcode.aop.p4c02.musicstreaming

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
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
import java.util.concurrent.TimeUnit

class PlayerFragment : Fragment(R.layout.fragment_player) {

    private var _binding: FragmentPlayerBinding? = null
    private lateinit var adapter: MusicAdapter
    private var model: PlayerModel = PlayerModel()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = requireNotNull(_binding)

    private var player: SimpleExoPlayer? = null

    private val updateSeekRunnable = Runnable {
        updateSeek()
    }

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
        initSeekBar()
        initRecyclerView()

        getVideoListFromServer()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initSeekBar() {
        binding.playerSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                player?.seekTo(seekBar.progress * 1000L)
            }

        })

        // clickable 을 false 로 주면 됨.
//        binding.playListSeekBar.setOnTouchListener { v, event ->
//            false // 터치 무시
//        }
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
            val nextMusic = model.nextMusic() ?: return@setOnClickListener
            playMusic(nextMusic)

        }

        binding.skipPrevImageView.setOnClickListener {
            val prevMusic = model.prevMusic() ?: return@setOnClickListener
            playMusic(prevMusic)

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

            // 미디어 아이템이 바뀔 때
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)

                val newIndex: String = mediaItem?.mediaId ?: return
                model.currentPosition = newIndex.toInt()
                adapter.submitList(model.getAdapterModels())

                // 리사이클러 뷰 스크롤 이동
                binding.playListRecyclerView.scrollToPosition(model.currentPosition)

                updatePlayerView(model.currentMusicModel())
            }

            // 재생, 재생완료, 버퍼링 상태 ...
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)

                updateSeek()
            }
        })

    }

    private fun updateSeek() {
        val player = this.player ?: return
        val duration = if (player.duration >= 0) player.duration else 0 // 전체 음악 길이
        val position = player.currentPosition

        updateSeekUi(duration, position)

        val state = player.playbackState

        view?.removeCallbacks(updateSeekRunnable)
        // 재생 중 일때 (재생 중이 아니거나, 재생이 끝나지 않은 경우)
        if (state != Player.STATE_IDLE && state != Player.STATE_ENDED) {
            view?.postDelayed(updateSeekRunnable, 1000) // 1초에 한번씩 실행
        }

    }

    private fun updateSeekUi(duration: Long, position: Long) {
        binding.playListSeekBar.max = (duration / 1000).toInt()
        binding.playListSeekBar.progress = (position / 1000).toInt()

        binding.playerSeekBar.max = (duration / 1000).toInt()
        binding.playerSeekBar.progress = (position / 1000).toInt()

        binding.playTimeTextView.text = String.format(
            "%02d:%02d",
            TimeUnit.MINUTES.convert(position, TimeUnit.MILLISECONDS),
            (position / 1000) % 60
        )
        binding.totalTimeTextView.text= String.format(
            "%02d:%02d",
            TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS),
            (duration / 1000) % 60
        )

    }

    private fun updatePlayerView(currentMusicModel: MusicModel?) {
        currentMusicModel ?: return

        binding.trackTextView.text = currentMusicModel.track
        binding.artistTextView.text = currentMusicModel.artist

        Glide.with(binding.coverImageView.context)
            .load(currentMusicModel.coverUrl)
            .into(binding.coverImageView)
    }

    private fun initRecyclerView() {
        adapter = MusicAdapter {
            playMusic(it)
        }

        binding.playListRecyclerView.adapter = adapter
        binding.playListRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun initPlayListButton() {
        binding.playListImageView.setOnClickListener {
            // 만약 서버에서 데이터가 다 불려오지 않은 상태 일 때
            if (model.currentPosition == -1) return@setOnClickListener

            // 강의 와는 다르게 구현
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

                            response.body()?.let { musicDto ->
                                model = musicDto.mapper()

                                setMusicList(model.getAdapterModels())
                                adapter.submitList(model.getAdapterModels())
                            }
                        }

                        override fun onFailure(call: Call<MusicDto>, t: Throwable) {

                        }

                    })
            }
    }

    private fun setMusicList(modelList: List<MusicModel>) {
        player ?: return
        context?.let {
            player?.addMediaItems(modelList.map { musicModel ->
                MediaItem.Builder()
                    .setMediaId(musicModel.id.toString()) // 미디어 아이디를 musicModel id로
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

    private fun playMusic(musicModel: MusicModel) {
        model.updateCurrentPosition(musicModel)
        player?.seekTo(model.currentPosition, 0) // positionsMs=0 초 부터 시작
        player?.play()
    }

    override fun onStop() {
        super.onStop()

        player?.pause()
        view?.removeCallbacks(updateSeekRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
        player?.release()
        view?.removeCallbacks(updateSeekRunnable)
    }

    companion object {
        fun newInstance(): PlayerFragment {
            return PlayerFragment()
        }
    }
}