package com.example.cafeteria

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Handler(Looper.getMainLooper()).postDelayed( {
            var auth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) {
                var intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                var intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        },3000)


//        if(mAuth.currentUser != null) {
//            startActivity(Intent(this, HomeActivity::class.java))
//            finish()
//        } else {
//            startActivity(Intent(this, AuthActivity::class.java))
//            finish()
//        }
    }
}