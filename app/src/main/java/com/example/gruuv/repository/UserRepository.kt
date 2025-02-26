import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        initializeUserInFirestore(user.uid) // Ensure Firestore setup on login
                    }
                    onResult(true, null) // Login successful
                } else {
                    val errorMessage = task.exception?.message ?: "An error occurred"
                    onResult(false, errorMessage)
                }
            }
    }

    fun registerUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        initializeUserInFirestore(user.uid) // Ensure Firestore setup on registration
                    }
                    onResult(true, null) // Registration successful
                } else {
                    val errorMessage = task.exception?.message ?: "An error occurred"
                    onResult(false, errorMessage)
                }
            }
    }

    private fun initializeUserInFirestore(userId: String) {
        val userDocument = firestore.collection("users").document(userId)
        userDocument.get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    // Initialize default collections and data for new users
                    val defaultData = mapOf(
                        "createdAt" to System.currentTimeMillis(),
                        "achievements" to emptyList<Map<String, Any>>(), // Placeholder
                        "history" to emptyMap<String, Any>() // Placeholder
                    )
                    userDocument.set(defaultData)
                        .addOnSuccessListener {
                            // User setup successfully
                            println("User initialized in Firestore: $userId")
                        }
                        .addOnFailureListener { e ->
                            println("Error initializing user in Firestore: $e")
                        }
                }
            }
            .addOnFailureListener { e ->
                println("Error checking user existence in Firestore: $e")
            }
    }

    fun logoutUser(onResult: (Boolean, String?) -> Unit) {
        try {
            firebaseAuth.signOut()
            onResult(true, null) // Logout successful
        } catch (e: Exception) {
            onResult(false, e.message ?: "An error occurred")
        }
    }

    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}
