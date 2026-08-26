package com.example.timer

enum class PomodoroMode(val label: String, val statusText: String) {
    FOCUS("Estudio", "Enfocado"),
    SHORT_BREAK("Corto", "Descanso Corto"),
    LONG_BREAK("Largo", "Descanso Largo")
}

data class PomodoroState(
    val mode: PomodoroMode = PomodoroMode.FOCUS,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val remainingSeconds: Long = 25 * 60L,
    val totalDurationSeconds: Long = 25 * 60L,
    val activeTaskId: Long? = null,
    val activeTaskTitle: String = "",
    val soundChoice: String = "digital_bell",
    val notificationsEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
) {
    val progress: Float
        get() = if (totalDurationSeconds > 0) {
            (totalDurationSeconds - remainingSeconds).toFloat() / totalDurationSeconds.toFloat()
        } else 0f

    val formattedTime: String
        get() {
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            return String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
        }
}
