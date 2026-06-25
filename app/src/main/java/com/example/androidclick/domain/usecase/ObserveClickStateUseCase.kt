package com.example.androidclick.domain.usecase

import com.example.androidclick.service.ClickForegroundService
import com.example.androidclick.service.ClickServiceState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveClickStateUseCase @Inject constructor() {
    operator fun invoke(): StateFlow<ClickServiceState> = ClickForegroundService.state
}
