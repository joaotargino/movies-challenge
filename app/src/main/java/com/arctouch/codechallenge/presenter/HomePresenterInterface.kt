package com.arctouch.codechallenge.presenter

interface HomePresenterInterface {

    fun getGenres(page: Long)
    fun getMovies(page: Long)
    fun searchMovies(movie: String)

}