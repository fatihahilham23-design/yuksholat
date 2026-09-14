package com.yuksholat.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yuksholat.alarm.PrayerAlarmScheduler
import com.yuksholat.audio.RetroSoundSynthesizer
import com.yuksholat.data.local.AppDatabase
import com.yuksholat.data.local.entity.UserProfile
import com.yuksholat.data.model.CityPreset
import com.yuksholat.data.repository.PrayerRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class SettingsUiState(
    val profile: UserProfile = UserProfile(),
    val availableCities: List<CityPreset> = CityPreset.INDONESIAN_CITIES,
    val selectedCity: CityPreset = CityPreset.DEFAULT_CITY
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PrayerRepository
    private val alarmScheduler = PrayerAlarmScheduler(application)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = PrayerRepository(db)

        viewModelScope.launch {
            repository.profileFlow.filterNotNull().collect { profile ->
                val city = CityPreset.INDONESIAN_CITIES.firstOrNull { it.cityName == profile.cityName } 
                    ?: CityPreset.DEFAULT_CITY

                RetroSoundSynthesizer.isSoundEnabled = profile.soundEnabled

                _uiState.update {
                    it.copy(
                        profile = profile,
                        selectedCity = city
                    )
                }
            }
        }
    }

    fun selectCity(city: CityPreset) {
        viewModelScope.launch {
            repository.updateLocation(
                cityName = city.cityName,
                lat = city.latitude,
                lng = city.longitude,
                tz = city.timezoneOffset
            )
            // Re-schedule alarm dengan koordinat baru
            val profile = repository.getProfile()
            val times = repository.calculatePrayerTimesForDate(profile, LocalDate.now())
            if (profile.alarmEnabled) {
                alarmScheduler.scheduleAllPrayerAlarms(times)
            }
        }
    }

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSoundAndAlarm(sound = enabled, alarm = _uiState.value.profile.alarmEnabled)
            RetroSoundSynthesizer.isSoundEnabled = enabled
        }
    }

    fun toggleAlarm(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSoundAndAlarm(sound = _uiState.value.profile.soundEnabled, alarm = enabled)
            if (enabled) {
                val profile = repository.getProfile()
                val times = repository.calculatePrayerTimesForDate(profile, LocalDate.now())
                alarmScheduler.scheduleAllPrayerAlarms(times)
            } else {
                alarmScheduler.cancelAllAlarms()
            }
        }
    }

    fun testSoundEffect() {
        RetroSoundSynthesizer.playCoinSound()
    }

    fun testFanfare() {
        RetroSoundSynthesizer.playLevelUpFanfare()
    }
}
