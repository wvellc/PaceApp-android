package net.paceapp.core.data.firestore

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// Await a Play-services Task without pulling in kotlinx-coroutines-play-services.
internal suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { cont ->
    addOnSuccessListener { cont.resume(it) }
    addOnFailureListener { cont.resumeWithException(it) }
    addOnCanceledListener { cont.cancel() }
}

// Maps a query snapshot to typed event documents, skipping malformed entries.
internal fun QuerySnapshot.toEventDocuments(): List<EventDocument> =
    documents.mapNotNull { runCatching { it.toObject(EventDocument::class.java) }.getOrNull() }
