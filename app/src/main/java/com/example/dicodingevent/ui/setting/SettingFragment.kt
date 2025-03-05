package com.example.dicodingevent.ui.setting

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.dicodingevent.databinding.FragmentSettingBinding
import com.example.dicodingevent.utils.EventWorkerNotification
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var workManager: WorkManager
    private lateinit var periodicWorkRequest: PeriodicWorkRequest

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        val switchTheme = binding.switchTheme
        val switchNotification = binding.switchNotification

        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        val settingViewModel =
            ViewModelProvider(this, SettingViewModelFactory(pref))[SettingViewModel::class.java]

        settingViewModel
            .getThemeSetting()
            .observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    switchTheme.isChecked = true
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    switchTheme.isChecked = false
                }
            }

        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            settingViewModel.saveThemeSetting(isChecked)
        }

        workManager = WorkManager.getInstance(requireContext())

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        val sharedPreferences =
            requireContext().getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
        val isNotificationActive = sharedPreferences.getBoolean("DAILY_REMINDER", false)
        switchNotification.isChecked = isNotificationActive

        settingViewModel
            .getNotificationSetting()
            .observe(viewLifecycleOwner) { isNotificationActive ->
                binding.switchNotification.isChecked = isNotificationActive
            }
        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val data =
                    Data
                        .Builder()
                        .build()
                val constraints =
                    Constraints
                        .Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                periodicWorkRequest =
                    PeriodicWorkRequest
                        .Builder(
                            EventWorkerNotification::class.java,
                            60,
                            TimeUnit.MINUTES,
                        ).setInputData(data)
                        .setConstraints(constraints)
                        .build()
                workManager.enqueue(periodicWorkRequest)
                settingViewModel.saveNotificationSetting(true)
                Toast.makeText(requireActivity(), "Notifikasi Aktif", Toast.LENGTH_SHORT).show()
            } else {
                workManager.cancelWorkById(periodicWorkRequest.id)
                settingViewModel.saveNotificationSetting(false)
                Toast
                    .makeText(requireActivity(), "Notifikasi Tidak Aktif", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            val sharedPref =
                requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val hasAskedBefore = sharedPref.getBoolean("has_asked_notification", false)

            if (!hasAskedBefore) {
                if (isGranted) {
                    Toast
                        .makeText(
                            requireActivity(),
                            "Notifications permission granted",
                            Toast.LENGTH_SHORT,
                        ).show()
                } else {
                    Toast
                        .makeText(
                            requireActivity(),
                            "Notifications permission rejected",
                            Toast.LENGTH_SHORT,
                        ).show()
                }
                sharedPref.edit().putBoolean("has_asked_notification", true).apply()
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
