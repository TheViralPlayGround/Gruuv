package com.example.gruuv.utils

import android.content.Context
import com.example.gruuv.R
import com.example.gruuv.model.Quote
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.io.InputStreamReader

fun uploadQuotesToFirestore(context: Context) {
    val firestore = FirebaseFirestore.getInstance()
    val inputStream = context.resources.openRawResource(R.raw.quotes)
    val reader = InputStreamReader(inputStream)
    val quoteType = object : TypeToken<List<Quote>>() {}.type
    val quotes: List<Quote> = Gson().fromJson(reader, quoteType)

    for (quote in quotes) {
        val docRef = firestore.collection("quotes").document()
        docRef.set(quote)
            .addOnSuccessListener {
                println("Successfully uploaded: ${quote.quote}")
            }
            .addOnFailureListener { e ->
                println("Failed to upload: ${quote.quote}, error: $e")
            }
    }
}
