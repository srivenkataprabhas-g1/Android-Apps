/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.waterme.data

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.waterme.model.Plant
import com.example.waterme.worker.WaterReminderWorker
import java.util.concurrent.TimeUnit

class WorkManagerWaterRepository(context: Context) : WaterRepository {
    private val workManager = WorkManager.getInstance(context)

    override val plants: List<Plant>
        get() = DataSource.plants

    override fun scheduleReminder(duration: Long, unit: TimeUnit, plantName: String) {
        /* Step-1:Create a variable called data with Data.Builder.
        The data needs to consist of a single string value where WaterReminderWorker.
        nameKey is the key and the plantName passed into scheduleReminder() is the value.
        */
        val data = Data.Builder()
            .putString(WaterReminderWorker.nameKey, plantName)
            .build()
        /* Step-2:Create a one-time work request with the WaterReminderWorker class.
        Use the duration and unit passed into the scheduleReminder() function and
        set the input data to the data variable you create.
        */
        val workRequest = OneTimeWorkRequestBuilder<WaterReminderWorker>()
            .setInputData(data)
            .setInitialDelay(duration, unit)
            .build()
        /* Step-3:Call the workManager's enqueueUniqueWork() method.
         Pass in the plant name concatenated with the duration,
        use REPLACE as the ExistingWorkPolicy, and the work request object.
        Note: Passing in the plant name, concatenated with the duration,
         lets you set multiple reminders per plant.
         You can schedule one reminder in 5 seconds for a Peony
         and another reminder in 1 day for a Peony.
         If you only pass in the plant name, when you schedule the second reminder for a Peony,
         it replaces the previously scheduled reminder for the Peony.
        */
        workManager.enqueueUniqueWork(
            plantName + duration,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}
