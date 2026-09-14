package com.yuksholat.ui.screens.achievements

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yuksholat.data.local.AppDatabase
import com.yuksholat.data.local.entity.Achievement
import com.yuksholat.data.repository.PrayerRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AchievementsUiState(
    val achievements: List<Achievement> = emptyList(),
    val unlockedCount: Int = 0,
    val totalCount: Int = 12
)

class AchievementsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PrayerRepository
    private val _uiState = MutableStateFlow(AchievementsUiState())
    val uiState: StateFlow<AchievementsUiState> = _uiState.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = PrayerRepository(db)
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                repository.achievementsFlow,
                repository.unlockedCountFlow
            ) { achs, unlocked ->
                AchievementsUiState(
                    achievements = achs,
                    unlockedCount = unlocked,
                    totalCount = achs.size
                )
            }.collect { _uiState.value = it }
        }
    }
}
