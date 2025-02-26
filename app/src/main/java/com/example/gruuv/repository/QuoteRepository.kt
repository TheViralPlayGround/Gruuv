package com.example.gruuv.repository

import com.google.firebase.firestore.FirebaseFirestore

class QuoteRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val quotesCollection = firestore.collection("quotes")

    fun fetchRandomQuote(onSuccess: (String, String, String) -> Unit, onFailure: (Exception) -> Unit) {
        quotesCollection.get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val documents = snapshot.documents
                    val randomQuote = documents.random() // Randomly pick a document
                    val quote = randomQuote.getString("quote") ?: "No quote available"
                    val author = randomQuote.getString("author") ?: "Unknown Author"
                    val category = randomQuote.getString("category") ?: "General"
                    onSuccess(quote, author, category)
                } else {
                    onFailure(Exception("No quotes available in Firestore"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}

