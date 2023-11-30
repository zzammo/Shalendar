package com.ddmyb.shalendar.util

import android.content.ContentResolver
import android.provider.CalendarContract
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object CalendarProvider {

    private val CALENDAR_PROJECTION: Array<String> = arrayOf(
        CalendarContract.Calendars._ID,
        CalendarContract.Calendars.NAME,                    // 1
        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,   // 2
    )

    private val EVENTS_PROJECTION: Array<String> = arrayOf(
        CalendarContract.Events.CALENDAR_ID,
        CalendarContract.Events.TITLE,
        CalendarContract.Events.EVENT_LOCATION,
        CalendarContract.Events.DTSTART,
        CalendarContract.Events.DTEND,
        CalendarContract.Events.ALL_DAY,
        CalendarContract.Events.DESCRIPTION,
        CalendarContract.Events.DISPLAY_COLOR
    )

    // The indices for the projection array above.
    private const val CALENDAR_ID_INDEX: Int = 0
    private const val CALENDAR_NAME_INDEX: Int = 1
    private const val CALENDAR_DISPLAY_NAME_INDEX: Int = 2

    private const val EVENTS_ID_INDEX: Int = 0
    private const val EVENTS_TITLE_INDEX: Int = 1
    private const val EVENTS_LOCATION_INDEX: Int = 2
    private const val EVENTS_START_INDEX: Int = 3
    private const val EVENTS_END_INDEX: Int = 4
    private const val EVENTS_ALL_DAY_INDEX: Int = 5
    private const val EVENTS_DESCRIPTION_INDEX: Int = 6
    private const val EVENTS_COLOR_INDEX: Int = 7

    private val logger = Logger("CalendarProvider", true)

    fun getCalendars(contentResolver: ContentResolver, idList: List<Int>, afterEach: (CalendarProvide) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            for (id in idList) {
                getEvents(contentResolver, id, afterEach)
            }
        }
    }

    fun getCalendarList(contentResolver: ContentResolver, after: (Map<Int, String>) -> Unit) {
        val uri = CalendarContract.Calendars.CONTENT_URI
        val selection = ""
        val selectionArgs = emptyArray<String>()
        val cur = contentResolver.query(
            uri,
            CALENDAR_PROJECTION,
            selection, selectionArgs,
            null,
        )

        logger.logD("$cur")
        CoroutineScope(Dispatchers.IO).launch {
            val idMap = mutableMapOf<Int, String>()

            while (cur?.moveToNext() == true) {
                val id = cur.getInt(CALENDAR_ID_INDEX)
                val name = cur.getString(CALENDAR_NAME_INDEX)
                val displayName = cur.getString(CALENDAR_DISPLAY_NAME_INDEX)

                logger.logD("$id, $name, $displayName")

                if (name == "Samsung Calendar") {
                    idMap[id] = displayName
                }
                else if(name.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"))) {
                    idMap[id] = "Google Calendar($displayName)"
                }

            }
            cur?.close()

            withContext(Dispatchers.Main) {
                after(idMap)
            }
        }

    }

    suspend fun getEvents(
        contentResolver: ContentResolver,
        targetId: Int,
        afterEach: (CalendarProvide) -> Unit = {},
        afterEnd: () -> Unit = {}) {
        val uri = CalendarContract.Events.CONTENT_URI
        val selection = ""
        val selectionArgs = emptyArray<String>()
        val cur = contentResolver.query(
            uri,
            EVENTS_PROJECTION,
            selection, selectionArgs,
            null,
        )

        logger.logD("$cur")
        while (cur?.moveToNext() == true) {
            val id = cur.getInt(EVENTS_ID_INDEX)
            val title = cur.getString(EVENTS_TITLE_INDEX)
            val location = cur.getString(EVENTS_LOCATION_INDEX)
            val start = cur.getLong(EVENTS_START_INDEX)
            val end = cur.getLong(EVENTS_END_INDEX)
            val allDay = cur.getInt(EVENTS_ALL_DAY_INDEX)
            val description = cur.getString(EVENTS_DESCRIPTION_INDEX)
            val color = cur.getInt(EVENTS_COLOR_INDEX)

            val data = CalendarProvide(id.toString(), title, location, start, end, allDay, description, color)

            logger.logD("$data")

            if (id == targetId) {
                withContext(Dispatchers.Main) {
                    afterEach(data)
                }
            }

        }

        withContext(Dispatchers.Main) {
            afterEnd()
        }

        cur?.close()

    }

}