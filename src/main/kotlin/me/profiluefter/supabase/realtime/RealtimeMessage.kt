package me.profiluefter.supabase.realtime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ColumnDescription(
    val name: String,
    val type: String
)

@Serializable
data class RealtimeInsertMessage<T>(
    val columns: List<ColumnDescription>,
    @SerialName("commit_timestamp")
    val commitTimestamp: String,
    val errors: List<String>?, // TODO: check what this actually is
    val record: T
)

@Serializable
data class RealtimeUpdateMessage<T>(
    val columns: List<ColumnDescription>,
    @SerialName("commit_timestamp")
    val commitTimestamp: String,
    val errors: List<String>?, // TODO: check what this actually is
    val record: T,
    @SerialName("old_record")
    val oldRecord: T
)

@Serializable
data class RealtimeDeleteMessage<T>(
    val columns: List<ColumnDescription>,
    @SerialName("commit_timestamp")
    val commitTimestamp: String,
    val errors: List<String>?, // TODO: check what this actually is
    @SerialName("old_record")
    val oldRecord: T
)