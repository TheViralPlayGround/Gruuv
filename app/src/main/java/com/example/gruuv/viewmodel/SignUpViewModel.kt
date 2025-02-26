package com.example.gruuv.viewmodel

import UserRepository
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SignUpViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun signUp(navController: NavController) {
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please fill in all fields")
            return
        }

        userRepository.registerUser(email, password) { success, error ->
            if (success) {
                navController.navigate("login") // Navigate to LoginScreen on success
            } else {
                _uiState.value = _uiState.value.copy(errorMessage = error)
            }
        }
    }
}

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val errorMessage: String? = null
)
