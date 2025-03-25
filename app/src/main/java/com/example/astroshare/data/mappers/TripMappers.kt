package com.example.astroshare.data.mappers

import com.example.astroshare.data.local.db.entity.TripEntity
import com.example.astroshare.data.model.Trip

object TripMappers {
    fun Trip.toEntity(): TripEntity {
        return TripEntity(
            id = this.id,
            ownerId = this.ownerId,
            title = this.title,
            content = this.content,
            imageUrl = this.imageUrl,
            timestamp = this.timestamp,
            locationName = this.locationName,
            locationDetails = this.locationDetails
        )
    }

    fun TripEntity.toModel(): Trip {
        return Trip(
            id = this.id,
            ownerId = this.ownerId,
            title = this.title,
            content = this.content,
            imageUrl = this.imageUrl,
            timestamp = this.timestamp,
            locationName = this.locationName,
            locationDetails = this.locationDetails
        )
    }
}