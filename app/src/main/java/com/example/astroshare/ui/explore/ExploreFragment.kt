package com.example.astroshare.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.astroshare.R

class ExploreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate a layout for this fragment or return a simple view
        //Change to Safe Args
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }
}