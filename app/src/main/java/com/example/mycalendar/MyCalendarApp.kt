package com.example.mycalendar

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Аннотация говорит Hilt сгенерировать компоненты для всего приложения
@HiltAndroidApp
class MyCalendarApp : Application()