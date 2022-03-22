package com.example.activityworld.network

enum class RequestType(val request: String) {
    LOGIN_USER("loginUtente"),
    REGISTER_USER("registrazioneUtente"),
    GET_FIELDS_WITH_TYPE("getListaCampi"),
    GET_AVAILABILITIES_WITH_FIELD_AND_DATE("getListaDisponibilitaFiltered"),
    INSERT_FIELD("aggiungiCampo"),
    INSERT_AVAILABILITY("aggiungiDisponibilita"),
    INSERT_BOOKING("aggiungiPrenotazione"),
    INSERT_PARTICIPANTS("aggiungiPartecipante"),
    INSERT_LINK("aggiungiAttribuzione")
}