package com.example.echoplex

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Directly start ProfileActivity
        startActivity(Intent(activity, ProfileActivity::class.java))
        // Finish the current fragment to avoid showing it
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()

        // Return null since the fragment won't be displayed
        return null
    }
}
