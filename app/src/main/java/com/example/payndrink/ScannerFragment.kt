package com.example.payndrink

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation

/**
 * A simple [Fragment] subclass.
 * Use the [ScannerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScannerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scanner, container, false)
    }

    /** Set listener for skip button */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button=view.findViewById<Button>(R.id.btnSkip)
        button.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_scannerFragment_to_mainFragment)
        }
    }

}