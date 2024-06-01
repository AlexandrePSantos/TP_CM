package com.example.trabpratico.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.trabpratico.R
import com.example.trabpratico.adapter.SlideAdapter
import com.example.trabpratico.model.SlideItem

class IntroActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var slideAdapter: SlideAdapter
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        viewPager = findViewById(R.id.viewPager)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        val slides = listOf(
            SlideItem(R.drawable.ic_slide1, "Welcome to our App!"),
            SlideItem(R.drawable.ic_slide2, "Discover new features."),
            SlideItem(R.drawable.ic_slide3, "Start using the app now.")
        )

        slideAdapter = SlideAdapter(slides)
        viewPager.adapter = slideAdapter

        btnLogin.setOnClickListener {
            navigateToLogin()
        }

        btnRegister.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
}
