package ua.olehkv.coursework.model

import java.io.Serializable

data class Advertisement(
    val country: String? = null,
    val city: String? = null,
    val tel: String? = null,
    val email: String? = null,
    val index: String? = null,
    val withSend: String? = null,
    val category: String? = null,
    val title: String? = null,
    val price: String? = null,
    val description: String? = null,
    val mainImage: String? = null,
    val image2: String? = null,
    val image3: String? = null,
    val key: String? = null,
    val uid: String? = null,
    val time: String = "0",
    var isFav: Boolean = false,
    var favCount: String = "0",

    var viewsCount: String = "0",
    var emailsCount: String = "0",
    var callsCount: String = "0",

): Serializable
