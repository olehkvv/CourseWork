package ua.olehkv.coursework.utils

import ua.olehkv.coursework.model.AdFilter
import ua.olehkv.coursework.model.Advertisement

object FilterManager {
    fun createFilter(ad: Advertisement) : AdFilter{
        return AdFilter(
            time = ad.time,
            cat_time = "${ad.category}_${ad.time}",
            cat_country_withSent_time = "${ad.category}_${ad.country}_${ad.withSend}_${ad.time}",
            cat_country_city_withSent_time = "${ad.category}_${ad.country}_${ad.city}_${ad.withSend}_${ad.time}",
            cat_country_city_index_withSent_time = "${ad.category}_${ad.country}_${ad.city}_${ad.index}_${ad.withSend}_${ad.time}",
            cat_index_withSent_time = "${ad.category}_${ad.index}_${ad.withSend}_${ad.time}",
            cat_withSend_time = "${ad.category}_${ad.withSend}_${ad.time}",

            country_withSent_time = "${ad.country}_${ad.withSend}_${ad.time}",
            country_city_withSent_time = "${ad.country}_${ad.city}_${ad.withSend}_${ad.time}",
            country_city_index_withSent_time = "${ad.country}_${ad.city}_${ad.index}_${ad.withSend}_${ad.time}",
            index_withSent_time = "${ad.index}_${ad.withSend}_${ad.time}",
            withSent_time = "${ad.withSend}_${ad.time}"
        )
    }
}