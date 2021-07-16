package com.lilcode.aop.p4c02.musicstreaming

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lilcode.aop.p4c02.musicstreaming.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _viewBinding: ActivityMainBinding? = null
    private val viewBinding: ActivityMainBinding get() = requireNotNull(_viewBinding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // 미래에 재상용성을 위해 프레그먼트로
        supportFragmentManager.beginTransaction()
            .replace(viewBinding.fragmentContainer.id, PlayerFragment.newInstance())
            .commit()
    }
}