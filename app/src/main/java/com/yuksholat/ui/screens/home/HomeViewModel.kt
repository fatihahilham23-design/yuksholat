package com.yuksholat.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yuksholat.alarm.PrayerAlarmScheduler
import com.yuksholat.audio.RetroSoundSynthesizer
import com.yuksholat.data.local.AppDatabase
import com.yuksholat.data.local.entity.Achievement
import com.yuksholat.data.local.entity.PrayerLog
import com.yuksholat.data.local.entity.TierConfig
import com.yuksholat.data.local.entity.UserProfile
import com.yuksholat.data.model.PrayerTimesResult
import com.yuksholat.data.model.PrayerType
import com.yuksholat.data.repository.PrayerRepository
import com.yuksholat.data.util.RamadanDetector
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class HomeUiState(
    val profile: UserProfile = UserProfile(),
    val todayLogs: List<PrayerLog> = emptyList(),
    val tierConfigs: List<TierConfig> = TierConfig.DEFAULT_TIERS,
    val prayerTimes: PrayerTimesResult? = null,
    val currentPrayer: PrayerType = PrayerType.DZUHUR,
    val nextPrayer: PrayerType = PrayerType.ASHAR,
    val nextPrayerTime: String = "--:--",
    val countdownFormatted: String = "00:00:00",
    val isCurrentPrayerLogged: Boolean = false,

    // Animation state
    val isAbsenInProgress: Boolean = false,
    val characterWalkFrame: Int = 0,
    val characterXProgress: Float = 0f, // 0f (idle) -> 1f (mosque door)
    val isInsideMosque: Boolean = false,
    val isFacingRight: Boolean = true,
    val animationStatusText: String = "",

    // Reward popups
    val showFloatingPoints: Boolean = false,
    val lastPointsEarned: Int = 0,
    val lastIsOnTime: Boolean = true,

    // NEW: Streak
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,

    // NEW: Daily Goal
    val dailyGoalProgress: Int = 0,  // 0-5
    val dailyGoalCompleted: Boolean = false,

    // NEW: Ramadan
    val isRamadan: Boolean = false,
    val ramadanDay: Int = 0,
    val ramadanDaysLeft: Int = 0,

    // NEW: Transformation (replaces showLevelUpDialog/newTierLevel/newTierName)
    val showTransformationDialog: Boolean = false,
    val transformationTierLevel: Int = 1,
    val transformationTierName: String = "",
    val transformationTierDescription: String = "",
    val transformationAuraColor: String = "#FFFFFF",

    // NEW: Achievements unlocked this session
    val newlyUnlockedAchievements: List<Achievement> = emptyList(),
    val showAchievementToast: Boolean = false,

    // Onboarding
    val showOnboardingGuide: Boolean = false,
    val showNameInputDialog: Boolean = false,

    // Tier & Disappointment
    val showTierInfoDialog: Boolean = false,
    val showDisappointmentDialog: Boolean = false
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PrayerRepository
    private val alarmScheduler = PrayerAlarmScheduler(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = PrayerRepository(database)

        observeData()
        startLiveCountdownTimer()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                repository.profileFlow.filterNotNull(),
                repository.getLogsForDateFlow(LocalDate.now()),
                repository.allTiersFlow,
                repository.achievementsFlow
            ) { profile, logs, tiers, achievements ->
                RetroSoundSynthesizer.isSoundEnabled = profile.soundEnabled

                val todayTimes = repository.calculatePrayerTimesForDate(profile, LocalDate.now())
                val nowTime = LocalTime.now()
                val curPrayer = todayTimes.getCurrentPrayerWindow(nowTime)
                val (nextP, nextPTime) = todayTimes.getNextPrayer(nowTime)

                val isLogged = logs.any { it.prayerName.equals(curPrayer.id, ignoreCase = true) }

                // Pengecekan Kekecewaan (Missed 3 prayers)
                val missedCount = PrayerType.entries.count { p ->
                    val pTime = todayTimes.getTimeFor(p)
                    LocalTime.now().isAfter(pTime.plusMinutes(30)) &&
                            logs.none { it.prayerName.equals(p.id, ignoreCase = true) }
                }

                // Schedule alarm bila belum
                if (profile.alarmEnabled) {
                    alarmScheduler.scheduleAllPrayerAlarms(todayTimes)
                }

                // Ramadan detection
                val today = LocalDate.now()
                val isRamadanNow = RamadanDetector.isRamadan(today)
                val ramadanDayNum = RamadanDetector.ramadanDayNumber(today)
                val ramadanDaysLeftNum = RamadanDetector.daysLeftInRamadan(today)

                // Daily goal from today's logs
                val dailyGoalProgress = logs.size.coerceAtMost(5)
                val dailyGoalCompleted = logs.size >= 5

                // Streak from profile fields
                val currentStreakVal = profile.currentStreak
                val bestStreakVal = profile.bestStreak

                _uiState.update { current ->
                    current.copy(
                        profile = profile,
                        todayLogs = logs,
                        tierConfigs = tiers.ifEmpty { TierConfig.DEFAULT_TIERS },
                        prayerTimes = todayTimes,
                        currentPrayer = curPrayer,
                        nextPrayer = nextP,
                        nextPrayerTime = todayTimes.getFormattedTime(nextP),
                        isCurrentPrayerLogged = isLogged,
                        currentStreak = currentStreakVal,
                        bestStreak = bestStreakVal,
                        dailyGoalProgress = dailyGoalProgress,
                        dailyGoalCompleted = dailyGoalCompleted,
                        isRamadan = isRamadanNow,
                        ramadanDay = ramadanDayNum,
                        ramadanDaysLeft = ramadanDaysLeftNum,
                        showOnboardingGuide = !profile.onboardingCompleted,
                        showNameInputDialog = profile.name.isEmpty(),
                        showDisappointmentDialog = missedCount >= 3 && !profile.onboardingCompleted
                    )
                }
            }.collect()
        }
    }

    fun showTierInfo() {
        _uiState.update { it.copy(showTierInfoDialog = true) }
    }

    fun dismissTierInfo() {
        _uiState.update { it.copy(showTierInfoDialog = false) }
    }

    fun dismissDisappointment() {
        _uiState.update { it.copy(showDisappointmentDialog = false) }
    }

    fun setProfileName(name: String) {
        viewModelScope.launch {
            repository.updateName(name)
            _uiState.update { it.copy(showNameInputDialog = false) }
        }
    }

    fun resetUserProgress() {
        viewModelScope.launch {
            repository.resetProgress()
            // Reset local UI state properties that might not be synced immediately via Flow
            _uiState.update { it.copy(characterXProgress = 0f, isInsideMosque = false) }
            RetroSoundSynthesizer.playCoinSound() // feedback
        }
    }

    private fun startLiveCountdownTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val now = LocalDateTime.now()
                val times = _uiState.value.prayerTimes
                if (times != null) {
                    val nextPrayer = _uiState.value.nextPrayer
                    val targetTime = times.getTimeFor(nextPrayer)
                    var targetDateTime = LocalDateTime.of(LocalDate.now(), targetTime)

                    if (targetDateTime.isBefore(now)) {
                        targetDateTime = targetDateTime.plusDays(1)
                    }

                    val duration = Duration.between(now, targetDateTime)
                    val seconds = duration.seconds.coerceAtLeast(0)
                    val h = seconds / 3600
                    val m = (seconds % 3600) / 60
                    val s = seconds % 60
                    val formatted = String.format("%02d:%02d:%02d", h, m, s)

                    _uiState.update { it.copy(countdownFormatted = formatted) }
                }
                delay(1000)
            }
        }
    }

    /**
     * Alur Absen Shalat:
     * 1. Karakter jalan kanan ke pintu masjid
     * 2. Masuk masjid & status 'Sedang Shalat di Masjid...'
     * 3. Delay 2s (shalat)
     * 4. Keluar masjid & jalan kembali ke posisi awal
     * 5. Pop up +100 EXP & simpan ke DB
     * 6. Cek level up tier
     */
    fun performAbsenShalat() {
        val currentState = _uiState.value
        if (currentState.isAbsenInProgress) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAbsenInProgress = true,
                    isFacingRight = true,
                    animationStatusText = "Menuju Masjid..."
                )
            }

            // 1. Jalan ke pintu masjid (0.0 -> 1.0)
            val steps = 15
            for (i in 1..steps) {
                val progress = i.toFloat() / steps.toFloat()
                _uiState.update {
                    it.copy(
                        characterXProgress = progress,
                        characterWalkFrame = i
                    )
                }
                delay(60)
            }

            // 2. Masuk masjid
            _uiState.update {
                it.copy(
                    isInsideMosque = true,
                    animationStatusText = "Sedang Shalat di Masjid..."
                )
            }

            // 3. Waktu Shalat di dalam masjid (2 detik)
            RetroSoundSynthesizer.playPrayerChime()
            delay(2000)

            // 4. Keluar masjid & jalan balik (1.0 -> 0.0)
            _uiState.update {
                it.copy(
                    isInsideMosque = false,
                    isFacingRight = false,
                    animationStatusText = "Selesai Shalat!"
                )
            }

            for (i in 1..steps) {
                val progress = 1f - (i.toFloat() / steps.toFloat())
                _uiState.update {
                    it.copy(
                        characterXProgress = progress,
                        characterWalkFrame = i
                    )
                }
                delay(60)
            }

            // 5. Simpan ke database & kalkulasi poin
            val targetPrayer = _uiState.value.currentPrayer
            val result = repository.recordPrayer(
                prayerType = targetPrayer,
                targetDate = LocalDate.now(),
                currentTime = LocalTime.now()
            )

            // Tandai onboarding selesai bila ini sesi pertama
            if (!_uiState.value.profile.onboardingCompleted) {
                repository.setOnboardingCompleted(true)
            }

            // Mainkan audio koin
            RetroSoundSynthesizer.playCoinSound()

            // 6. Tampilkan efek poin, update streak & daily goal, cek kenaikan tier
            _uiState.update {
                it.copy(
                    isAbsenInProgress = false,
                    characterXProgress = 0f,
                    characterWalkFrame = 0,
                    isFacingRight = true,
                    animationStatusText = "",
                    showFloatingPoints = true,
                    lastPointsEarned = result.pointsEarned,
                    lastIsOnTime = result.isOnTime,
                    showOnboardingGuide = false,
                    currentStreak = result.newStreak,
                    dailyGoalProgress = result.dailyGoalProgress,
                    dailyGoalCompleted = result.dailyGoalCompleted
                )
            }

            // Achievement toast
            if (result.unlockedAchievements.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        showAchievementToast = true,
                        newlyUnlockedAchievements = result.unlockedAchievements
                    )
                }
            }

            if (result.didLevelUp) {
                delay(800)

                // Get tier config for the new tier
                val tier = _uiState.value.tierConfigs.firstOrNull { it.tierLevel == result.newTierLevel }

                RetroSoundSynthesizer.playTransformCharge()

                _uiState.update {
                    it.copy(
                        showTransformationDialog = true,
                        transformationTierLevel = result.newTierLevel,
                        transformationTierName = result.newTierName,
                        transformationTierDescription = tier?.description ?: "",
                        transformationAuraColor = tier?.badgeColorHex ?: "#FFFFFF"
                    )
                }

                delay(800)
                RetroSoundSynthesizer.playTransformFlash()
            }
        }
    }

    fun dismissFloatingPoints() {
        _uiState.update { it.copy(showFloatingPoints = false) }
    }

    fun dismissTransformationDialog() {
        _uiState.update { it.copy(showTransformationDialog = false) }
        viewModelScope.launch { repository.setFirstLevelUpCompleted() }
    }

    fun dismissAchievementToast() {
        _uiState.update { it.copy(showAchievementToast = false, newlyUnlockedAchievements = emptyList()) }
    }

    fun dismissOnboardingGuide() {
        viewModelScope.launch {
            repository.setOnboardingCompleted(true)
            _uiState.update { it.copy(showOnboardingGuide = false) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
