package com.moliverac8.recipevault.ui.account

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.moliverac8.recipevault.*
import com.moliverac8.recipevault.databinding.FragmentAccountBinding
import com.moliverac8.recipevault.framework.workmanager.BackupWorkerManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import java.util.*

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private val viewModel: AccountViewModel by viewModels()
    private val prefs: SharedPreferences by lazy {
        requireContext().getSharedPreferences("recipe-vault", AppCompatActivity.MODE_PRIVATE)
    }
    private lateinit var binding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val serializedCredential = prefs.getString(DROPBOX_CREDENTIAL, null)

        binding = FragmentAccountBinding.inflate(layoutInflater)

        if (serializedCredential != null) {
            viewModel.doingBackup.observe(viewLifecycleOwner) { isWorking ->
                if (isWorking) {
                    // Progress bar goes
                    with(prefs.edit()) {
                        val time = Calendar.getInstance()
                        putLong(LOCAL_BACKUP_TIME, time.timeInMillis).apply()
                        putLong(CLOUD_BACKUP_TIME, time.timeInMillis).apply()
                    }
                }
            }

            viewModel.isFinished.observe(viewLifecycleOwner) { backupSize ->
                prefs.edit().putLong(BACKUP_SIZE, backupSize).apply()
                // Progress bar ends
                updateMetadata(binding)
            }

            binding.makeBackupBtn.setOnClickListener {
                viewModel.makeBackup(CoroutineExceptionHandler { _, _ ->
                    Snackbar.make(it, getString(R.string.backup_error), Snackbar.LENGTH_LONG)
                        .show()
                })
            }

            binding.restoreBackupBtn.setOnClickListener {
                viewModel.restoreBackup(CoroutineExceptionHandler { _, _ ->
                    Snackbar.make(it, getString(R.string.backup_error), Snackbar.LENGTH_LONG)
                        .show()
                })
            }

            binding.automaticBackupBtn.setOnClickListener {
                val interval = when(binding.timingGroup.checkedRadioButtonId) {
                    binding.month.id -> 30L
                    binding.week.id -> 7L
                    binding.day.id -> 1L
                    else -> 30L
                }
                BackupWorkerManager(requireContext()).launchWorker(interval)
            }

        } else {
            binding.loginBtn.setOnClickListener {
                viewModel.loginToDropbox()
            }

            binding.lastLocalDate.visibility = View.GONE
            binding.lastCloudDate.visibility = View.GONE
            binding.makeBackupBtn.visibility = View.GONE
            binding.restoreBackupBtn.visibility = View.GONE
            binding.size.visibility = View.GONE
            binding.loginBtn.visibility = View.VISIBLE
        }

        updateMetadata(binding)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val serializedCredential = prefs.getString(DROPBOX_CREDENTIAL, null)
        val firstTime = prefs.getBoolean(FIRST_TIME_LOGIN, true)

        if (serializedCredential != null && firstTime) {

            viewModel.firstTimeSetup.observe(viewLifecycleOwner) {
                updateMetadata(binding)
            }

            prefs.edit().putBoolean(FIRST_TIME_LOGIN, false).apply()

            // El usuario ha iniciado sesión en Dropbox
            binding.lastLocalDate.visibility = View.VISIBLE
            binding.lastCloudDate.visibility = View.VISIBLE
            binding.makeBackupBtn.visibility = View.VISIBLE
            binding.restoreBackupBtn.visibility = View.VISIBLE
            binding.size.visibility = View.VISIBLE
            binding.loginBtn.visibility = View.GONE

            viewModel.saveSizeOfCloudBackup(prefs)
            viewModel.saveDateOfLastBackup(prefs)
        }
    }

    private fun updateMetadata(binding: FragmentAccountBinding) {
        val cal = Calendar.getInstance()
        binding.local = cal.apply {
            timeInMillis = prefs.getLong(LOCAL_BACKUP_TIME, 0L)
        }.time

        binding.cloud = cal.apply {
            timeInMillis = prefs.getLong(CLOUD_BACKUP_TIME, 0L)
        }.time

        binding.sizeInBytes = prefs.getLong(BACKUP_SIZE, 0L)
    }
}