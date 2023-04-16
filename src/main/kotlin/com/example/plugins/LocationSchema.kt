package com.example.plugins

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction


@Serializable
data class Location(
    val ID: String,
    val timestamp: String,
    val latitude: String,
    val longitude: String,
    val status: String
)


class LocationService (private val database: Database) {
    object Locations: Table() {
        val ID = varchar("id", 255)
        val timestamp = varchar("timestamp", 255)
        val latitude = varchar("latitude", 255)
        val longitude = varchar("longitude", 255)
        val status = varchar("status", 255)

        override val primaryKey = PrimaryKey(ID)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Locations)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(location: Location): String =  dbQuery {
        Locations.insert {
            it[ID] = location.ID
            it[timestamp] = location.timestamp
            it[latitude] = location.latitude
            it[longitude] = location.longitude
            it[status] = location.status
        }[Locations.ID]
    }


}