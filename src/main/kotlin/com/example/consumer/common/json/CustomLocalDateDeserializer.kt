package com.example.consumer.common.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CustomLocalDateDeserializer : StdDeserializer<LocalDate>(LocalDate::class.java) {
    @Throws(IOException::class)
    override fun deserialize(parser: JsonParser, context: DeserializationContext): LocalDate {
        return LocalDate.parse(parser.readValueAs(String::class.java), formatter)
    }

    companion object {
        private const val serialVersionUID = 1L
        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }
}