package com.arctouch.codechallenge.api

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiClient {

    fun create(): TmdbApi {

        val retrofit = Retrofit.Builder()
                .baseUrl(TmdbApi.URL)
                .addCallAdapterFactory(
                        RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        return retrofit.create(TmdbApi::class.java)
    }

}