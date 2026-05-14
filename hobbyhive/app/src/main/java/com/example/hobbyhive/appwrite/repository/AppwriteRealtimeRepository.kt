package com.example.hobbyhive.appwrite.repository

import com.example.hobbyhive.appwrite.AppwriteClient
import com.example.hobbyhive.appwrite.AppwriteConfig
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.RealtimeSubscription
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

// ═══════════════════════════════════════════════════════════════════════════
// AppwriteRealtimeRepository.kt
//
// Wraps Appwrite Realtime subscriptions into Kotlin Flows.
//
// HOW APPWRITE REALTIME WORKS:
//   • You subscribe to a "channel" like "databases.{dbId}.collections.{colId}.documents"
//   • Appwrite pushes events over a WebSocket whenever documents change
//   • Event types: create, update, delete (embedded in RealtimeResponseEvent.events list)
//
// USAGE IN VIEWMODEL:
//   private var realtimeJob: Job? = null
//
//   fun startListening() {
//       realtimeJob = viewModelScope.launch {
//           realtimeRepository.subscribeToForumPosts().collect { event ->
//               when (event.type) {
//                   RealtimeEvent.Type.CREATE -> { /* prepend new post */ }
//                   RealtimeEvent.Type.UPDATE -> { /* update post in list */ }
//                   RealtimeEvent.Type.DELETE -> { /* remove post from list */ }
//               }
//           }
//       }
//   }
//
//   override fun onCleared() {
//       realtimeJob?.cancel()   // Always cancel to avoid memory leaks
//   }
// ═══════════════════════════════════════════════════════════════════════════

/** Simplified event emitted to ViewModels — wraps the raw Appwrite payload. */
data class RealtimeEvent(
    val type: Type,
    val payload: Map<String, Any>  // Raw document attribute map
) {
    enum class Type { CREATE, UPDATE, DELETE, UNKNOWN }
}

class AppwriteRealtimeRepository {

    private val realtime get() = AppwriteClient.realtime

    // ─── Forum Posts — Realtime ──────────────────────────────────────────

    /**
     * Subscribe to live changes in the forum_posts collection.
     * Emits [RealtimeEvent] whenever a post is created, updated, or deleted.
     *
     * This Flow uses [callbackFlow] to bridge the Appwrite callback API into
     * a cold Kotlin Flow that cancels cleanly when the collector scope ends.
     */
    fun subscribeToForumPosts(): Flow<RealtimeEvent> = callbackFlow {
        val channel = buildCollectionChannel(AppwriteConfig.COLLECTION_FORUM_POSTS)

        // Subscribe and forward events into the Flow
        val subscription: RealtimeSubscription = realtime.subscribe(channel) { response ->
            val event = parseEvent(response.events, response.payload)
            trySend(event)   // Non-blocking send to Flow collector
        }

        // awaitClose is called when the Flow collector cancels (e.g. ViewModel.onCleared)
        awaitClose {
            subscription.close()   // Clean up the WebSocket subscription
        }
    }

    // ─── Hobbies — Realtime ──────────────────────────────────────────────

    /**
     * Subscribe to changes in the hobbies collection for a specific user.
     * Useful for multi-device sync: if the user edits a hobby on another
     * device, this Flow picks it up automatically.
     */
    fun subscribeToHobbies(): Flow<RealtimeEvent> = callbackFlow {
        val channel = buildCollectionChannel(AppwriteConfig.COLLECTION_HOBBIES)

        val subscription: RealtimeSubscription = realtime.subscribe(channel) { response ->
            trySend(parseEvent(response.events, response.payload))
        }

        awaitClose { subscription.close() }
    }

    // ─── Forum Comments — Realtime ───────────────────────────────────────

    /**
     * Subscribe to new comments in the forum_comments collection.
     * Useful on the Post Detail screen so comments appear live.
     */
    fun subscribeToComments(): Flow<RealtimeEvent> = callbackFlow {
        val channel = buildCollectionChannel(AppwriteConfig.COLLECTION_FORUM_COMMENTS)

        val subscription: RealtimeSubscription = realtime.subscribe(channel) { response ->
            trySend(parseEvent(response.events, response.payload))
        }

        awaitClose { subscription.close() }
    }

    // ─── Private helpers ─────────────────────────────────────────────────

    /**
     * Builds the Appwrite channel string for a collection.
     * Format: "databases.{databaseId}.collections.{collectionId}.documents"
     */
    private fun buildCollectionChannel(collectionId: String): String {
        return "databases.${AppwriteConfig.DATABASE_ID}.collections.$collectionId.documents"
    }

    /**
     * Maps the raw Appwrite event string list to our simplified [RealtimeEvent.Type].
     * Appwrite event strings look like:
     *   "databases.*.collections.*.documents.*.create"
     *   "databases.*.collections.*.documents.*.update"
     *   "databases.*.collections.*.documents.*.delete"
     */
    @Suppress("UNCHECKED_CAST")
    private fun parseEvent(
        events: Collection<String>,
        payload: Any
    ): RealtimeEvent {
        val type = when {
            events.any { it.endsWith(".create") } -> RealtimeEvent.Type.CREATE
            events.any { it.endsWith(".update") } -> RealtimeEvent.Type.UPDATE
            events.any { it.endsWith(".delete") } -> RealtimeEvent.Type.DELETE
            else                                   -> RealtimeEvent.Type.UNKNOWN
        }
        return RealtimeEvent(type = type, payload = payload as? Map<String, Any> ?: emptyMap())
    }
}
