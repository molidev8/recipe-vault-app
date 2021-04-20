package com.moliverac8.recipevault.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.moliverac8.recipevault.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(layoutInflater)


        binding.makeBackupBtn.setOnClickListener {
        }

        binding.restoreBackupBtn.setOnClickListener {
        }

        return binding.root
    }
}