# aop-part4-chapter02 - 음악 스트리밍 앱

자세한 구현 설명은 저의 [블로그 글](https://whyprogrammer.tistory.com/619) 에서도 확인하실 수 있습니다.

# 목차

1. 인트로 (완성앱 & 구현 기능 소개)
2. 재생화면 UI 구성하기
3. 플레이리스트 UI 구성하기
4. 음악 목록 API 만들기
5. ExoPlayer를 이용하여 음악 재생하기 (1)
6. ExoPlayer를 이용하여 음악 재생하기 (2)
7. ExoPlayer를 이용하여 음악 재생하기 (3)
8. ExoPlayer를 이용하여 음악 재생하기 (4)
9. 아웃트로

# 결과화면

![1](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbrmKcd%2FbtraNyfKC30%2FIyV5AzOI6PzyqhPtJMZBW0%2Fimg.png)

![2](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FmuUVN%2FbtraVTpxsGJ%2F1IM7rUSKD3Y46ZvFl5a7Bk%2Fimg.png)

# 이 챕터를 통해 배우는 것

- **Exoplayer** 사용하기 (2)
    - custom controller
    - Playlist 등
- androidx.constraintlayout.widget.Group
- **Seekbar** Custom 하기
- postDelayed
- TimeUnit

### ExoPlayer

- Google이 Android SDK 와 별도로 배포되는 오픈소스 프로젝트
- 오디오 및 동영상 재생 가능
- 오디오 및 동영상 재생 관련 강력한 기능들 포함
- 유튜브 앱에서 사용하는 라이브러리
- https://exoplayer.dev/hello-world.html

- 2가지 seekTo() 사용

다른 미디어 아이템으로 이동
```kotlin
    private fun playMusic(musicModel: MusicModel) {
    model.updateCurrentPosition(musicModel)
    player?.seekTo(model.currentPosition, 0) // positionsMs=0 초 부터 시작
    player?.play()
}
```

실제 재생 위치로 이동
```kotlin
    private fun initSeekBar() {
    binding.playerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            player?.seekTo(seekBar.progress * 1000L)
        }

    })
```

---

### 음악 스트리밍 앱

Retrofit 을 이용하여 재생 목록을 받아와 구성함

재생 목록을 클릭하여 ExoPlayer 를 이용하여 음악을 재생할 수 있음.

이전, 다음 트랙 버튼을 눌러서 이전, 다음 음악으로 재생하고, ui 를 업데이트 할 수 있음.

PlayList 화면과 Player 화면 간의 전환을 할 수 있음.

Seekbar 를 custom 하여 원하는 UI 로 표시할 수 있음.

---

### mocky

- https://run.mocky.io/v3/e4db045a-23a9-4b49-a3fc-78cf51f3f964

### group

- 그룹 위젯
- 재생목록, 플레이어 그룹 나눠서 나타내기를 관리함

### diffUtil

```kotlin
    companion object {
    val diffUtil = object : DiffUtil.ItemCallback<MusicModel>() {
        override fun areItemsTheSame(
            oldItem: MusicModel,
            newItem: MusicModel
        ): Boolean { // id 값만 비교
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MusicModel,
            newItem: MusicModel
        ): Boolean { // 내부 내용 비교
            return oldItem == newItem
        }
    }
}
```

```kotlin
    fun getAdapterModels(): List<MusicModel> {
    return playMusicList.mapIndexed { index, musicModel ->
        val newItem = musicModel.copy(
            isPlaying = index == currentPosition
        )
        newItem
    }
}
```

areItemsTheSame = 실제로 같은 아이템인지

areContentsTheSame = 실제로 컨텐츠와 같은 컨텐츠 인지

해당 프로젝트 에서 아이디 값은 인덱스로. 데이터 클래스의 copy()를 통해 실제로 새로운 모델로 만들어 주었음 (주소값이 새로운 값으로)
areContentsTheSame() 에서 실제로 다른 아이템으로 인식하므로 ui 업데이트가 가능했음
