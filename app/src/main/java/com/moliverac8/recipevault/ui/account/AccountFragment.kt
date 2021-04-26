package com.moliverac8.recipevault.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dropbox.core.android.Auth
import com.moliverac8.recipevault.databinding.FragmentAccountBinding
import com.moliverac8.recipevault.databinding.FragmentAccountInitBinding
import com.moliverac8.recipevault.framework.workmanager.BackupUserData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val prefs =
            requireContext().getSharedPreferences("recipe-vault", AppCompatActivity.MODE_PRIVATE)
        val serializedCredential = prefs.getString("credential", null)

        val binding =
            if (serializedCredential != null) FragmentAccountBinding.inflate(layoutInflater).apply {
                makeBackupBtn.setOnClickListener {
                    viewModel.makeBackup()
                }

                restoreBackupBtn.setOnClickListener {
                    viewModel.restoreBackup()
                }
            }
            else FragmentAccountInitBinding.inflate(layoutInflater).apply {
                loginBtn.setOnClickListener {
                    viewModel.loginToDropbox()
                    findNavController().popBackStack()
                }
            }

        return binding.root
    }
}