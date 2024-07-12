package com.example.echoplex

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.echoplex.databinding.ActivityProfileBinding
import com.example.echoplex.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException

class ProfileActivity : AppCompatActivity() {

    private var binding: ActivityProfileBinding? = null
    private var auth: FirebaseAuth? = null
    private var database: FirebaseDatabase? = null
    private var storage: FirebaseStorage? = null
    private var selectedImage: Uri? = null
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding!!.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dialog = ProgressDialog(this).apply {
            setMessage("Updating Profile..")
            setCancelable(false)
        }

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        binding!!.profilePhoto.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 45)
        }

        binding!!.profileBtn.setOnClickListener {
            val name: String = binding!!.profileName.text.toString()
            val email: String = binding!!.profileEmail.text.toString()

            if (name.isEmpty()) {
                binding!!.profileName.error = "Please type your name"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding!!.profileEmail.error = "Please type your email"
                return@setOnClickListener
            }

            dialog!!.show()

            if (selectedImage != null) {
                val reference = storage!!.reference.child("Profile")
                    .child(auth!!.uid!!)

                Log.d("ProfileActivity", "Starting upload process...")

                reference.putFile(selectedImage!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("ProfileActivity", "Upload successful")

                        reference.downloadUrl.addOnCompleteListener { uriTask ->
                            if (uriTask.isSuccessful) {
                                val imageUrl = uriTask.result.toString()
                                val uid = auth!!.uid
                                val emailTemp = auth!!.currentUser!!.email
                                val nameTemp: String = binding!!.profileName.text.toString()
                                val user = User(uid, nameTemp, emailTemp, imageUrl)
                                database!!.reference
                                    .child("users")
                                    .child(uid!!)
                                    .setValue(user)
                                    .addOnCompleteListener {
                                        dialog!!.dismiss()
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                            } else {
                                dialog!!.dismiss()
                                handleFirebaseStorageError(uriTask.exception)
                            }
                        }
                    } else {
                        dialog!!.dismiss()
                        handleFirebaseStorageError(task.exception)
                    }
                }
            } else {
                dialog!!.dismiss()
                showToast("Please select an image.")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null && data.data != null) {
            selectedImage = data.data
            binding!!.profilePhoto.setImageURI(selectedImage)
        }
    }

    private fun handleFirebaseStorageError(exception: Exception?) {
        dialog!!.dismiss()
        if (exception is StorageException) {
            when (exception.errorCode) {
                StorageException.ERROR_OBJECT_NOT_FOUND -> {
                    showToast("Error: Object not found.")
                }
                StorageException.ERROR_BUCKET_NOT_FOUND -> {
                    showToast("Error: Bucket not found.")
                }
                StorageException.ERROR_PROJECT_NOT_FOUND -> {
                    showToast("Error: Project not found.")
                }
                StorageException.ERROR_QUOTA_EXCEEDED -> {
                    showToast("Error: Quota exceeded. Please try again later.")
                }
                StorageException.ERROR_NOT_AUTHENTICATED -> {
                    showToast("Error: User not authenticated. Please login again.")
                }
                StorageException.ERROR_NOT_AUTHORIZED -> {
                    showToast("Error: User not authorized to perform this action.")
                }
                else -> {
                    showToast("Unknown error occurred. Please try again.")
                }
            }
        } else {
            showToast("An error occurred: ${exception?.message}")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
