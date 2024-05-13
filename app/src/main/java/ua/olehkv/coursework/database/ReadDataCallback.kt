package ua.olehkv.coursework.database

import ua.olehkv.coursework.models.Advertisement

interface ReadDataCallback {
    fun readData(list: ArrayList<Advertisement>)
}