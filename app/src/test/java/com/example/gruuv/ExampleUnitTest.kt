package com.example.gruuv

import com.example.gruuv.repository.AchievementRepository
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun updateAchievementEffort_correctlyUpdatesFirestore() {
        // Arrange
        val firestoreMock: FirebaseFirestore = mock()
        val collectionMock: CollectionReference = mock()
        val documentMock: DocumentReference = mock()
        val updateTaskMock: Task<Void> = mock()
        val getTaskMock: Task<DocumentSnapshot> = mock()
        val documentSnapshotMock: DocumentSnapshot = mock()

        // Mock Firestore structure
        whenever(firestoreMock.collection("users")).thenReturn(collectionMock)
        whenever(collectionMock.document("testUserId")).thenReturn(documentMock)
        whenever(documentMock.collection("achievements")).thenReturn(collectionMock)
        whenever(collectionMock.document("testAchievementId")).thenReturn(documentMock)

        // Mock the `update()` method
        whenever(documentMock.update(any<Map<String, Any>>())).thenReturn(updateTaskMock)
        whenever(updateTaskMock.addOnSuccessListener(any())).thenAnswer { invocation ->
            val listener = invocation.arguments[0] as OnSuccessListener<Void>
            listener.onSuccess(null) // Simulate successful update
            updateTaskMock
        }
        whenever(updateTaskMock.addOnFailureListener(any())).thenAnswer { updateTaskMock }

        // Mock the `get()` method
        whenever(documentMock.get()).thenReturn(getTaskMock)
        whenever(getTaskMock.addOnSuccessListener(any())).thenAnswer { invocation ->
            val listener = invocation.arguments[0] as OnSuccessListener<DocumentSnapshot>
            listener.onSuccess(documentSnapshotMock) // Simulate successful retrieval
            getTaskMock
        }
        whenever(getTaskMock.addOnFailureListener(any())).thenAnswer { getTaskMock }

        // Mock the returned DocumentSnapshot
        whenever(documentSnapshotMock.exists()).thenReturn(true)

        val repository = AchievementRepository(firestoreMock)

        // Act
        repository.updateAchievementEffort("testAchievementId", 8, "testUserId") { success ->
            assert(success)
        }

        // Assert
        val expectedMap = mapOf(
            "effort" to 8,
            "effortHistory" to mapOf("2024-12-29" to 8) // Replace with the appropriate test date
        )
        verify(documentMock).update(expectedMap)
    }
}
