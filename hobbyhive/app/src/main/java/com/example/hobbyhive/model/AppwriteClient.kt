package com.example.hobbyhive.model

import android.content.Context
import android.util.Log
import com.example.hobbyhive.constants.APPWRITE_PROJECT_ID
import com.example.hobbyhive.constants.APPWRITE_PUBLIC_ENDPOINT
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases

object AppwriteClient {

    lateinit var client: Client
    lateinit var account: Account
    lateinit var databases: Databases

    fun init(context: Context) {
        client = Client(context)
            .setEndpoint(APPWRITE_PUBLIC_ENDPOINT)
            .setProject(APPWRITE_PROJECT_ID)

        account   = Account(client)
        databases = Databases(client)
        
        Log.d("AppwriteClient", "Initialized with Project ID: $APPWRITE_PROJECT_ID")
    }
}