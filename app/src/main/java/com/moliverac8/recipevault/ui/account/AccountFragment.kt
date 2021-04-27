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
import com.google.android.material.snackbar.Snackbar
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.databinding.FragmentAccountBinding
import com.moliverac8.recipevault.databinding.FragmentAccountInitBinding
import com.moliverac8.recipevault.framework.workmanager.BackupUserData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler

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
                    viewModel.makeBackup(CoroutineExceptionHandler { _, _ ->
                        Snackbar.make(it, getString(R.string.backup_error), Snackbar.LENGTH_LONG)
                            .show()
                    })
                }

                restoreBackupBtn.setOnClickListener {
                    viewModel.restoreBackup(CoroutineExceptionHandler { _, _ ->
                        Snackbar.make(it, getString(R.string.backup_error), Snackbar.LENGTH_LONG)
                            .show()
                    })
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