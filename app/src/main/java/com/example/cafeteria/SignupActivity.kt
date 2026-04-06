package com.example.cafeteria

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cafeteria.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var frAuth: FirebaseAuth
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        frAuth = FirebaseAuth.getInstance()

        binding.registerbtn.setOnClickListener {

            registerUser()
        }

    }

    private fun registerUser() {
        val name = binding.etnameUser.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()


        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT)
                .show()
            return
        }
        frAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val firebaseUser = frAuth.currentUser
                firebaseUser?.let { user ->
                    val uid = user.uid

//                    // Update Profile with SAP ID
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
//
                    user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Registration Successful with User ID",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = if (email == "aeshima2004@gmail.com") {
                                Intent(this, AdminPageActivity::class.java)
                            } else {
                                Intent(this, HomeActivity::class.java)
                            }
                            startActivity(intent)
                            finish()
                        }
                    }

                    // Save to Firestore using a Kotlin Map
                    val userMap = hashMapOf(
                        "email" to email,
                        "name" to name,
                        "uid" to uid,
                        "registeredAt" to FieldValue.serverTimestamp()
                    )

                    db.collection("users").document(uid)
                        .set(userMap)
                        .addOnSuccessListener { Log.d("FIRESTORE", "User profile created!")

                        }
                        .addOnFailureListener { e -> Log.e("FIRESTORE", "Error adding user", e) }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Sign Up Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

//    fun registerUser(email: String, password: String) {
//        frAuth.createUserWithEmailAndPassword(email,password)
//            .addOnFailureListener {
//                Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
//            }
//            .addOnSuccessListener {
//                Toast.makeText(this, "Resistration Successfull", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this, HomeActivity::class.java)
//                startActivity(intent)
//                finish()
//            }
//    }
