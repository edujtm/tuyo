package me.edujtm.tuyo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val itemId: String,
    val prevKey: String?,
    val nextKey: String?
)
