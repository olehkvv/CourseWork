package ua.olehkv.coursework.model

import java.io.Serializable

data class Advertisement(
    val country: String? = null,
    val city: String? = null,
    val tel: String? = null,
    val index: String? = null,
    val withSend: String? = null,
    val category: String? = null,
    val title: String? = null,
    val price: String? = null,
    val description: String? = null,
    val key: String? = null,
    val uid: String? = null,

    var viewsCount: String = "0",
    var emailsCount: String = "0",
    var callsCount: String = "0"
): Serializable
