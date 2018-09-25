package com.arctouch.codechallenge.presenter

import com.arctouch.codechallenge.model.Movie

interface HomeViewInterface {

    //    fun showToast(s: String)
    fun displayMovies(movieResponse: MutableList<Movie>)
//    fun displayError(s: String)
}