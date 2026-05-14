package com.example.hobbyhive.appwrite

// ═══════════════════════════════════════════════════════════════════════════
// AppwriteConfig.kt
//
// ⚠️  THIS IS THE ONLY FILE YOU NEED TO EDIT WITH YOUR CREDENTIALS.
//
// How to get these values:
//  1. Go to https://cloud.appwrite.io and open your project.
//  2. PROJECT_ID  → Project Settings → General → 6a02fa5e002078771b0b
//  3. ENDPOINT    → Project Settings → General → https://fra.cloud.appwrite.io/v1
//                   (default for Appwrite Cloud: "https://cloud.appwrite.io/v1")
//  4. DATABASE_ID → Databases → your database → copy the Database ID
//  5. BUCKET_ID   → Storage → your bucket → copy the Bucket ID
//  6. Collection IDs → Databases → your database → each collection → copy ID
//
// HOW TO CREATE COLLECTIONS in Appwrite Console:
//  • "hobbies"        — attributes: userId(str), name(str), description(str),
//                       category(str), rating(float), progress(int), status(str),
//                       notes(str), imageFileId(str?), reminderEnabled(bool),
//                       reminderTime(int?), targetDate(int?), createdAt(int), updatedAt(int)
//  • "sessions"       — userId(str), hobbyId(str), durationMinutes(int),
//                       sessionDate(int), notes(str), createdAt(int)
//  • "goals"          — userId(str), hobbyId(str), title(str), description(str),
//                       targetValue(int), currentValue(int), unit(str),
//                       deadline(int?), status(str), createdAt(int), updatedAt(int)
//  • "forum_posts"    — userId(str), authorName(str), title(str), content(str),
//                       category(str), upvotes(int), repliesCount(int), createdAt(int)
//  • "forum_comments" — postId(str), userId(str), authorName(str),
//                       content(str), createdAt(int)
//  • "hobby_groups"   — name(str), description(str), category(str), membersCount(int)
//  • "profiles"       — userId(str), fullName(str), about(str), avatarFileId(str?)
//
// PERMISSIONS (recommended):
//  • hobbies / sessions / goals → Read/Write for user:{{userId}} only
//  • forum_posts / forum_comments / hobby_groups → Read for any, Write for users
//  • profiles → Read for any, Write for user:{{userId}} only
// ═══════════════════════════════════════════════════════════════════════════

object AppwriteConfig {

    // ─── Core Appwrite credentials ───────────────────────────────────────
    /** Your Appwrite Project ID — replace this with the real value */
    const val PROJECT_ID = "6a02fa5e002078771b0b"

    /** Your Appwrite API Endpoint */
    const val ENDPOINT = "https://fra.cloud.appwrite.io/v1"

    // ─── Database ────────────────────────────────────────────────────────
    /** The ID of the Appwrite Database you create for HobbyHive */
    const val DATABASE_ID = "1234"

    // ─── Collection IDs ──────────────────────────────────────────────────
    const val COLLECTION_HOBBIES         = "hobbies"
    const val COLLECTION_SESSIONS        = "sessions"
    const val COLLECTION_GOALS           = "goals"
    const val COLLECTION_FORUM_POSTS     = "forum_posts"
    const val COLLECTION_FORUM_COMMENTS  = "forum_comments"
    const val COLLECTION_HOBBY_GROUPS    = "hobby_groups"
    const val COLLECTION_PROFILES        = "profiles"

    // ─── Storage ─────────────────────────────────────────────────────────
    /** Bucket used for profile images and hobby image attachments */
    const val BUCKET_HOBBY_IMAGES = "6a05fbaf0008b1ab44c6"
}
