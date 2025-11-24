package com.example.truequesperu.Services

import com.example.truequesperu.Models.Alert
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class AlertService {

    private val db = FirebaseFirestore.getInstance()
    private val alertRef = db.collection("alerts")

    fun sendAlert(alert: Alert, onComplete: (Boolean) -> Unit) {
        val docId = alertRef.document().id
        val alertToSave = alert.copy(id = docId)

        alertRef.document(docId)
            .set(alertToSave)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun listenAlerts(callback: (List<Alert>) -> Unit): ListenerRegistration {
        return alertRef
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null) {
                    callback(emptyList())
                    return@addSnapshotListener
                }

                val list = snapshots.documents.mapNotNull { doc ->
                    doc.toObject(Alert::class.java)
                }

                callback(list)
            }
    }
}