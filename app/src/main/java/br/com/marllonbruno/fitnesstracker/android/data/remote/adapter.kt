package br.com.marllonbruno.fitnesstracker.android.data.remote

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Este adaptador ensina o Moshi a converter entre o objeto LocalDate
 * e uma String no formato padrão ISO (ex: "2025-08-12").
 */
class LocalDateAdapter {

    @ToJson
    fun toJson(date: LocalDate): String {
        // Converte o objeto LocalDate para uma String
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    @FromJson
    fun fromJson(dateString: String): LocalDate {
        // Converte a String do JSON de volta para um objeto LocalDate
        return LocalDate.parse(dateString)
    }
}