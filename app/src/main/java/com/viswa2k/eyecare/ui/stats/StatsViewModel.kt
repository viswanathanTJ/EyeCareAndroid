package com.viswa2k.eyecare.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viswa2k.eyecare.data.db.entity.DailyStats
import com.viswa2k.eyecare.domain.GetDailyStatsUseCase
import com.viswa2k.eyecare.domain.GetStreakUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class StatsViewModel(
    getDailyStatsUseCase: GetDailyStatsUseCase,
    getStreakUseCase: GetStreakUseCase
) : ViewModel() {

    val todayStats: StateFlow<DailyStats?> = getDailyStatsUseCase.today()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val weeklyStats: StateFlow<List<DailyStats>> = getDailyStatsUseCase.weekly()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentStreak: StateFlow<Int?> = getStreakUseCase.current()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val maxStreak: StateFlow<Int?> = getStreakUseCase.max()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
