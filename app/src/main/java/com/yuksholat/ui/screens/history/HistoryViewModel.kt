package com.yuksholat.ui.screens.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yuksholat.data.local.AppDatabase
import com.yuksholat.data.local.entity.PrayerLog
import com.yuksholat.data.local.entity.TierConfig
import com.yuksholat.data.local.entity.UserProfile
import com.yuksholat.data.model.PrayerTimesResult
import com.yuksholat.data.model.PrayerType
import com.yuksholat.data.repository.PrayerRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HistoryUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val profile: UserProfile = UserProfile(),
    val selectedDateLogs: List<PrayerLog> = emptyList(),
    val prayerTimes: PrayerTimesResult? = null,
    val totalPrayersLogged: Int = 0,
    val onTimePercentage: Int = 100,
    val currentStreakDays: Int = 0,
    val tierConfigs: List<TierConfig> = TierConfig.DEFAULT_TIERS
)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PrayerRepository
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = PrayerRepository(db)

        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            _selectedDate.flatMapLatest { date ->
                combine(
                    repository.profileFlow.filterNotNull(),
                    repository.getLogsForDateFlow(date),
                    repository.totalLogsCountFlow,
                    repository.onTimeLogsCountFlow,
                    repository.allTiersFlow
                ) { profile, logs, totalLogs, onTimeLogs, tiers ->
                    val times = repository.calculatePrayerTimesForDate(profile, date)
                    val onTimePct = if (totalLogs > 0) ((onTimeLogs.toDouble() / totalLogs.toDouble()) * 100).toInt() else 100
                    val streak = repository.calculateStreak()

                    HistoryUiState(
                        selectedDate = date,
                        profile = profile,
                        selectedDateLogs = logs,
                        prayerTimes = times,
                        totalPrayersLogged = totalLogs,
                        onTimePercentage = onTimePct,
                        currentStreakDays = streak,
                        tierConfigs = tiers.ifEmpty { TierConfig.DEFAULT_TIERS }
                    )
                }
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun manualLogPrayer(prayerType: PrayerType) {
        viewModelScope.launch {
            repository.recordPrayer(
                prayerType = prayerType,
                targetDate = _selectedDate.value
            )
        }
    }
}
