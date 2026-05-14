package com.example.hobbyhive.appwrite

import android.content.Context
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases
import io.appwrite.services.Realtime
import io.appwrite.services.Storage

// ═══════════════════════════════════════════════════════════════════════════
// AppwriteClient.kt
//
// Singleton that initialises the Appwrite SDK Client and lazily exposes
// the four service objects (Account, Databases, Storage, Realtime).
//
// Usage:
//   1. Call AppwriteClient.init(context) once in HobbyHiveApp.onCreate()
//   2. Anywhere else: AppwriteClient.account / .databases / .storage / .realtime
// ═══════════════════════════════════════════════════════════════════════════

object AppwriteClient {

    // The underlying Appwrite client — lateinit so it is set only once
    private lateinit var client: Client
    lateinit var context: Context
        private set

    // ─── Service accessors ───────────────────────────────────────────────
    // These are lazy so they are only created after init() is called.

    /** Appwrite Account service — auth (sign-up, login, logout, session) */
    val account: Account by lazy { Account(client) }

    /** Appwrite Databases service — CRUD operations on all collections */
    val databases: Databases by lazy { Databases(client) }

    /** Appwrite Storage service — file upload / download / delete */
    val storage: Storage by lazy { Storage(client) }

    /** Appwrite Realtime service — live subscriptions to document changes */
    val realtime: Realtime by lazy { Realtime(client) }

    // ────────────────────────────────────────────────────────────────────
    /**
     * Must be called exactly once in [HobbyHiveApp.onCreate].
     * Sets the endpoint and project ID from [AppwriteConfig].
     */
    fun init(context: Context) {
        this.context = context.applicationContext
        client = Client(this.context)
        client
            .setEndpoint(AppwriteConfig.ENDPOINT)   // e.g. "https://cloud.appwrite.io/v1"
            .setProject(AppwriteConfig.PROJECT_ID)  // Your Appwrite project ID
            .setSelfSigned(false)                    // Set to true only for local dev with self-signed cert
    }
}
