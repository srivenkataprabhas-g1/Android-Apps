package com.prabhas.mycity.data

import com.prabhas.mycity.R
import com.prabhas.mycity.model.Category
import com.prabhas.mycity.model.Recommendation

object LocalDataProvider {
    val recommendations = listOf(
        // Coffee Shops
        Recommendation(
            id = 1,
            nameRes = R.string.coffee_1_name,
            descriptionRes = R.string.coffee_1_desc,
            addressRes = R.string.coffee_1_address,
            imageRes = R.drawable.roasterycoffee,
            category = Category.CoffeeShops
        ),
        Recommendation(
            id = 2,
            nameRes = R.string.coffee_2_name,
            descriptionRes = R.string.coffee_2_desc,
            addressRes = R.string.coffee_2_address,
            imageRes = R.drawable.autumncafe,
            category = Category.CoffeeShops
        ),
        Recommendation(
            id = 3,
            nameRes = R.string.coffee_3_name,
            descriptionRes = R.string.coffee_3_desc,
            addressRes = R.string.coffee_3_address,
            imageRes = R.drawable.theorycafe,
            category = Category.CoffeeShops
        ),
        Recommendation(
            id = 4,
            nameRes = R.string.coffee_4_name,
            descriptionRes = R.string.coffee_4_desc,
            addressRes = R.string.coffee_4_address,
            imageRes = R.drawable.concu,
            category = Category.CoffeeShops
        ),
        Recommendation(
            id = 5,
            nameRes = R.string.coffee_5_name,
            descriptionRes = R.string.coffee_5_desc,
            addressRes = R.string.coffee_5_address,
            imageRes = R.drawable.trueblackcoffee,
            category = Category.CoffeeShops
        ),
        // Restaurants
        Recommendation(
            id = 6,
            nameRes = R.string.rest_1_name,
            descriptionRes = R.string.rest_1_desc,
            addressRes = R.string.rest_1_address,
            imageRes = R.drawable.paradise,
            category = Category.Restaurants
        ),
        Recommendation(
            id = 7,
            nameRes = R.string.rest_2_name,
            descriptionRes = R.string.rest_2_desc,
            addressRes = R.string.rest_2_address,
            imageRes = R.drawable.bawarchi,
            category = Category.Restaurants
        ),
        Recommendation(
            id = 8,
            nameRes = R.string.rest_3_name,
            descriptionRes = R.string.rest_3_desc,
            addressRes = R.string.rest_3_address,
            imageRes = R.drawable.chutneys,
            category = Category.Restaurants
        ),
        Recommendation(
            id = 9,
            nameRes = R.string.rest_4_name,
            descriptionRes = R.string.rest_4_desc,
            addressRes = R.string.rest_4_address,
            imageRes = R.drawable.shahgouse,
            category = Category.Restaurants
        ),
        Recommendation(
            id = 10,
            nameRes = R.string.rest_5_name,
            descriptionRes = R.string.rest_5_desc,
            addressRes = R.string.rest_5_address,
            imageRes = R.drawable.olivebistro,
            category = Category.Restaurants
        ),
        // Parks
        Recommendation(
            id = 11,
            nameRes = R.string.park_1_name,
            descriptionRes = R.string.park_1_desc,
            addressRes = R.string.park_1_address,
            imageRes = R.drawable.lumbinipark,
            category = Category.Parks
        ),
        Recommendation(
            id = 12,
            nameRes = R.string.park_2_name,
            descriptionRes = R.string.park_2_desc,
            addressRes = R.string.park_2_address,
            imageRes = R.drawable.kbrnationalpark,
            category = Category.Parks
        ),
        Recommendation(
            id = 13,
            nameRes = R.string.park_3_name,
            descriptionRes = R.string.park_3_desc,
            addressRes = R.string.park_3_address,
            imageRes = R.drawable.necklaceroad,
            category = Category.Parks
        ),
        Recommendation(
            id = 14,
            nameRes = R.string.park_4_name,
            descriptionRes = R.string.park_4_desc,
            addressRes = R.string.park_4_address,
            imageRes = R.drawable.sanjeevaiahpark,
            category = Category.Parks
        ),
        Recommendation(
            id = 15,
            nameRes = R.string.park_5_name,
            descriptionRes = R.string.park_5_desc,
            addressRes = R.string.park_5_address,
            imageRes = R.drawable.botanicalgarden,
            category = Category.Parks
        ),
        // Zoos
        Recommendation(
            id = 16,
            nameRes = R.string.park_6_name,
            descriptionRes = R.string.park_6_desc,
            addressRes = R.string.park_6_address,
            imageRes = R.drawable.nehruzoological,
            category = Category.Zoos
        ),
        // Museums
        Recommendation(
            id = 17,
            nameRes = R.string.museum_1_name,
            descriptionRes = R.string.museum_1_desc,
            addressRes = R.string.museum_1_address,
            imageRes = R.drawable.salarjung,
            category = Category.Museums
        ),
        Recommendation(
            id = 18,
            nameRes = R.string.museum_2_name,
            descriptionRes = R.string.museum_2_desc,
            addressRes = R.string.museum_2_address,
            imageRes = R.drawable.chowmalla,
            category = Category.Museums
        ),
        Recommendation(
            id = 19,
            nameRes = R.string.museum_3_name,
            descriptionRes = R.string.museum_3_desc,
            addressRes = R.string.museum_3_address,
            imageRes = R.drawable.birlasciencecentre,
            category = Category.Museums
        ),
        Recommendation(
            id = 20,
            nameRes = R.string.museum_4_name,
            descriptionRes = R.string.museum_4_desc,
            addressRes = R.string.museum_4_address,
            imageRes = R.drawable.telanganastate,
            category = Category.Museums
        ),
        Recommendation(
            id = 21,
            nameRes = R.string.museum_5_name,
            descriptionRes = R.string.museum_5_desc,
            addressRes = R.string.museum_5_address,
            imageRes = R.drawable.nizammuseum,
            category = Category.Museums
        ),
        // Temples
        Recommendation(
            id = 22,
            nameRes = R.string.temple_1_name,
            descriptionRes = R.string.temple_1_desc,
            addressRes = R.string.temple_1_address,
            imageRes = R.drawable.pedamma,
            category = Category.Temples
        ),
        Recommendation(
            id = 23,
            nameRes = R.string.temple_2_name,
            descriptionRes = R.string.temple_2_desc,
            addressRes = R.string.temple_2_address,
            imageRes = R.drawable.chilkur,
            category = Category.Temples
        ),
        Recommendation(
            id = 25,
            nameRes = R.string.temple_4_name,
            descriptionRes = R.string.temple_4_desc,
            addressRes = R.string.temple_4_address,
            imageRes = R.drawable.iskcon,
            category = Category.Temples
        ),
        Recommendation(
            id = 26,
            nameRes = R.string.temple_5_name,
            descriptionRes = R.string.temple_5_desc,
            addressRes = R.string.temple_5_address,
            imageRes = R.drawable.ujjainmahakalitemple,
            category = Category.Temples
        )
    )

    val categories = listOf(
        Category.CoffeeShops,
        Category.Restaurants,
        Category.Parks,
        Category.Museums,
        Category.Temples,
        Category.Zoos
    )
}
