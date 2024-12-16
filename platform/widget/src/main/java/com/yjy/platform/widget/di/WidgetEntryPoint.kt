package com.yjy.platform.widget.di

import com.yjy.data.challenge.api.ChallengePreferencesRepository
import com.yjy.domain.GetStartedChallengesUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun getStartedChallengesUseCase(): GetStartedChallengesUseCase
    fun challengePreferencesRepository(): ChallengePreferencesRepository
}
