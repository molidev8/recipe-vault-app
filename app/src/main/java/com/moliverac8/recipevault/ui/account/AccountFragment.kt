package com.moliverac8.recipevault.ui.account

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import com.moliverac8.recipevault.*
import com.moliverac8.recipevault.databinding.FragmentAccountBinding
import com.moliverac8.recipevault.framework.workmanager.BackupWorkerManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private val viewModel: AccountViewModel by viewModels()
    private val prefs: SharedPreferences by lazy {
        requireContext().getSharedPreferences("recipe-vault", AppCompatActivity.MODE_PRIVATE)
    }
    private lateinit var binding: FragmentAccountBinding
    private val bottomBarView: BottomAppBar by lazy {
        requireActivity().findViewById(R.id.bottomBar)
    }
    private val newRecipeBtn: FloatingActionButton by lazy {
        requireActivity().findViewById(R.id.newRecipeBtn)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(layoutInflater)

        val firstTime = prefs.getBoolean(FIRST_TIME_LOGIN, true)

        if (!firstTime) {
            // El usuario ha iniciado sesión en Dropbox
            binding.loginGroup.visibility = View.GONE
            binding.loggedGroup.visibility = View.VISIBLE
        }

        updateMetadata(binding)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        enterTransition = MaterialFadeThrough()
        newRecipeBtn.hide()
    }

    override fun onResume() {
        super.onResume()
        val serializedCredential = prefs.getString(DROPBOX_CREDENTIAL, null)
        val firstTime = prefs.getBoolean(FIRST_TIME_LOGIN, true)

        if (serializedCredential != null) {
            viewModel.isFinished.observe(viewLifecycleOwner) { backupSize ->
                prefs.edit().putLong(BACKUP_SIZE, backupSize).apply()
                binding.progressBar.hide()
                Snackbar.make(requireView(), getString(R.string.backup_done_ok), Snackbar.LENGTH_SHORT)
                    .setAnchorView(bottomBarView).show()
                updateMetadata(binding)
            }

            viewModel.doingBackup.observe(viewLifecycleOwner) { isWorking ->
                if (isWorking) {
                    binding.progressBar.show()
                    with(prefs.edit()) {
                        val time = Calendar.getInstance()
                        putLong(LOCAL_BACKUP_TIME, time.timeInMillis).apply()
                        putLong(CLOUD_BACKUP_TIME, time.timeInMillis).apply()
                    }
                }
            }

            viewModel.doingRestore.observe(viewLifecycleOwner) { isWorking ->
                if (isWorking) {
                    binding.progressBar.show()
                } else {
                    binding.progressBar.show()
                    Snackbar.make(binding.root, getString(R.string.restore_done_ok), Snackbar.LENGTH_SHORT)
                        .setAnchorView(bottomBarView).show()
                    binding.progressBar.hide()
                }
            }

            binding.makeBackupBtn.setOnClickListener {
                viewModel.makeBackup(CoroutineExceptionHandler { _, _ ->
                    Snackbar.make(it, getString(R.string.backup_error), Snackbar.LENGTH_LONG)
                        .setAnchorView(bottomBarView)
                        .show()
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.progressBar.hide()
                    }
                })
            }

            binding.restoreBackupBtn.setOnClickListener {
                viewModel.restoreBackup(CoroutineExceptionHandler { _, _ ->
                    Snackbar.make(it, getString(R.string.backup_error), Snackbar.LENGTH_LONG)
                        .setAnchorView(bottomBarView)
                        .show()
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.progressBar.hide()
                    }
                })
            }

            binding.automaticBackupBtn.setOnClickListener {
                val interval = when (binding.timingGroup.checkedRadioButtonId) {
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
        }

        if (serializedCredential != null && firstTime) {

            viewModel.firstTimeSetup.observe(viewLifecycleOwner) {
                updateMetadata(binding)
            }

            prefs.edit().putBoolean(FIRST_TIME_LOGIN, false).apply()

            // El usuario ha iniciado sesión en Dropbox
            binding.loginGroup.visibility = View.GONE
            binding.loggedGroup.visibility = View.VISIBLE

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

    companion object {
        fun newInstance(): AccountFragment = AccountFragment()
    }
}