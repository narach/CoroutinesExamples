package com.example.firestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firestore.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

data class Person(val name: String = "", val age: Int = -1)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the document
        val tutorialDocument = Firebase.firestore.collection("coroutines")
            .document("tutorial")
        val peter = Person("Peter", 25)
        GlobalScope.launch(Dispatchers.IO) {
            delay(3000L)
            tutorialDocument.set(peter).await()
            val person = tutorialDocument.get().await().toObject(Person::class.java)
            withContext(Dispatchers.Main) {
                binding.tvData.text = person.toString()
            }
        }
    }
}