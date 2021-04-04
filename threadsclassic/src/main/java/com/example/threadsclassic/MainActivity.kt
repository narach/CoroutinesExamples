package com.example.threadsclassic

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

const val TAG = "ThreadSamples"

class CustomThread : Thread() { // Создаем новый класс для потока
    override fun run() {
        Log.d(TAG, "Running in thread: ${currentThread().name}")
    }
}

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Создание и запуск нового потока
        val runnable = Runnable {
            Log.d(TAG, "Running in thread: ${Thread.currentThread().name}")
        }
        Thread(runnable).apply {
            start()
        }

        // Запускаем поток
        val customThread = CustomThread()
        customThread.start()

        // Примеры ExecutorService

        // Один поток в пуле
        val singleThreadExecutor = Executors.newSingleThreadExecutor()
        // Будет переиспользовать ранее созданные потоки
        val cachedThreadPoolExecutor = Executors.newCachedThreadPool()
        // Фиксированное число активных потоков в пуле
        val fixedThreadPoolExecutor = Executors.newFixedThreadPool(10)
        // Может запускать потоки с задержкой или по расписанию
        val scheduledThreadPoolExecutor = Executors.newScheduledThreadPool(10)

        // Запуск задач
        val runnable2 = Runnable { // Будет выполнено в отдельном потоке
            Log.d(TAG, "Started by executor service...")
        }
        singleThreadExecutor.submit(runnable2)

        val callable = object : Callable<String> {
            override fun call(): String {
                // Исполняется в отдельном потоке c возвратом результата
                return "Result from another thread!"
            }
        }
        val futureResult = singleThreadExecutor.submit(callable)
        val result = futureResult.get() // Ожидаем результат. Поток блокируется
        Log.d(TAG, "Callable result: $result")

        val longCallable = object : Callable<String> {
            override fun call(): String {
                for (i in 1..10) {
                    if (!Thread.currentThread().isInterrupted) {
                        Log.d(TAG, "$i: Callable is running...")
                        Thread.sleep(1000)
                    }
                }
                return "Callable Finished!"
            }
        }

//        var longTaskFuture = singleThreadExecutor.submit(longCallable)
//        Thread.sleep(3000)
//        Log.d(TAG, "Trying to cancel callable")
//        longTaskFuture.cancel(true)
//        Log.d(TAG, "Callable is cancelled!")

//        singleThreadExecutor.shutdown()

        CompletableFuture.supplyAsync(
            object : Supplier<Int> {
                override fun get(): Int {
                    // Работа в потоке с возвратом результата
                    return 200
                }
            }, singleThreadExecutor
        ).thenApplyAsync(object : Function<Int, String> {
            override fun apply(num: Int): String {
                // Работа в потоке с входными данными и возвратом результата
                return "The number is: $num"
            }
        }, singleThreadExecutor
        ).thenAcceptAsync(
            object : Consumer<String> {
                override fun accept(result: String) {
                    // Работа с входными данными
                    Log.d(TAG, "Result: $result")
                }
            })
    }
}