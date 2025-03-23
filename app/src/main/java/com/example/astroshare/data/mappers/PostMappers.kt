package com.example.astroshare.data.mappers

import com.example.astroshare.data.local.db.entity.PostEntity
import com.example.astroshare.data.model.Post

object PostMappers {

    fun PostEntity.toModel(): Post = Post(
        id = this.id,
        ownerId = this.ownerId,
        title = this.title,
        content = this.content,
        imageUrl = this.imageUrl,
        timestamp = this.timestamp
    )

    fun Post.toEntity(): PostEntity = PostEntity(
        id = this.id,
        ownerId = this.ownerId,
        title = this.title,
        content = this.content,
        imageUrl = this.imageUrl,
        timestamp = this.timestamp
    )
}