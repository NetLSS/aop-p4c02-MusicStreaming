package com.lilcode.aop.p4c02.musicstreaming

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.lilcode.aop.p4c02.musicstreaming.databinding.FragmentPlayerBinding
import com.lilcode.aop.p4c02.musicstreaming.service.MusicDto
import com.lilcode.aop.p4c02.musicstreaming.service.MusicService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlayerFragment : Fragment(R.layout.fragment_player) {

    private var _binding: FragmentPlayerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPlayListButton()

        getVideoListFromServer()

    }

    private fun initPlayListButton() {
        binding.playListImageView.setOnClickListener {
            //TODO: 만약 서버에서 데이터가 다 불려오지 않은 상태 일 때

            binding.playListGroup.isVisible = binding.playerViewGroup.isVisible.also { binding.playerViewGroup.isVisible = binding.playListGroup.isVisible }
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
                    .enqueue(object : Callback<MusicDto>{
                        override fun onResponse(
                            call: Call<MusicDto>,
                            response: Response<MusicDto>
                        ) {
                            Log.d("PlayerFragment", "${response.body()}")
                        }

                        override fun onFailure(call: Call<MusicDto>, t: Throwable) {

                        }

                    })
            }
    }

    companion object {
        fun newInstance(): PlayerFragment {
            return PlayerFragment()
        }
    }
}