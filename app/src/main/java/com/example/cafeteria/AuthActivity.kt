package com.example.cafeteria

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AuthActivity : AppCompatActivity() {

    // Using lateinit because these are initialized in onCreate
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var sapIdEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Initialize Views
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        sapIdEditText = findViewById(R.id.sapIdEditText)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        loginButton.setOnClickListener { loginUser() }
        registerButton.setOnClickListener { registerUser() }
    }

    private fun loginUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()



        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        if (email == "aeshima2004@gmail.com" ) {
            startActivity(Intent(this, AdminPageActivity::class.java))
            finish()
        }

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                mAuth.currentUser?.let { firebaseUser ->
                    val username = when {
                        !firebaseUser.displayName.isNullOrEmpty() -> firebaseUser.displayName
                        firebaseUser.email?.contains("@") == true -> firebaseUser.email!!.substringBefore("@")
                        else -> "User"
                    }

                    val intent = Intent(this, HomeActivity::class.java).apply {
                        putExtra("USERNAME", username)
                    }
                    startActivity(intent)
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Login Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }


    }

    private fun registerUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val sapId = sapIdEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || sapId.isEmpty()) {
            Toast.makeText(this, "Please enter email, password, and SAP ID", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val firebaseUser = mAuth.currentUser
                firebaseUser?.let { user ->
                    val uid = user.uid

                    // Update Profile with SAP ID
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(sapId)
                        .build()

                    user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Registration Successful with SAP ID", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Save to Firestore using a Kotlin Map
                    val userMap = hashMapOf(
                        "email" to email,
                        "sapId" to sapId,
                        "uid" to uid,
                        "registeredAt" to FieldValue.serverTimestamp()
                    )

                    db.collection("users").document(uid)
                        .set(userMap)
                        .addOnSuccessListener { Log.d("FIRESTORE", "User profile created!") }
                        .addOnFailureListener { e -> Log.e("FIRESTORE", "Error adding user", e) }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Sign Up Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}