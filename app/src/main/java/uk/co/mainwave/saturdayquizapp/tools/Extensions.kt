package uk.co.mainwave.saturdayquizapp.tools

fun Float.toPrettyString(): String = toInt().toString() + if (this % 1f == 0.5f) "½" else ""
