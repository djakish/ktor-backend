package com.example.plugins

import org.jetbrains.exposed.sql.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureDatabases() {
    val fileName  = "locations.db"
    val file = File(fileName)
    file.createNewFile()

    val database = Database.connect("jdbc:sqlite:file:${file.absolutePath}", driver = "org.sqlite.JDBC")

    val locationService = LocationService(database)
    routing {
        post("/locations") {
            val location = call.receive<Location>()
            val id = locationService.create(location)
            call.respond(HttpStatusCode.Created, id)
        }
    }
}
