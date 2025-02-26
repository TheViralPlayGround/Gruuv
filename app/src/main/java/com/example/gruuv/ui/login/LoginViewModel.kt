import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun login(navController: NavController) {
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please fill in all fields")
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Clear any previous error messages
                    _uiState.value = _uiState.value.copy(errorMessage = null)

                    // Navigate to the dashboard
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    // Display error message to the user
                    _uiState.value = _uiState.value.copy(
                        errorMessage = task.exception?.message ?: "An error occurred"
                    )
                }
            }

        userRepository.loginUser(email, password) { success, error ->
            if (success) {
                navController.navigate("dashboard") // Navigate to DashboardScreen on success
            } else {
                _uiState.value = _uiState.value.copy(errorMessage = error)
            }
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val errorMessage: String? = null
)
