package com.example.birthday.worker

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.birthday.BirthdayApplication
import com.example.birthday.BirthdayViewModel
import com.example.birthday.R
import com.example.birthday.data.Birthday
import com.example.birthday.ui.MainActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.prefs.Preferences

class BirthdayNotificationWorker(context: Context, params: WorkerParameters) :
    Worker(context, params) {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun doWork(): Result {

        val intent = Intent(
            applicationContext,
            MainActivity::class.java
        ).apply {  //flags are probably not needed
            //flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val birthdays = inputData.getStringArray(birthdays)
        val dates = inputData.getIntArray(dates)

        val calendar: Calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentMonth = calendar.get(Calendar.MONTH)
        val comparableDate =
            ((currentMonth + 1) * 30) + currentDay //has a date representing the current date that is comparable against the others

        val pendingIntent: PendingIntent = PendingIntent
            .getActivity(applicationContext, 0, intent, 0)

        for (i in birthdays!!.indices) {
            if (dates!![i] == comparableDate) {

                val builder =
                    NotificationCompat.Builder(
                        applicationContext,
                        BirthdayApplication.CHANNEL_ID
                    )
                        .setSmallIcon(R.drawable.ic_baseline_cake_24)
                        .setContentTitle("Happy Birthday, ${birthdays[i]}!")
                        .setContentText("It's time to wish them a happy birthday!")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)

                with(NotificationManagerCompat.from(applicationContext)) {
                    notify(birthdays[i].hashCode() + dates[i].hashCode(), builder.build())
                    //using hashCode() will ensure that different notification will be sent depending solely on the
                    //name in the entry. in case there are multiple people with the same name (i.e, a 'Mary' on January 20th
                    //and a 'Mary' on December 4th) /birthday, we add the hashcodes of both the date and the name, so that the notifications
                    //for one birthday does not encroach on the birthday of another
                }
            }
        }

        return Result.success()
    }

    companion object {
        const val birthdays = "BIRTHDAYS"
        const val dates = "DATES"
    }
}
