@file:Suppress("RemoveExplicitTypeArguments")

package com.android_academy.covid_19.di.module

import com.android_academy.covid_19.db.CodeOrangeDB
import com.android_academy.covid_19.db.dao.CollisionLocationsDao
import com.android_academy.covid_19.db.dao.InfectionLocationsDao
import com.android_academy.covid_19.db.dao.UserLocationsDao
import com.android_academy.covid_19.db.dao.UserMetaDataDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbModule = module {

    single<CodeOrangeDB> {
        CodeOrangeDB.create(androidContext())
    }

    factory<InfectionLocationsDao> {
        get<CodeOrangeDB>().infectionPointsDao()
    }

    factory<UserMetaDataDao> {
        get<CodeOrangeDB>().userMetaDataDao()
    }

    factory<UserLocationsDao> {
        get<CodeOrangeDB>().userLocationsDao()
    }

    factory<CollisionLocationsDao> {
        get<CodeOrangeDB>().collisionLocationsDao()
    }
}
