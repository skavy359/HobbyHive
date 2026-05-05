package com.example.hobbyhive.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log

class HobbyJobService : JobService() {

    companion object {
        const val JOB_ID = 1001
        private const val TAG = "HobbyJobService"
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "Background hobby sync started")
        // Periodic sync work — update hobby stats, check milestones
        // In a production app, this would sync data or compute stats
        Thread {
            try {
                // Simulate sync work
                Thread.sleep(2000)
                Log.d(TAG, "Background hobby sync completed")
            } catch (e: InterruptedException) {
                Log.e(TAG, "Sync interrupted", e)
            } finally {
                jobFinished(params, false)
            }
        }.start()
        return true // Work is being done on a separate thread
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "Background hobby sync stopped")
        return true // Reschedule if stopped prematurely
    }
}
