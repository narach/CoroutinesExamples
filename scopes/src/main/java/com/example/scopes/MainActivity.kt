package com.example.scopes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.scopes.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val TAG = "LifecycleDemo"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            btnStartActivity.setOnClickListener {
                GlobalScope.launch { // Будет выполняться, пока не закроется приложение
                    while (true) {
                        delay(1000L)
                        Log.d(TAG,"Global coroutine is running...")
                    }
                }

                lifecycleScope.launch { // Будет выполняться только пока активна MainActivity
                    while(true) {
                        delay(1000)
                        Log.d(TAG, "Activity coroutine is running")
                    }
                }

                GlobalScope.launch { // Открываем новую Activity с задержкой 5 секунд.
                    delay(5000)
                    Intent(this@MainActivity, SecondActivity::class.java).also {
                        startActivity(it)
                        finish() // Здесь корутина, запущенная из lifecycleScope, остановится
                    }
                }
            }
        }
    }
}