package com.intelliavant.mytimetracker.utils


fun formatTime(milliseconds: Long): String {
    val secs = milliseconds / 1000;
    val hours = (secs / 3600).toString().padStart(2, '0');
    val minutes = ((secs % 3600) / 60).toString().padStart(2, '0');
    val seconds = (secs % 60).toString().padStart(2, '0');

    return "$hours:$minutes:$seconds";
}


