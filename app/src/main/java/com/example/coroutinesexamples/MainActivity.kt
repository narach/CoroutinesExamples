package com.example.coroutinesexamples

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.coroutinesexamples.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        runInMainScope()
//        runIoAndUpdateInUI()
//        runInMainBlocking()

        // Job cancelling example
//        val job = cancellingExample()
//        runBlocking { // Cancel manually
//            delay(2000)
//            Log.d(TAG, "Cancelling a job")
//            job.cancel()
//        }
//
//        // Cancelling by timeout
//        GlobalScope.launch {
//            withTimeout(3000L) {
//                for(i in 30..45) {
//                    if(isActive) { // Check if the coroutine was not cancelled.
//                        Log.d(TAG, "Result for i = $i: ${fib(i)}")
//                    }
//                }
//            }
//        }

        asyncAwaitDemo()
    }

    fun jobsExample() {
        // Jobs
        val job = GlobalScope.launch(Dispatchers.Default) {
            Log.d(TAG,"Doing some job...")
        }

        runBlocking {
            job.join() // Ожидаем, пока завершится корутина
        }
    }

    fun cancellingExample() : Job {
        val job = GlobalScope.launch(Dispatchers.Default) {
            Log.d(TAG, "Starting long running calculation...")
            for(i in 30..45) {
                if(isActive) { // Check if the coroutine was not cancelled.
                    Log.d(TAG, "Result for i = $i: ${fib(i)}")
                }
            }
        }
        return job
    }

    fun runInMainScope() {
        GlobalScope.launch {
            val networkCallAnswer = doNetworkCall()
            val networkCallAnswer2 = doNetworkCall2()

            Log.d(TAG, networkCallAnswer)
            Log.d(TAG, networkCallAnswer2)
        }
        Log.d(TAG, "From thread ${Thread.currentThread().name}")
    }

    fun runIoAndUpdateInUI() {
        GlobalScope.launch(Dispatchers.IO) { // Run in the IO dispatcher Thread
            Log.d(TAG, "Starting coroutine in thread: ${Thread.currentThread().name}")
            val answer = doNetworkCall()
            withContext(Dispatchers.Main) { // Update UI in Main dispatcher Thread
                Log.d(TAG, "Updating UI in thread: ${Thread.currentThread().name}")
                binding.tvResult.text = answer
            }
        }
    }

    fun runInMainBlocking() {
        // Run Blocking
        Log.d(TAG, "Before runBlocking")
        runBlocking { // Run coroutine in a mainThread and block it until completed
            launch(Dispatchers.IO) {// Run in IO Thread
                delay(3000L)
                Log.d(TAG, "Finished IO Coroutine 1")
            }
            launch(Dispatchers.IO) {// Run in IO Thread
                delay(3000L)
                Log.d(TAG, "Finished IO Coroutine 2")
            }
            Log.d(TAG, "Start of runBlocking")
            delay(5000L) // Block UI update
            Log.d(TAG, "End of runBlocking")
        }
        Log.d(TAG, "After runBlocking")
    }

    // Последовательное и параллельное выполнение корутин.
    fun asyncAwaitDemo() {
        GlobalScope.launch(Dispatchers.IO) {
            val time1 = measureTimeMillis { // Последовательное выполнение
                val response1 = doNetworkCall()
                val response2 = doNetworkCall2()
            }
            Log.d(TAG, "Two calls took $time1 ms")

            // В двух отдельных корутинах - ждем выполнения обеих - не правильно!
            val time2 = measureTimeMillis {
                var response1: String? = null
                var response2: String? = null
                val job1 = launch { response1 = doNetworkCall() }
                val job2 = launch { response2 = doNetworkCall2() }
                job1.join()
                job2.join()
            }
            Log.d(TAG, "Two calls in separate coroutines took $time2 ms")

            // Асинхронный вызов
            val time3 = measureTimeMillis {
                val response1 = async { doNetworkCall() }
                val response2 = async { doNetworkCall2() }
                Log.d(TAG, "Answer1 is ${response1.await()}")
                Log.d(TAG, "Answer2 is ${response1.await()}")
            }
            Log.d(TAG, "Request took $time3 ms")
        }
    }

    suspend fun doNetworkCall() : String {
        delay(3000L)
        return "Network Response 1"
    }

    suspend fun doNetworkCall2() : String {
        delay(3000L)
        return "Network Response 2"
    }

    fun fib(n: Int): Long {
        return if(n == 0) 0
        else if(n == 1) 1
        else fib(n-1) + fib(n-2)
    }
}