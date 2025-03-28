package com.example.astroshare.data.model

data class BodiesEventsResponse(
    val data: BodiesEventsData
)

data class BodiesEventsData(
    val dates: BodiesEventsDates,
    val observer: BodiesEventsObserver,
    val table: BodiesEventsTable
)

data class BodiesEventsDates(
    val from: String,
    val to: String
)

data class BodiesEventsObserver(
    val location: BodiesEventsLocation
)

data class BodiesEventsLocation(
    val longitude: Double,
    val latitude: Double,
    val elevation: Double
)

/** The 'table' object which has 'header' and 'rows' */
data class BodiesEventsTable(
    val header: List<String>,
    val rows: List<BodiesEventsRow>
)

data class BodiesEventsRow(
    val entry: BodyEntry,
    val cells: List<BodyCells>
)

data class BodyEntry(
    val id: String,
    val name: String
)

/**
 * Updated com.example.astroshare.data.model.BodyCells data model to match the sample JSON.
 * It includes a type, eventHighlights, rise, set, and extraInfo.
 */
data class BodyCells(
    val type: String,
    val eventHighlights: EventHighlights?,
    val rise: String,
    val set: String,
    val extraInfo: ExtraInfo?
)

/** Represents event highlight times and altitudes */
data class EventHighlights(
    val partialStart: EventTime?,
    val totalStart: EventTime?,
    val peak: EventTime?,
    val totalEnd: EventTime?,
    val partialEnd: EventTime?
)

/** Represents a single event time with altitude */
data class EventTime(
    val date: String,
    val altitude: Double
)

/** Extra info for the event (e.g., obscuration) */
data class ExtraInfo(
    val obscuration: Double
)
