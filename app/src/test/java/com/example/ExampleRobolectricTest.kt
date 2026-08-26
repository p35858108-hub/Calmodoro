package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.timer.PomodoroMode
import com.example.timer.PomodoroState
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Calmodoro", appName)
    }

    @Test
    fun `pomodoro state formats time correctly`() {
        val state = PomodoroState(
            mode = PomodoroMode.FOCUS,
            remainingSeconds = 1500L,
            totalDurationSeconds = 1500L
        )
        assertEquals("25:00", state.formattedTime)
        assertEquals(0f, state.progress, 0.001f)
    }

    @Test
    fun `pomodoro state calculates progress correctly`() {
        val state = PomodoroState(
            mode = PomodoroMode.FOCUS,
            remainingSeconds = 750L,
            totalDurationSeconds = 1500L
        )
        assertEquals("12:30", state.formattedTime)
        assertEquals(0.5f, state.progress, 0.001f)
    }
}
