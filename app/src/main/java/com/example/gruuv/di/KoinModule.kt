package com.example.gruuv.di

import LoginViewModel
import UserRepository
import com.example.gruuv.repository.AchievementRepository
import com.example.gruuv.ui.dashboard.AchievementViewModel
import com.example.gruuv.viewmodel.SignUpViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Provide FirebaseFirestore instance
    single { FirebaseFirestore.getInstance() }

    // Repositories
    single { AchievementRepository(get()) }
    single { UserRepository() }

    // ViewModels
    viewModel { LoginViewModel(get()) }
    viewModel { SignUpViewModel(get()) }
    viewModel { AchievementViewModel(get()) }

    factory { AchievementViewModel(get()) }

}
